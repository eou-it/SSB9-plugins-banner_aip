/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.sql.Sql
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.exceptions.ActionItemExceptionFactory
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.NotFoundException
import net.hedtech.banner.general.communication.population.*
import net.hedtech.banner.general.communication.population.selectionlist.CommunicationPopulationSelectionListService
import net.hedtech.banner.general.scheduler.SchedulerErrorContext
import net.hedtech.banner.general.scheduler.SchedulerJobContext
import net.hedtech.banner.general.scheduler.SchedulerJobReceipt
import net.hedtech.banner.general.scheduler.SchedulerJobService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional

import java.sql.Connection
import java.sql.SQLException

/**
 * ActionItemPost Composite Service is responsible for initiating and processing group posts.
 * Controllers and other client code should generally work through this service for interacting with group send
 * behavior and objects.
 */
@Transactional
class ActionItemPostCompositeService {

    private static final log = Logger.getLogger(ActionItemPostCompositeService.class)
    ActionItemPostService actionItemPostService
    ActionItemPostDetailService actionItemPostDetailService
    CommunicationPopulationSelectionListService communicationPopulationSelectionListService
    CommunicationPopulationCompositeService communicationPopulationCompositeService
    SchedulerJobService schedulerJobService
    def sessionFactory
    def dataSource


    /**
     * Initiate the posting of a actionItems to a set of prospect recipients
     * @param request the post to initiate
     */
    public ActionItemPost sendAsynchronousPostItem( ActionItemPostRequest request ) {
        if (log.isDebugEnabled()) log.debug( "Method sendAsynchronousGroupActionItem reached." );
        if (!request) throw new IllegalArgumentException( "request may not be null!" )

        String jobName = request.getName();
        if(!jobName || jobName.isEmpty()) {
            throw ActionItemExceptionFactory.createNotFoundException( ActionItemPostCompositeService, "@@r1:jobNameInvalid@@" )
        }

        ActionItemPost groupSend = new ActionItemPost();
        groupSend.populationListId = request.getPopulationId()

        // do lookup for population version
        groupSend.postingName = jobName
        groupSend.postingScheduleDateTime = request.scheduledStartDate
        groupSend.populationRegenerateIndicator = request.getRecalculateOnPost()
        groupSend.postingJobId = request.referenceId
        groupSend.postingActionItemGroupId = request.postGroupId
        String bannerUser = SecurityContextHolder.context.authentication.principal.getOracleUserName()

        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )

        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation( population ) > 0)

        boolean useCurrentReplica = (!groupSend.populationRegenerateIndicator || !request.scheduledStartDate)

        if (hasQuery && useCurrentReplica) {
            // this will need to be updated once we allow queries to be added to existing manual only populations
            assignPopulationVersion( groupSend )
            assignPopulationCalculation( groupSend, bannerUser )
        } else if (groupSend.populationRegenerateIndicator) { // scheduled with future replica of population
            // FIXME: put this back in once column is in db
            groupSend.populationVersionId = null
            groupSend.populationCalculationId = null
        } else { // sending now or scheduled with replica of current population
            assert (useCurrentReplica == true)
            assignPopulationVersion( groupSend )
            if (hasQuery) {
                assignPopulationCalculation( groupSend, bannerUser )
            }
        }
        // we don't use parameterValues. remove?
        groupSend.postingParameterValues = null
        groupSend.postingDisplayStartDate = request.displayStartDate
        groupSend.postingDisplayEndDate = request.displayEndDate
        println "CRR: do post"
        groupSend = (ActionItemPost) actionItemPostService.create( groupSend )
        println "CRR: done post"
        // Create the details records.
        // FIXME: constraints, createdBy etc
        request.actionItemIds.each {
            ActionItemPostDetail groupDetail = new ActionItemPostDetail()
            groupDetail.actionItemId = it
            groupDetail.actionItemPostId = groupSend.id
            groupDetail.lastModifiedBy = bannerUser
            groupDetail.lastModified = new Date()
            println "CRR: do post Detail"
            actionItemPostDetailService.create( groupDetail )
            println "CRR: done post Detail"
        }


        if (request.scheduledStartDate) {
            groupSend = schedulePost( groupSend, bannerUser )
        } else {
            if (hasQuery) {
                assert( groupSend.populationCalculationId != null )
            }
            groupSend = schedulePostImmediately( groupSend, bannerUser )
        }

        return groupSend
    }

    private static void assignPopulationCalculation( ActionItemPost groupSend, String bannerUser) {
        CommunicationPopulationCalculation calculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy(groupSend
                .getPopulationListId(), bannerUser)
        if (!calculation || !calculation.status.equals(CommunicationPopulationCalculationStatus.AVAILABLE)) {
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostCompositeService.class,
                    "populationNotCalculatedForUser")
        }
        groupSend.populationCalculationId = calculation.id
    }

    /**
     * Deletes a group send and it's dependent objects. The group send must not bre running otherwise an
     * application exception will be thrown.
     *
     * @param groupSendId the long id of the group send
     */
    public void deletePost( Long groupSendId ) {
        if (log.isDebugEnabled()) {
            log.debug( "deleteGroupSend for id = ${groupSendId}." )
        }

        ActionItemPost groupSend = (ActionItemPost) actionItemPostService.get( groupSendId )
        if (!groupSend) {
            throw ActionItemExceptionFactory.createNotFoundException( groupSendId, ActionItemPost.class )
        }

        if (!groupSend.postingCurrentState.pending && !groupSend.postingCurrentState.terminal) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostCompositeService.class, "cannotDeleteRunningPost" )
        }

        // Grab population calculation if only used for this group send
        CommunicationPopulationCalculation calculation = null
        boolean recalculateOnSend = groupSend.populationRegenerateIndicator
        if (groupSend.populationCalculationId != null) {
            calculation = CommunicationPopulationCalculation.get( groupSend.populationCalculationId )
        }

        //if group send is scheduled
        if(groupSend.postingJobId != null) {
            schedulerJobService.deleteScheduledJob( groupSend.postingJobId, groupSend.postingGroupId )
        } else {
            //if Group send is not scheduled then remove job and recipient data
            deleteActionItemJobsByGroupSendId(groupSendId)
            deleteRecipientDataByGroupSendId(groupSendId)
        }
        actionItemPostService.delete( groupSendId )

        // Garbage collect the population calculation
        if (calculation != null) {
            if (recalculateOnSend) {
                communicationPopulationCompositeService.deletePopulationCalculation( groupSend.populationCalculationId )
            } else {
                CommunicationPopulationCalculation latestCalculation =
                        CommunicationPopulationCalculation.findLatestByPopulationVersionIdAndCalculatedBy( calculation.populationVersion.id, calculation.createdBy )
                if (calculation.id != latestCalculation.id) {
                    communicationPopulationCompositeService.deletePopulationCalculation( latestCalculation )
                }
            }
        }
    }

    /**
     * Stops a group send. The group send must be running otherwise an application exception will be thrown.
     * @param groupSendId the long id of the group send
     * @return the updated (stopped) group send
     */
    public ActionItemPost stopPost( Long groupSendId ) {
        if (log.isDebugEnabled()) log.debug( "Stopping group send with id = ${groupSendId}." )

        ActionItemPost groupSend = (ActionItemPost) actionItemPostService.get( groupSendId )

        if (groupSend.postingCurrentState.isTerminal()) {
            log.warn( "Group send with id = ${groupSend.id} has already concluded with execution state ${groupSend.postingCurrentState.toString()}." )
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService.class, "cannotStopConcludedPost" )
        }

        groupSend.markStopped()
        groupSend = savePost( groupSend )

        if (groupSend.postingJobId != null) {
            this.schedulerJobService.deleteScheduledJob( groupSend.postingJobId, groupSend.postingGroupId )
        }

        // fetch any post jobs for this group send and marked as stopped
        stopPendingActionItemJobs( groupSend.id )
        stopPendingPostItems( groupSend.id )

        return groupSend
    }

    /**
     * Marks a group post as complete.
     * @param groupSendId the id of the group post.
     * @return the updated group post
     */
    public ActionItemPost completePost( Long groupSendId ) {
        if (log.isDebugEnabled()) log.debug( "Completing group send with id = " + groupSendId + "." )

        ActionItemPost aGroupSend = (ActionItemPost) actionItemPostService.get( groupSendId )
        aGroupSend.markComplete()
        return savePost( aGroupSend )
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Scheduling service callback job methods
    //////////////////////////////////////////////////////////////////////////////////////

    public ActionItemPost calculatePopulationVersionForPostFired( SchedulerJobContext jobContext ) {
        calculatePopulationVersionForGroupSend( jobContext.parameters )
    }

    public ActionItemPost calculatePopulationVersionForPostFailed( SchedulerErrorContext errorContext ) {
        return scheduledPostCallbackFailed( errorContext )
    }

    public ActionItemPost generatePostItemsFired( SchedulerJobContext jobContext ) {
        return generatePostItems( jobContext.parameters )
    }

    public ActionItemPost generatePostItemsFailed( SchedulerErrorContext errorContext ) {
        return scheduledPostCallbackFailed( errorContext )
    }

    private CommunicationPopulationVersion assignPopulationVersion( ActionItemPost groupSend ) {
        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )
        CommunicationPopulationVersion populationVersion
        if (population.changesPending) {
            populationVersion = communicationPopulationCompositeService.createPopulationVersion( population )
            population.changesPending = false
            communicationPopulationCompositeService.updatePopulation(population)
            // Todo: Should we delete population versions no longer in use by any group sends aside from he one we just created
            // We would need to remove all the associated objects.
        } else {
            populationVersion = CommunicationPopulationVersion.findLatestByPopulationId( groupSend.populationListId )
        }
        assert populationVersion.id
        groupSend.populationVersionId = populationVersion.id
        return populationVersion
    }

    private ActionItemPost scheduledPostCallbackFailed( SchedulerErrorContext errorContext ) {
        Long groupSendId = errorContext.jobContext.getParameter("groupSendId") as Long
        if (log.isDebugEnabled()) {
            log.debug("${errorContext.jobContext.errorHandle} called for groupSendId = ${groupSendId} with message = ${errorContext?.cause?.message}")
        }

        ActionItemPost groupSend = ActionItemPost.get( groupSendId )
        if (!groupSend) {
            throw new ApplicationException("groupSend", new NotFoundException())
        }

        groupSend.setPostingCurrentState(ActionItemPostExecutionState.Error)
        if (errorContext.cause) {
            groupSend.postingErrorCode = ActionItemErrorCode.UNKNOWN_ERROR
            groupSend.postingErrorText = errorContext.cause.message
        } else {
            groupSend.postingErrorCode = ActionItemErrorCode.UNKNOWN_ERROR
        }
        groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
        return groupSend
    }

    /**
     * This method is called by the scheduler to regenerate a population list specifically for the group send
     * and change the state of the group send to next state.
     */
    private ActionItemPost calculatePopulationVersionForGroupSend( Map parameters ) {
        Long groupSendId = parameters.get( "groupSendId" ) as Long
        assert( groupSendId )
        if (log.isDebugEnabled()) {
            log.debug( "Calling calculatePopulationVersionForPost for groupSendId = ${groupSendId}.")
        }

        ActionItemPost groupSend = ActionItemPost.get( groupSendId )
        if (!groupSend) {
            throw new ApplicationException("groupSend", new NotFoundException())
        }

        if(!groupSend.postingCurrentState.isTerminal()) {
            try {
                boolean shouldUpdateGroupSend = false
                CommunicationPopulationVersion populationVersion
                if (!groupSend.populationVersionId) {
                    populationVersion = assignPopulationVersion( groupSend )
                    shouldUpdateGroupSend = true
                } else {
                    populationVersion = CommunicationPopulationVersion.get( groupSend.populationVersionId )
                }

                if (!populationVersion) {
                    throw new ApplicationException( "populationVersion", new NotFoundException() )
                }

                boolean hasQuery = (CommunicationPopulationVersionQueryAssociation.countByPopulationVersion( populationVersion ) > 0)

                if (!groupSend.populationCalculationId && hasQuery) {
                    groupSend.postingCurrentState = ActionItemPostExecutionState.Calculating
                    CommunicationPopulationCalculation calculation = communicationPopulationCompositeService.calculatePopulationVersionForPost(
                            populationVersion )
                    groupSend.populationCalculationId = calculation.id
                    shouldUpdateGroupSend = true
                }
                if (shouldUpdateGroupSend) {
                    groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
                }
                groupSend = generatePostItemsImpl(groupSend)
            } catch (Throwable t) {
                log.error( t.getMessage() )
                groupSend.refresh()
                groupSend.markError( ActionItemErrorCode.UNKNOWN_ERROR, t.getMessage() )
                groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
            }
        }
        return groupSend
    }

    /**
     * This method is called by the scheduler to create the group send items and move the state of
     * the group send to processing.
     */
    private ActionItemPost generatePostItems( Map parameters ) {
        Long groupSendId = parameters.get( "groupSendId" ) as Long
        assert( groupSendId )

        if (log.isDebugEnabled()) {
            log.debug( "Calling generateGroupSendItems for groupSendId = ${groupSendId}.")
        }
        ActionItemPost groupSend = ActionItemPost.get(groupSendId)
        if (!groupSend) {
            throw new ApplicationException("groupSend", new NotFoundException())
        }

        if(!groupSend.postingCurrentState.isTerminal()) {
            try {
                groupSend = generatePostItemsImpl(groupSend)
            } catch (Throwable t) {
                log.error(t.getMessage())
                groupSend.markError( ActionItemErrorCode.UNKNOWN_ERROR, t.getMessage() )
                groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
            }
        }
        return groupSend
    }

    private ActionItemPost schedulePostImmediately( ActionItemPost groupSend, String bannerUser ) {
        SchedulerJobContext jobContext = new SchedulerJobContext( groupSend.postingJobId != null ? groupSend.postingJobId : UUID.randomUUID().toString
                () )
            .setBannerUser( bannerUser )
            .setMepCode( groupSend.vpdiCode )
            .setJobHandle( "actionItemPostCompositeService", "generatePostItemsFired" )
            .setErrorHandle( "actionItemPostCompositeService", "generatePostItemsFailed" )
            .setParameter( "groupSendId", groupSend.id )

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleNowServiceMethod( jobContext )
        println jobReceipt
        groupSend.markQueued( jobReceipt.jobId, jobReceipt.groupId )
        groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
        return groupSend
    }


    private ActionItemPost schedulePost( ActionItemPost groupSend, String bannerUser ) {
        Date now = new Date(System.currentTimeMillis())
        if (now.after(groupSend.postingScheduleDateTime)) {
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostService.class, "invalidScheduledDate")
        }

        SchedulerJobContext jobContext = new SchedulerJobContext( groupSend.postingJobId )
            .setBannerUser( bannerUser )
            .setMepCode( groupSend.vpdiCode )
            .setScheduledStartDate( groupSend.postingScheduleDateTime )
            .setParameter( "groupSendId", groupSend.id )

        println "crr: jobContext: " + jobContext
        println "CRR regen? " + groupSend.populationRegenerateIndicator
        if(groupSend.populationRegenerateIndicator) {
            jobContext.setJobHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFired" )
                .setErrorHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFailed" )
        } else {
            jobContext.setJobHandle( "actionItemPostCompositeService", "generatePostItemsFired" )
                .setErrorHandle( "actionItemPostCompositeService", "generatePostItemsFailed" )
        }

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleServiceMethod( jobContext )
        println "CRR jobReceipt? " + jobReceipt
        groupSend.markScheduled( jobReceipt.jobId, jobReceipt.groupId )
        println "CRR update ActionItemPost as Scheduled: " + groupSend
        groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
        return groupSend
    }

    private ActionItemPost generatePostItemsImpl( ActionItemPost groupSend ) {
        // We'll created the group send items synchronously for now until we have support for scheduling.
        // The individual group send items will still be processed asynchronously via the framework.
        createPostItems(groupSend)
        groupSend.markProcessing()
        groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
        return groupSend
    }

    private ActionItemPost savePost( ActionItemPost groupSend ) {
        //TODO: Figure out why ServiceBase.update is not working with this domain.
        return groupSend.save( flush:true ) //update( groupSend )
    }

    /**
     * Removes all actionItem job records referenced by a group send id.
     *
     * @param groupSendId the long id of the group send.
     */
    private void deleteActionItemJobsByGroupSendId( Long groupSendId ) {
        if (log.isDebugEnabled()) {
            log.debug( "Attempting to delete all actionItem jobs referenced by group send id = ${groupSendId}.")
        }
        Sql sql = new Sql(sessionFactory.getCurrentSession().connection())
        try {
            int rows = sql.executeUpdate("DELETE FROM gcbajob a WHERE EXISTS (SELECT b.gcraiim_surrogate_id FROM gcraiim b, gcbapst c WHERE a" +
                    ".gcbajob_aiim_reference_id = b.gcraiim_reference_id AND b.gcraiim_gcbapst_id = c.gcbapst_surrogate_id AND c" +
                    ".gcbapst_surrogate_id" +
                    " = ?)",
                    [ groupSendId ] )
            if (log.isDebugEnabled()) {
                log.debug( "Deleting ${rows} actionItem jobs referenced by group send id = ${groupSendId}.")
            }
        } catch (Exception e) {
            log.error( e )
            throw e
        } finally {
            sql?.close()
        }
    }


    /**
     * Removes all recipient data records referenced by a group send id.
     *
     * @param groupSendId the long id of the group send.
     */
    private void deleteRecipientDataByGroupSendId( Long groupSendId ) {
        if (log.isDebugEnabled()) {
            log.debug( "Attempting to delete all recipient data referenced by group send id = ${groupSendId}.")
        }
        Sql sql = new Sql(sessionFactory.getCurrentSession().connection())
        try {
            int rows = sql.executeUpdate( "DELETE FROM gcbrdat a WHERE EXISTS (SELECT b.gcrgsim_surrogate_id FROM gcrgsim b, gcbgsnd c WHERE a.gcbrdat_reference_id = b.gcrgsim_reference_id AND b.gcrgsim_group_send_id = c.gcbgsnd_surrogate_id AND c.gcbgsnd_surrogate_id = ?)",
                    [ groupSendId ] )
            if (log.isDebugEnabled()) {
                log.debug( "Deleting ${rows} recipient data referenced by group send id = ${groupSendId}.")
            }
        } catch (Exception e) {
            log.error( e )
            throw e
        } finally {
            sql?.close()
        }
    }


    private void stopPendingActionItemJobs( Long groupSendId ) {
        def Sql sql
        try {
            Connection connection = (Connection) sessionFactory.getCurrentSession().connection()
            sql = new Sql( (Connection) sessionFactory.getCurrentSession().connection() )
            sql.executeUpdate( "update GCBAJOB set GCBAJOB_STATUS='STOPPED', GCBAJOB_ACTIVITY_DATE = SYSDATE where " +
                    "GCBAJOB_STATUS in ('PENDING', 'DISPATCHED') and GCBAJOB_REFERENCE_ID in " +
                    "(select GCRAIIM_REFERENCE_ID from GCRAIIM where GCRAIIM_GCBAPST_ID = ${groupSendId} and GCRAIIM_CURRENT_STATE = 'Complete')" )
        } catch (SQLException e) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService, e )
        } catch (Exception e) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService, e )
        } finally {
            sql?.close()
        }
    }

    private void stopPendingPostItems( Long groupSendId ) {
        def Sql sql
        try {
            Connection connection = (Connection) sessionFactory.getCurrentSession().connection()
            sql = new Sql( (Connection) sessionFactory.getCurrentSession().connection() )
            sql.executeUpdate( "update GCRAIIM set GCRAIIM_CURRENT_STATE='Stopped', GCRAIIM_ACTIVITY_DATE = SYSDATE, GCRAIIM_STOP_DATE = SYSDATE " +
                    "where " +
                    "GCRAIIM_CURRENT_STATE in ('Ready') and GCRAIIM_GCBAPST_ID = ${groupSendId}" )
        } catch (SQLException e) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService, e )
        } catch (Exception e) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService, e )
        } finally {
            sql?.close()
        }
    }

    // Taken and modifies from BCM. Use Objects instead of big insert?
    private void createPostItems( ActionItemPost groupSend ) {
        if (log.isDebugEnabled()) log.debug( "Generating group send item records for group send with id = " + groupSend?.id );
        def sql
        try {
            def ctx = ServletContextHolder.servletContext.getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT)
            def sessionFactory = ctx.sessionFactory
            def session = sessionFactory.currentSession
            sql = new Sql(session.connection())

            sql.execute(
            [
                state:ActionItemPostWorkExecutionState.Ready.toString(),
                group_send_key:groupSend.id,
                current_time:new Date().toTimestamp()
            ],
            """
            INSERT INTO gcraiim (gcraiim_gcbapst_id, gcraiim_pidm, gcraiim_creationdatetime
                                            ,gcraiim_current_state, gcraiim_reference_id, gcraiim_user_id, gcraiim_activity_date, 
                                            gcraiim_started_date)
                    select
                        gcbapst_surrogate_id,
                        gcrlent_pidm,
                        :current_time,
                        :state,
                        sys_guid(),
                        gcbapst_user_id,
                        :current_time,
                        :current_time
                    from (
                        select gcrlent_pidm, gcbapst_surrogate_id, gcbapst_user_id
                            from gcrslis, gcrlent, gcbapst, gcrpopc
                            where
                            gcbapst_surrogate_id = :group_send_key
                            and gcrpopc_surrogate_id = gcbapst_popcalc_id
                            and gcrslis_surrogate_id = gcrpopc_slis_id
                            and gcrlent_slis_id = gcrslis_surrogate_id
                        union
                        select gcrlent_pidm, gcbapst_surrogate_id, gcbapst_user_id
                            from gcrslis, gcrlent, gcbapst, gcrpopv
                            where
                            gcbapst_surrogate_id = :group_send_key

                            and gcrslis_surrogate_id = gcrpopv_include_list_id
                            and gcrlent_slis_id = gcrslis_surrogate_id
                    )
            """ )
//                            and gcrpopv_surrogate_id = gcbapst_popversion_id (line 555 when in db)
            if (log.isDebugEnabled()) log.debug( "Created " + sql.updateCount + " group send item records for group send with id = " + groupSend.id )
        } catch (SQLException ae) {
            log.debug "SqlException in INSERT INTO gcraiim ${ae}"
            log.debug ae.stackTrace
            throw ae
        } catch (Exception ae) {
            log.debug "Exception in INSERT INTO gcraiim ${ae}"
            log.debug ae.stackTrace
            throw ae
        } finally {
            sql?.close()
        }
    }
}
