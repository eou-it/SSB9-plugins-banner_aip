/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.sql.Sql
import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.exceptions.ActionItemExceptionFactory
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.exceptions.NotFoundException
import net.hedtech.banner.general.communication.population.*
import net.hedtech.banner.general.scheduler.SchedulerErrorContext
import net.hedtech.banner.general.scheduler.SchedulerJobContext
import net.hedtech.banner.general.scheduler.SchedulerJobReceipt
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
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

    private static final LOGGER = Logger.getLogger( this.class )

    def actionItemPostService
    def actionItemProcessingCommonService
    def actionItemPostDetailService

    def communicationPopulationCompositeService

    def schedulerJobService

    def sessionFactory

    def springSecurityService

    def actionItemService

    def actionItemGroupService

    /**
     * Initiate the posting of a actionItems to a set of prospect recipients
     * @param requestMap the post to initiate
     */
    def sendAsynchronousPostItem( requestMap ) {
        LoggerUtility.debug( LOGGER, "Method sendAsynchronousGroupActionItem reached." )
        actionItemPostService.preCreateValidation( requestMap )
        def user = springSecurityService.getAuthentication()?.user
        def success = false
        ActionItemPost groupSend = getActionPostInstance( requestMap, user )
        validateDates( groupSend, requestMap.scheduled )
        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation( population ) > 0)
        boolean useCurrentReplica = (!groupSend.populationRegenerateIndicator || !requestMap.scheduledStartDate)
        if (hasQuery && useCurrentReplica) {
            // this will need to be updated once we allow queries to be added to existing manual only populations
            assignPopulationVersion( groupSend )
            assignPopulationCalculation( groupSend, user.oracleUserName )
        } else if (groupSend.populationRegenerateIndicator) { // scheduled with future replica of population
            groupSend.populationVersionId = null
            groupSend.populationCalculationId = null
        } else { // sending now or scheduled with replica of current population
            assert (useCurrentReplica == true)
            assignPopulationVersion( groupSend )
            if (hasQuery) {
                assignPopulationCalculation( groupSend, user.oracleUserName )
            }
        }
        // we don't use parameterValues. remove?
        ActionItemPost groupSendSaved = actionItemPostService.create( groupSend )
        // Create the details records.
        requestMap.actionItemIds.each {
            addPostingDetail( it, groupSendSaved.id )
            if (requestMap.postNow) {
                markActionItemPosted( it )
            }
        }
        if (requestMap.postNow) {
            markActionItemGroupPosted( groupSendSaved.postingActionItemGroupId )
        }
        if (requestMap.postNow) {
            if (hasQuery) {
                assert (groupSendSaved.populationCalculationId != null)
            }
            groupSendSaved = schedulePostImmediately( groupSendSaved, user.oracleUserName )
        } else if (requestMap.scheduledStartDate) {
            groupSendSaved = schedulePost( groupSendSaved, user.oracleUserName )
        }
        success = true
        [
                success : success,
                savedJob: groupSendSaved
        ]
    }

    /**
     * Creates new Instanace of Action Item Post
     * @param requestMap
     * @param user
     * @return
     */
    private ActionItemPost getActionPostInstance( requestMap, user ) {
        new ActionItemPost(
                populationListId: requestMap.populationId,
                postingActionItemGroupId: requestMap.postGroupId,
                postingName: requestMap.name,
                //postingDisplayStartDate: actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayStartDate ),
                //postingDisplayEndDate: actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayEndDate ),
                //postingScheduleDateTime: requestMap.scheduledStartDate ? actionItemProcessingCommonService.convertToLocaleBasedDate(
                //        requestMap.scheduledStartDate ) : null,
                postingDisplayStartDate: requestMap.displayStartDate,
                postingDisplayEndDate: requestMap.displayEndDate,
                postingScheduleDateTime: requestMap.scheduledStartDate,
                postingCreationDateTime: new Date(),
                populationRegenerateIndicator: false,
                postingDeleteIndicator: false,
                postingCreatorId: user.oracleUserName,
                postingCurrentState: requestMap.postNow ? ActionItemPostExecutionState.Queued : (requestMap.scheduled ? ActionItemPostExecutionState.Scheduled : ActionItemPostExecutionState.New),
                )
    }

    /**
     * Add Posting Details
     * @param actionItemId
     * @param postingId
     * @param user
     * @return
     */
    private addPostingDetail( actionItemId, postingId ) {
        ActionItemPostDetail groupDetail = new ActionItemPostDetail(
                actionItemPostId: postingId,
                actionItemId: actionItemId
        )
        actionItemPostDetailService.create( groupDetail )
    }

    /**
     * Marks Action Item Posted
     * @param actionItemId
     * @param user
     * @return
     */
    private markActionItemPosted( actionItemId ) {
        ActionItem actionItem = actionItemService.get( actionItemId )
        actionItem.postedIndicator = AIPConstants.YES_IND
        actionItemService.update( actionItem )
    }

    /**
     * Marks Action Item Group Posted
     * @param actionItemGroupId
     * @param user
     * @return
     */
    private markActionItemGroupPosted( actionItemGroupId ) {
        ActionItemGroup actionItemGroup = actionItemGroupService.get( actionItemGroupId )
        actionItemGroup.postingInd = AIPConstants.YES_IND
        actionItemGroupService.update( actionItemGroup )
    }

    /**
     * Checks if posting name is already present
     * @param name
     * @return
     */
    private def validateDates( ActionItemPost groupSend, isScheduled ) {
        Date currentDate = actionItemProcessingCommonService.getLocaleBasedCurrentDate()
        if (currentDate.compareTo( groupSend.postingDisplayStartDate ) > 0) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.absolete.display.start.date', [] ) )
        }
        if (groupSend.postingDisplayStartDate.compareTo( groupSend.postingDisplayEndDate ) > 0) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.display.start.date.more.than.display.end.date', [] ) )
        }
        if (isScheduled) {
            Date now = new Date( System.currentTimeMillis() )
            if (now.after( groupSend.postingScheduleDateTime )) {
                throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService.class, "preCreate.validation.display.absolete.schedule.date" )
            }
        }
    }


    private static void assignPopulationCalculation( ActionItemPost groupSend, String bannerUser ) {
        CommunicationPopulationCalculation calculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( groupSend
                                                                                                                                             .getPopulationListId(), bannerUser )
        if (!calculation || !calculation.status.equals( CommunicationPopulationCalculationStatus.AVAILABLE )) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostCompositeService.class,
                                                                         "populationNotCalculatedForUser" )
        }
        groupSend.populationCalculationId = calculation.id
    }

    /**
     * Deletes a group send and it's dependent objects. The group send must not bre running otherwise an
     * application exception will be thrown.
     *
     * @param groupSendId the long id of the group send
     */
    void deletePost( Long groupSendId ) {
        LoggerUtility.debug( LOGGER, "deleteGroupSend for id = ${groupSendId}." )

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
        if (groupSend.postingJobId != null) {
            schedulerJobService.deleteScheduledJob( groupSend.postingJobId, groupSend.postingGroupId )
        } else {
            //if Group send is not scheduled then remove job and recipient data
            deleteActionItemJobsByGroupSendId( groupSendId )
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
    ActionItemPost stopPost( Long groupSendId ) {
        LoggerUtility.debug( LOGGER, "Stopping group send with id = ${groupSendId}." )

        ActionItemPost groupSend = (ActionItemPost) actionItemPostService.get( groupSendId )

        if (groupSend.postingCurrentState.isTerminal()) {
            LoggerUtility.warn( LOGGER, "Group send with id = ${groupSend.id} has already concluded with execution state ${groupSend.postingCurrentState.toString()}." )
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

        groupSend
    }

    /**
     * Marks a group post as complete.
     * @param groupSendId the id of the group post.
     * @return the updated group post
     */
    ActionItemPost completePost( Long groupSendId ) {
        LoggerUtility.debug( LOGGER, "Completing group send with id = " + groupSendId + "." )
        ActionItemPost aGroupSend = (ActionItemPost) actionItemPostService.get( groupSendId )
        aGroupSend.markComplete()
        savePost( aGroupSend )
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Scheduling service callback job methods
    //////////////////////////////////////////////////////////////////////////////////////

    ActionItemPost calculatePopulationVersionForPostFired( SchedulerJobContext jobContext ) {
        calculatePopulationVersionForGroupSend( jobContext.parameters )
    }


    ActionItemPost calculatePopulationVersionForPostFailed( SchedulerErrorContext errorContext ) {
        scheduledPostCallbackFailed( errorContext )
    }


    ActionItemPost generatePostItemsFired( SchedulerJobContext jobContext ) {
        generatePostItems( jobContext.parameters )
    }


    ActionItemPost generatePostItemsFailed( SchedulerErrorContext errorContext ) {
        scheduledPostCallbackFailed( errorContext )
    }


    private CommunicationPopulationVersion assignPopulationVersion( ActionItemPost groupSend ) {
        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )
        CommunicationPopulationVersion populationVersion
        if (population.changesPending) {
            populationVersion = communicationPopulationCompositeService.createPopulationVersion( population )
            population.changesPending = false
            communicationPopulationCompositeService.updatePopulation( population )
            // Todo: Should we delete population versions no longer in use by any group sends aside from he one we just created
            // We would need to remove all the associated objects.
        } else {
            populationVersion = CommunicationPopulationVersion.findLatestByPopulationId( groupSend.populationListId )
        }
        assert populationVersion.id
        groupSend.populationVersionId = populationVersion.id
        populationVersion
    }


    private ActionItemPost scheduledPostCallbackFailed( SchedulerErrorContext errorContext ) {
        Long groupSendId = errorContext.jobContext.getParameter( "groupSendId" ) as Long
        LoggerUtility.debug( LOGGER, "${errorContext.jobContext.errorHandle} called for groupSendId = ${groupSendId} with message = ${errorContext?.cause?.message}" )
        ActionItemPost groupSend = ActionItemPost.get( groupSendId )
        if (!groupSend) {
            throw new ApplicationException( "groupSend", new NotFoundException() )
        }

        groupSend.setPostingCurrentState( ActionItemPostExecutionState.Error )
        if (errorContext.cause) {
            groupSend.postingErrorCode = ActionItemErrorCode.UNKNOWN_ERROR
            groupSend.postingErrorText = errorContext.cause.message
        } else {
            groupSend.postingErrorCode = ActionItemErrorCode.UNKNOWN_ERROR
        }
        groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
        groupSend
    }

    /**
     * This method is called by the scheduler to regenerate a population list specifically for the group send
     * and change the state of the group send to next state.
     */
    private ActionItemPost calculatePopulationVersionForGroupSend( Map parameters ) {
        Long groupSendId = parameters.get( "groupSendId" ) as Long
        assert (groupSendId)
        LoggerUtility.debug( LOGGER, "Calling calculatePopulationVersionForPost for groupSendId = ${groupSendId}." )
        ActionItemPost groupSend = ActionItemPost.get( groupSendId )
        if (!groupSend) {
            throw new ApplicationException( "groupSend", new NotFoundException() )
        }

        if (!groupSend.postingCurrentState.isTerminal()) {
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
                groupSend = generatePostItemsImpl( groupSend )
            } catch (Throwable t) {
                LOGGER.error( t.getMessage() )
                groupSend.refresh()
                groupSend.markError( ActionItemErrorCode.UNKNOWN_ERROR, t.getMessage() )
                groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
            }
        }
        groupSend
    }

    /**
     * This method is called by the scheduler to create the group send items and move the state of
     * the group send to processing.
     */
    private ActionItemPost generatePostItems( Map parameters ) {
        Long groupSendId = parameters.get( "groupSendId" ) as Long
        assert (groupSendId)
        LoggerUtility.debug( LOGGER, "Calling generateGroupSendItems for groupSendId = ${groupSendId}." )
        ActionItemPost groupSend = ActionItemPost.get( groupSendId )
        if (!groupSend) {
            throw new ApplicationException( "groupSend", new NotFoundException() )
        }

        if (!groupSend.postingCurrentState.isTerminal()) {
            try {
                groupSend = generatePostItemsImpl( groupSend )
            } catch (Throwable t) {
                LoggerUtility.error( LOGGER, t.getMessage() )
                groupSend.markError( ActionItemErrorCode.UNKNOWN_ERROR, t.getMessage() )
                groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
            }
        }
        groupSend
    }


    private ActionItemPost schedulePostImmediately( ActionItemPost groupSend, String bannerUser ) {
        SchedulerJobContext jobContext = new SchedulerJobContext(
                groupSend.postingJobId != null ? groupSend.postingJobId : UUID.randomUUID().toString() )
                .setBannerUser( bannerUser )
                .setMepCode( groupSend.vpdiCode )
                .setJobHandle( "actionItemPostCompositeService", "generatePostItemsFired" )
                .setErrorHandle( "actionItemPostCompositeService", "generatePostItemsFailed" )
                .setParameter( "groupSendId", groupSend.id )

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleNowServiceMethod( jobContext )
        groupSend.markQueued( jobReceipt.jobId, jobReceipt.groupId )
        groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
        groupSend
    }


    private ActionItemPost schedulePost( ActionItemPost groupSend, String bannerUser ) {
        Date now = new Date( System.currentTimeMillis() )
        if (now.after( groupSend.postingScheduleDateTime )) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService.class, "invalidScheduledDate" )
        }

        SchedulerJobContext jobContext = new SchedulerJobContext(
                groupSend.postingJobId != null ? groupSend.postingJobId : UUID.randomUUID().toString() )
                .setBannerUser( bannerUser )
                .setMepCode( groupSend.vpdiCode )
                .setScheduledStartDate( groupSend.postingScheduleDateTime )
                .setParameter( "groupSendId", groupSend.id )

        if (groupSend.populationRegenerateIndicator) {
            jobContext.setJobHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFired" )
                    .setErrorHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFailed" )
        } else {
            jobContext.setJobHandle( "actionItemPostCompositeService", "generatePostItemsFired" )
                    .setErrorHandle( "actionItemPostCompositeService", "generatePostItemsFailed" )
        }

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleServiceMethod( jobContext )
        groupSend.markScheduled( jobReceipt.jobId, jobReceipt.groupId )
        groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
        groupSend
    }


    private ActionItemPost generatePostItemsImpl( ActionItemPost groupSend ) {
        createPostItems( groupSend )
        groupSend.markProcessing()
        groupSend = (ActionItemPost) actionItemPostService.update( groupSend )
        groupSend
    }


    private ActionItemPost savePost( ActionItemPost groupSend ) {
        actionItemPostService.update( groupSend )
    }

    /**
     * Removes all actionItem job records referenced by a group send id.
     *
     * @param groupSendId the long id of the group send.
     */
    private void deleteActionItemJobsByGroupSendId( Long groupSendId ) {
        LoggerUtility.debug( LOGGER, "Attempting to delete all actionItem jobs referenced by group send id = ${groupSendId}." )
        Sql sql = new Sql( sessionFactory.getCurrentSession().connection() )
        try {
            int rows = sql.executeUpdate( "DELETE FROM gcbajob a WHERE EXISTS (SELECT b.gcraiim_surrogate_id FROM gcraiim b, gcbapst c WHERE a" +
                                                  ".gcbajob_aiim_reference_id = b.gcraiim_reference_id AND b.gcraiim_gcbapst_id = c.gcbapst_surrogate_id AND c" +
                                                  ".gcbapst_surrogate_id" +
                                                  " = ?)",
                                          [groupSendId] )
            LoggerUtility.debug( LOGGER, "Deleting ${rows} actionItem jobs referenced by group send id = ${groupSendId}." )
        } catch (Exception e) {
            LOGGER.error( e )
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
        Sql sql
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

    // TODO: Taken and modified from BCM. Use Hibernate Batch Update or a function in the DB instead of big insert?
    private void createPostItems( ActionItemPost groupSend ) {
        LoggerUtility.debug( LOGGER, "Generating group send item records for group send with id = " + groupSend?.id )
        def sql
        try {
            def ctx = ServletContextHolder.servletContext.getAttribute( GrailsApplicationAttributes.APPLICATION_CONTEXT )
            def sessionFactory = ctx.sessionFactory
            def session = sessionFactory.currentSession
            sql = new Sql( session.connection() )

            sql.execute(
                    [
                            state         : ActionItemPostWorkExecutionState.Ready.toString(),
                            group_send_key: groupSend.id,
                            current_time  : new Date().toTimestamp()
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
                            and gcrpopv_surrogate_id = gcbapst_popversion_id
                            and gcrslis_surrogate_id = gcrpopv_include_list_id
                            and gcrlent_slis_id = gcrslis_surrogate_id
                    )
            """ )

            LoggerUtility.debug( LOGGER, "Created " + sql.updateCount + " group send item records for group send with id = " + groupSend.id )
        } catch (SQLException ae) {
            LoggerUtility.debug( LOGGER, "SqlException in INSERT INTO gcraiim ${ae}" )
            LoggerUtility.debug( LOGGER, ae.stackTrace )
            throw ae
        } catch (Exception ae) {
            LoggerUtility.debug( LOGGER, "Exception in INSERT INTO gcraiim ${ae}" )
            LoggerUtility.debug( LOGGER, ae.stackTrace )
            throw ae
        } finally {
            sql?.close()
        }
    }
}
