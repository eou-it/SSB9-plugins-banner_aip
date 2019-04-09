/*******************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost


import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.exceptions.ActionItemExceptionFactory
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.exceptions.NotFoundException
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculationStatus
import net.hedtech.banner.general.communication.population.CommunicationPopulationQueryAssociation
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersion
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersionQueryAssociation
import net.hedtech.banner.general.scheduler.SchedulerErrorContext
import net.hedtech.banner.general.scheduler.SchedulerJobContext
import net.hedtech.banner.general.scheduler.SchedulerJobReceipt
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestContextHolder

/**
 * ActionItemPost Composite Service is responsible for initiating and processing group posts.
 * Controllers and other client code should generally work through this service for interacting with group send
 * behavior and objects.
 */
@Transactional
class ActionItemPostCompositeService {

    private static
    final LOGGER = Logger.getLogger( "net.hedtech.banner.aip.post.grouppost.ActionItemPostCompositeService" )

    def actionItemPostService

    def actionItemProcessingCommonService

    def actionItemPostDetailService

    def grailsApplication

    def transactionManager

    def communicationPopulationCompositeService

    def asynchronousBannerAuthenticationSpoofer

    def schedulerJobService

    def sessionFactory

    def actionItemPostWorkService

    def springSecurityService

    def actionItemService

    def actionItemGroupService

    def actionItemJobService
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
            if (groupSend.populationRegenerateIndicator) {
                groupSend.populationVersionId = null
                groupSend.populationCalculationId = null
            } else {
                assignPopulationVersion(groupSend)
                assignPopulationCalculation(groupSend, user.oracleUserName)
            }
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
        }
        if (requestMap.postNow) {
            groupSendSaved = schedulePostImmediately( groupSendSaved, user.oracleUserName )
        } else if (requestMap.scheduledStartDate) {
            groupSendSaved = schedulePost( groupSendSaved, user.oracleUserName )
        }
        success = true
        LoggerUtility.debug( LOGGER, " Finished Saving Posting ${groupSendSaved}." )
        [
                success : success,
                savedJob: groupSendSaved
        ]
    }

    /**
     * Update the posting of a actionItems to a set of prospect recipients
     * @param requestMap the post to update
     */
    def updateAsynchronousPostItem( requestMap ) {
        actionItemPostService.preCreateValidation( requestMap )
        ActionItemPost groupSend = (ActionItemPost) actionItemPostService.get( (requestMap.postId ?: 0) as long )
        if (!groupSend) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostCompositeService.class, "ActionItemPostNotExist" )
        }
        if (!groupSend.postingCurrentState.pending && !groupSend.postingCurrentState.terminal) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostCompositeService.class, "cannotUpdateRunningPost" )
        }
        def user = springSecurityService.getAuthentication()?.user
        def success = false
        groupSend = updateActionPostInstance( requestMap, user, groupSend )
        validateDates( groupSend, requestMap.scheduled )
        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation( population ) > 0)
        boolean useCurrentReplica = (!groupSend.populationRegenerateIndicator || !requestMap.scheduledStartDate)
        if (hasQuery && useCurrentReplica) {
            // this will need to be updated once we allow queries to be added to existing manual only populations
            if (groupSend.populationRegenerateIndicator) {
                groupSend.populationVersionId = null
                groupSend.populationCalculationId = null
            } else {
                assignPopulationVersion(groupSend)
                assignPopulationCalculation(groupSend, user.oracleUserName)
            }
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
        ActionItemPost groupSendSaved = actionItemPostService.update( groupSend )
        // Create the details records.
        deletePostingDetail( groupSendSaved.id )
        requestMap.actionItemIds.each {
            addPostingDetail( it, groupSendSaved.id )
        }
        if (groupSend.aSyncJobId != null) {
            schedulerJobService.deleteScheduledJob( groupSend.aSyncJobId, groupSend.aSyncGroupId )
        }
        if (requestMap.postNow) {
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
     * Creates new Instance of Action Item Post
     * @param requestMap
     * @param user
     * @return
     */
    ActionItemPost getActionPostInstance( requestMap, user ) {
        Date scheduledStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.scheduledStartDate )
        String scheduledStartTime = requestMap.scheduledStartTime
        String timezoneStringOffset = requestMap.timezoneStringOffset
        Calendar displayDateTimeCalendar = getDisplayDateTimeCalender( requestMap.displayDatetimeZone )
        Calendar scheduledStartDateCalendar = null
        if (!requestMap.postNow && scheduledStartDate && scheduledStartTime) {
            scheduledStartDateCalendar = actionItemProcessingCommonService.getRequestedTimezoneCalendar( scheduledStartDate, scheduledStartTime, timezoneStringOffset )
        }

        new ActionItemPost(
                populationListId: requestMap.populationId,
                postingActionItemGroupId: requestMap.postingActionItemGroupId,
                postingName: requestMap.postingName,
                postingDisplayStartDate: actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayStartDate ),
                postingDisplayEndDate: actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayEndDate ),
                postingScheduleDateTime: scheduledStartDateCalendar ? scheduledStartDateCalendar.getTime() : null,
                postingCreationDateTime: new Date(),
                populationRegenerateIndicator: requestMap.populationRegenerateIndicator,
                postingDeleteIndicator: false,
                postingCreatorId: user.oracleUserName,
                postingCurrentState: requestMap.postNow ? ActionItemPostExecutionState.Queued : (requestMap.scheduled ? ActionItemPostExecutionState.Scheduled : ActionItemPostExecutionState.New),
                postingDisplayDateTime: displayDateTimeCalendar ? displayDateTimeCalendar.getTime() : null,
                postingTimeZone: requestMap.displayDatetimeZone.timeZoneVal

        )

    }

    /**Get Display Date and Time
     *
     * @param userEnteredValue
     * @return
     */
    def getDisplayDateTimeCalender( userEnteredValue ) {
        Date displayDate = actionItemProcessingCommonService.convertToLocaleBasedDate( userEnteredValue.dateVal )
        def  userEnteredHour= userEnteredValue.timeVal.substring( 0, 2 ).toInteger()
        def  userEnteredMinute= userEnteredValue.timeVal.substring( 2 ).toInteger()
        Calendar displayDateTimeCalendar = Calendar.getInstance()
        displayDateTimeCalendar.setTime( displayDate )
        displayDateTimeCalendar.set( java.util.Calendar.HOUR, userEnteredHour )
        displayDateTimeCalendar.set( java.util.Calendar.MINUTE, userEnteredMinute )
        displayDateTimeCalendar.set( java.util.Calendar.SECOND, 0 )
        displayDateTimeCalendar.set( java.util.Calendar.MILLISECOND, 0 )
        displayDateTimeCalendar
    }

    /**
     * update  Instance of Action Item Post
     * @param requestMap
     * @param user
     * @param actionItemPost
     * @return
     */
    ActionItemPost updateActionPostInstance( requestMap, user, actionItemPost ) {
        Date scheduledStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.scheduledStartDate )
        String scheduledStartTime = requestMap.scheduledStartTime
        String timezoneStringOffset = requestMap.timezoneStringOffset
        Calendar displayDateTimeCalendar = getDisplayDateTimeCalender( requestMap.displayDatetimeZone  )
        Calendar scheduledStartDateCalendar = null
        if (!requestMap.postNow && scheduledStartDate && scheduledStartTime) {
            scheduledStartDateCalendar = actionItemProcessingCommonService.getRequestedTimezoneCalendar( scheduledStartDate, scheduledStartTime, timezoneStringOffset )
        }

        actionItemPost.populationListId = requestMap.populationId
        actionItemPost.postingActionItemGroupId = requestMap.postingActionItemGroupId
        actionItemPost.postingName = requestMap.postingName
        actionItemPost.postingDisplayStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayStartDate )
        actionItemPost.postingDisplayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayEndDate )
        actionItemPost.postingScheduleDateTime = scheduledStartDateCalendar ? scheduledStartDateCalendar.getTime() : null
        actionItemPost.postingCreationDateTime = new Date()
        actionItemPost.populationRegenerateIndicator = requestMap.populationRegenerateIndicator
        actionItemPost.postingDeleteIndicator = false
        actionItemPost.postingCreatorId = user.oracleUserName
        actionItemPost.postingCurrentState = requestMap.postNow ? ActionItemPostExecutionState.Queued : (requestMap.scheduled ? ActionItemPostExecutionState.Scheduled : ActionItemPostExecutionState.New)
        actionItemPost.postingDisplayDateTime = displayDateTimeCalendar ? displayDateTimeCalendar.getTime() : null
        actionItemPost.postingTimeZone = requestMap.displayDatetimeZone.timeZoneVal
        actionItemPost

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
     * delete Posting Details
     * @param postingId
     * @param user
     * @return
     */
    private deletePostingDetail( postingId ) {
        List actionItemPostDetailList = actionItemPostDetailService.fetchByActionItemPostId( postingId )
        actionItemPostDetailList.each {
            actionItemPostDetailService.delete( it )
        }
    }

    /**
     * Marks Action Item Posted
     * @param actionItemId
     * @param user
     * @return
     */
    def markActionItemPosted( actionItemId ) {
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
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.obsolete.display.start.date', [] ) )
        }
        if (groupSend.postingDisplayStartDate.compareTo( groupSend.postingDisplayEndDate ) > 0) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.display.start.date.more.than.display.end.date', [] ) )
        }
        if (isScheduled) {
            Date now = new Date( System.currentTimeMillis() )
            if (now.after( groupSend.postingScheduleDateTime )) {
                throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService.class, "preCreate.validation.display.obsolete.schedule.date" )
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
        if (groupSend.aSyncJobId != null) {
            schedulerJobService.deleteScheduledJob( groupSend.aSyncJobId, groupSend.aSyncGroupId )
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
        groupSend = actionItemPostService.update( groupSend )

        if (groupSend.aSyncJobId != null) {
            this.schedulerJobService.deleteScheduledJob( groupSend.aSyncJobId, groupSend.aSyncGroupId )
        }

        // fetch any post jobs for this group send and marked as stopped
        stopPendingAndDispatchedJobs( groupSend.id )
        actionItemPostWorkService.updateStateToStop( groupSend )
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
        actionItemPostService.update( aGroupSend )
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Scheduling service callback job methods for Query Population (leave public)
    //////////////////////////////////////////////////////////////////////////////////////

    public ActionItemPost calculatePopulationVersionForPostFired( SchedulerJobContext jobContext ) {
        markArtifactsAsPosted( jobContext.parameters.get( "groupSendId" ) as Long )
        calculatePopulationVersionForGroupSend( jobContext.parameters )
    }


    public ActionItemPost calculatePopulationVersionForPostFailed( SchedulerErrorContext errorContext ) {
        scheduledPostCallbackFailed( errorContext )
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Scheduling service callback job methods for Manual population (leave public)
    //////////////////////////////////////////////////////////////////////////////////////

    public ActionItemPost generatePopulationVersionForPostFired( SchedulerJobContext jobContext ) {
        markArtifactsAsPosted( jobContext.parameters.get( "groupSendId" ) as Long )
        generatePopulationVersionForGroupSend( jobContext.parameters )
    }

    public ActionItemPost generatePopulationVersionForPostFailed( SchedulerErrorContext errorContext ) {
        scheduledPostCallbackFailed( errorContext )
    }

    /**
     *
     * @param groupSendId
     * @return
     */
    public def markArtifactsAsPosted( groupSendId ) {
        ActionItemPost groupSend = actionItemPostService.get( groupSendId )
        markActionItemGroupPosted( groupSend.postingActionItemGroupId )
        List actionItemsIds = actionItemPostDetailService.fetchByActionItemPostId( groupSendId ).actionItemId
        actionItemsIds?.each {
            markActionItemPosted( it )
        }
    }

    /**
     *
     * @param jobContext
     * @return
     */
    public ActionItemPost generatePostItemsFired( SchedulerJobContext jobContext ) {
        asynchronousBannerAuthenticationSpoofer.setMepContext( sessionFactory.currentSession.connection(), jobContext.parameters.get( "mepCode" ) )
        generatePostItemsFiredImpl( jobContext )
    }

     /**
     *
     * @param jobContext
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    private void generatePostItemsFiredImpl( SchedulerJobContext jobContext ) {
            asynchronousBannerAuthenticationSpoofer.setMepContext( sessionFactory.currentSession.connection(), jobContext.parameters.get( "mepCode" ) )
        markArtifactsAsPosted( jobContext.parameters.get( "groupSendId" ) as Long )
        generatePostItems( jobContext.parameters )
    }

    /**
     *
     * @param errorContext
     * @return
     */
    public ActionItemPost generatePostItemsFailed( SchedulerErrorContext errorContext ) {
        asynchronousBannerAuthenticationSpoofer.setMepContext( sessionFactory.currentSession.connection(), errorContext.jobContext.parameters.get( "mepCode" ) )
        generatePostItemsFailedImpl( errorContext )
    }

    /**
     *
     * @param errorContext
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    private void generatePostItemsFailedImpl( SchedulerErrorContext errorContext ) {
            asynchronousBannerAuthenticationSpoofer.setMepContext( sessionFactory.currentSession.connection(), errorContext.jobContext.parameters.get( "mepCode" ) )
        scheduledPostCallbackFailed( errorContext )
    }


    CommunicationPopulationVersion assignPopulationVersion( ActionItemPost groupSend ) {

        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )
        CommunicationPopulationVersion populationVersion

        if (population.changesPending) {
            populationVersion = communicationPopulationCompositeService.createPopulationVersion( population )
            population.changesPending = false
            communicationPopulationCompositeService.updatePopulation( population )
        } else {
            populationVersion = CommunicationPopulationVersion.findLatestByPopulationId( groupSend.populationListId )

        }
        assert populationVersion.id
        groupSend.populationVersionId = populationVersion.id
        populationVersion
    }


    ActionItemPost scheduledPostCallbackFailed( SchedulerErrorContext errorContext ) {
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
    ActionItemPost calculatePopulationVersionForGroupSend( Map parameters ) {
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
                if (groupSend.populationVersionId) {
                    populationVersion = CommunicationPopulationVersion.get( groupSend.populationVersionId )
                } else {
                    populationVersion = assignPopulationVersion( groupSend )
                    shouldUpdateGroupSend = true
                }
                if (!populationVersion) {
                    throw new ApplicationException( "populationVersion", new NotFoundException() )
                }

                boolean hasQuery = (CommunicationPopulationVersionQueryAssociation.countByPopulationVersion( populationVersion ) > 0)

                if (!groupSend.populationCalculationId && hasQuery) {
                    groupSend.postingCurrentState = ActionItemPostExecutionState.Calculating
                    CommunicationPopulationCalculation calculation = communicationPopulationCompositeService.calculatePopulationVersionForGroupSend(
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
     * This method is called by the scheduler to regenerate a population list specifically for the Manual Population
     *
     * */
    ActionItemPost generatePopulationVersionForGroupSend( Map parameters ) {
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
                populationVersion = assignPopulationVersion( groupSend )
                shouldUpdateGroupSend = true

                if (!populationVersion) {
                    throw new ApplicationException( "populationVersion", new NotFoundException() )
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
    ActionItemPost generatePostItems( Map parameters ) {
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


    ActionItemPost schedulePostImmediately( ActionItemPost groupSend, String bannerUser ) {
        LoggerUtility.debug( LOGGER, " Start creating  jobContext for ${bannerUser}." )
        def mepCode = RequestContextHolder.currentRequestAttributes().request.session.getAttribute( 'mep' )
        SchedulerJobContext jobContext = new SchedulerJobContext(
                groupSend.aSyncJobId != null ? groupSend.aSyncJobId : UUID.randomUUID().toString() )
                .setBannerUser( bannerUser )
                .setMepCode( mepCode )
                .setParameter( "groupSendId", groupSend.id )

        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation( population ) > 0)

        if (hasQuery && groupSend.populationRegenerateIndicator) {
            jobContext.setJobHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFired" )
                    .setErrorHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFailed" )
        }
        else {
            jobContext.setJobHandle( "actionItemPostCompositeService", "generatePostItemsFired" )
                    .setErrorHandle( "actionItemPostCompositeService", "generatePostItemsFailed" )
        }

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleNowServiceMethod( jobContext )
        groupSend.markQueued( jobReceipt.jobId, jobReceipt.groupId )
        LoggerUtility.debug( LOGGER, " Completing marking posting in Queue." )
        actionItemPostService.update( groupSend )
    }

    /**
     * Schedules the action item posting
     * @param groupSend
     * @param bannerUser
     * @return
     */
    ActionItemPost schedulePost( ActionItemPost groupSend, String bannerUser ) {
        Date now = new Date( System.currentTimeMillis() )
        if (now.after( groupSend.postingScheduleDateTime )) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostService.class, "invalidScheduledDate" )
        }
        def mepCode = RequestContextHolder.currentRequestAttributes().request.session.getAttribute( 'mep' )
        LOGGER.debug "Setting mepCod ${mepCode} in JobContext"
        SchedulerJobContext jobContext = new SchedulerJobContext(
                groupSend.aSyncJobId != null ? groupSend.aSyncJobId : UUID.randomUUID().toString() )
                .setBannerUser( bannerUser )
                .setMepCode( mepCode )
                .setScheduledStartDate( groupSend.postingScheduleDateTime )
                .setParameter( "groupSendId", groupSend.id )

        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation( groupSend.populationListId )
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation( population ) > 0)

        if (hasQuery && groupSend.populationRegenerateIndicator) {
            jobContext.setJobHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFired" )
                    .setErrorHandle( "actionItemPostCompositeService", "calculatePopulationVersionForPostFailed" )
        }
        else if(!hasQuery){
            jobContext.setJobHandle( "actionItemPostCompositeService", "generatePopulationVersionForPostFired" )
                    .setErrorHandle( "actionItemPostCompositeService", "generatePopulationVersionForPostFailed" )
        }
        else {
            jobContext.setJobHandle( "actionItemPostCompositeService", "generatePostItemsFired" )
                    .setErrorHandle( "actionItemPostCompositeService", "generatePostItemsFailed" )
        }

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleServiceMethod( jobContext )
        groupSend.markScheduled( jobReceipt.jobId, jobReceipt.groupId )
        actionItemPostService.update( groupSend )
    }

    /**
     *
     * @param groupSend
     * @return
     */
    ActionItemPost generatePostItemsImpl( ActionItemPost groupSend ) {
        createPostItems( groupSend )
        groupSend.markProcessing()
        actionItemPostService.update( groupSend )
    }

    /**
     * Removes all actionItem job records referenced by a group send id.
     *
     * @param groupSendId the long id of the group send.
     */
    void deleteActionItemJobsByGroupSendId( Long groupSendId ) {
        actionItemJobService.deleteJobForAPostingId( groupSendId )
    }


    void stopPendingAndDispatchedJobs( Long groupSendId ) {
        actionItemJobService.stopPendingAndDispatchedJobs( groupSendId )
    }

    /**
     *
     * @param groupSend
     */
    void createPostItems( ActionItemPost groupSend ) {
        LoggerUtility.debug( LOGGER, "Generating group send item records for group send with id = " + groupSend?.id )
        def session = sessionFactory.currentSession
        def timeoutSeconds = (grailsApplication.config.banner?.transactionTimeout instanceof Integer ? (grailsApplication.config.banner?.transactionTimeout) : 300)
        try {
            transactionManager.setDefaultTimeout( timeoutSeconds * 2 )
            List<ActionItemPostSelectionDetailReadOnly> list = session.getNamedQuery( 'ActionItemPostSelectionDetailReadOnly.fetchSelectionIds' )
                    .setLong( 'postingId', groupSend.id )
                    .list()
            list?.each { ActionItemPostSelectionDetailReadOnly it ->
                session.createSQLQuery( """ INSERT INTO gcraiim (gcraiim_gcbapst_id, gcraiim_pidm, gcraiim_creationdatetime
                                                                   ,gcraiim_current_state, gcraiim_reference_id, gcraiim_user_id, gcraiim_activity_date,
                                                                   gcraiim_started_date) values (${groupSend.id}, ${
                    it.actionItemPostSelectionPidm
                }, sysdate, '${ActionItemPostWorkExecutionState.Ready.toString()}' ,'$sysGuId', '${
                    it.postingUserId
                }', sysdate, sysdate ) """ )
                        .executeUpdate()
            }
            LoggerUtility.debug( LOGGER, "Created " + list?.size() + " group send item records for group send with id = " + groupSend.id )
        } finally {
            transactionManager.setDefaultTimeout( timeoutSeconds )
        }
    }
    /**
     * Get system GU id
     * @return
     */
    String getSysGuId() {
        sessionFactory.currentSession.createSQLQuery( ' select RAWTOHEX(sys_guid()) from dual' ).uniqueResult()
    }

}
