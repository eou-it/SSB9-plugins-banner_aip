/*******************************************************************************
 Copyright 2018-2020 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.util.Holders
import groovy.util.logging.Slf4j
import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.common.AIPConstants
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
import org.springframework.transaction.annotation.Propagation
import org.springframework.web.context.request.RequestContextHolder

import java.text.SimpleDateFormat
import grails.gorm.transactions.Transactional


/**
 * ActionItemPost Composite Service is responsible for initiating and processing group posts.
 * Controllers and other client code should generally work through this service for interacting with group send
 * behavior and objects.
 */
@Slf4j
@Transactional
class ActionItemPostCompositeService {


    def actionItemPostService

    def actionItemProcessingCommonService

    def actionItemPostDetailService

    def grailsApplication

    def communicationPopulationCompositeService

    def asynchronousBannerAuthenticationSpoofer

    def schedulerJobService

    def sessionFactory

    def actionItemPostWorkService

    def springSecurityService

    def actionItemService

    def actionItemGroupService

    def actionItemJobService
    def actionItemPostRecurringDetailsService
    /**
     * Initiate the posting of a actionItems to a set of prospect recipients
     * @param requestMap the post to initiate
     */
    def sendAsynchronousPostItem(requestMap) {
        ActionItemPost recurringActionItemPost = requestMap.recurringActionItemPost
        log.debug( "Method sendAsynchronousGroupActionItem reached.")
        if (!recurringActionItemPost){
            actionItemPostService.preCreateValidation(requestMap)}
        def user = springSecurityService.getAuthentication()?.user
        def success = false
        ActionItemPost groupSend = recurringActionItemPost ? recurringActionItemPost : getActionPostInstance(requestMap, user)
        if(!requestMap.isRecurEdit) {
            validateDates(groupSend, requestMap.scheduled)
        }
        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation(groupSend.populationListId)
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation(population) > 0)
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
            assignPopulationVersion(groupSend)
            if (hasQuery) {
                assignPopulationCalculation(groupSend, user.oracleUserName)
            }
        }
        // we don't use parameterValues. remove?
        ActionItemPost groupSendSaved = actionItemPostService.create(groupSend)
        // Create the details records.
        requestMap.actionItemIds.each {
            addPostingDetail(it, groupSendSaved.id)
        }
        if (requestMap.postNow) {
            groupSendSaved = schedulePostImmediately(groupSendSaved, user.oracleUserName)
        } else if (requestMap.scheduledStartDate) {
            groupSendSaved = schedulePost(groupSendSaved, user.oracleUserName,requestMap.isRecurEdit)
        }
        success = true
        log.debug( " Finished Saving Posting ${groupSendSaved}.")
        [
                success : success,
                savedJob: groupSendSaved
        ]
    }

    /**
     * Update the posting of a actionItems to a set of prospect recipients
     * @param requestMap the post to update
     */
    def updateAsynchronousPostItem(requestMap) {
        actionItemPostService.preCreateValidation(requestMap)
        ActionItemPost groupSend = (ActionItemPost) actionItemPostService.get((requestMap.postId ?: 0) as long)
        if (!groupSend) {
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostCompositeService.class, "ActionItemPostNotExist")
        }
        if (!groupSend.postingCurrentState.pending && !groupSend.postingCurrentState.terminal) {
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostCompositeService.class, "cannotUpdateRunningPost")
        }
        def user = springSecurityService.getAuthentication()?.user
        def success = false
        groupSend = updateActionPostInstance(requestMap, user, groupSend)
        validateDates(groupSend, requestMap.scheduled)
        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation(groupSend.populationListId)
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation(population) > 0)
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
            assignPopulationVersion(groupSend)
            if (hasQuery) {
                assignPopulationCalculation(groupSend, user.oracleUserName)
            }
        }
        // we don't use parameterValues. remove?
        ActionItemPost groupSendSaved = actionItemPostService.update(groupSend)
        // Create the details records.
        deletePostingDetail(groupSendSaved.id)
        requestMap.actionItemIds.each {
            addPostingDetail(it, groupSendSaved.id)
        }
        if (groupSend.aSyncJobId != null) {
            schedulerJobService.deleteScheduledJob(groupSend.aSyncJobId, groupSend.aSyncGroupId)
        }
        if (requestMap.postNow) {
            groupSendSaved = schedulePostImmediately(groupSendSaved, user.oracleUserName)
        } else if (requestMap.scheduledStartDate) {
            groupSendSaved = schedulePost(groupSendSaved, user.oracleUserName,requestMap.isRecurEdit)
        }
        success = true
        [
                success : success,
                savedJob: groupSendSaved
        ]
    }

    /**
     * Validates and creates the Recurring action item detail object
     * @param requestMap post request containing parameters from user input
     * @return
     */
    ActionItemPostRecurringDetails validateAndCreateActionItemRecurDetlObject(def requestMap) {
        Date displayStartDateTime= getDisplayDateTimeCalender(requestMap.displayDatetimeZone).getTime()
        actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        def actionItemPostRecurringDetailsObject = getActionItemPostRecurringInstance(requestMap)
        actionItemPostRecurringDetailsService.validateDates(actionItemPostRecurringDetailsObject,displayStartDateTime)
        actionItemPostRecurringDetailsService.create(actionItemPostRecurringDetailsObject)
    }
    /**
     * Creates ActionItemPost object for a recurring action item for displaying it in the grid.
     * It does not schedule the job or post the job
     * @param requestMap post request containing parameters from user input
     * @param recurringDetailId recurringDetailId containing metadata for recurrence
     * @param user logged in user
     * @return
     */

    ActionItemPost createActionItemPostForRecurActionItem(def requestMap, Long recurringDetailId, def user) {
        requestMap.displayStartDate = requestMap.recurStartDate
        requestMap.displayEndDate = requestMap.recurEndDate
        requestMap.timezoneStringOffset = requestMap.recurPostTimezone
        actionItemPostService.preCreateValidation(requestMap)
        def actionItemPost = getActionPostInstance(requestMap, user)
        actionItemPost.setPostingCurrentState( ActionItemPostExecutionState.RecurrenceScheduled)
        actionItemPost.recurringPostInd = true
        actionItemPost.recurringPostDetailsId = recurringDetailId
        actionItemPostService.create(actionItemPost)
    }
    /**
     * This method initiates the creation of recurring action item posting job
     * @param requestMap post request containing parameters from user input
     * @return
     */

    def addRecurringActionItemPosting(requestMap) {

        def user = springSecurityService.getAuthentication()?.user
        requestMap.recurrence=true
        ActionItemPostRecurringDetails actionItemPostRecurringDetails = validateAndCreateActionItemRecurDetlObject(requestMap)
        ActionItemPost post = createActionItemPostForRecurActionItem(requestMap, actionItemPostRecurringDetails.id, user)
        def actionItemIds = requestMap.actionItemIds

        // Create the Action Item details records.
        requestMap.actionItemIds.each {
            addPostingDetail(it, post.id)
        }

        def actionItemPostObjects = createActionItemObjects(actionItemPostRecurringDetails, post)

        def success = true
        def result
        def asyncRequestMap = [scheduled    : true,
                               actionItemIds: actionItemIds,
                               postNow      : false]

        actionItemPostObjects.each { actionItemPost ->
            asyncRequestMap.scheduledStartDate = actionItemPost.postingScheduleDateTime
            asyncRequestMap.recurringActionItemPost = actionItemPost
            result = sendAsynchronousPostItem(asyncRequestMap)
        }

        [
                success : success,
                savedJob: post
        ]

    }

    /**
     * This method updates the recurring action item posting job
     * @param requestMap post request containing parameters from user input
     * @return
     */

    def updateRecurringActionItemPosting(requestMap) {

        def success = false
        requestMap.recurrence=true
        actionItemPostService.preCreateValidation(requestMap)
        actionItemPostRecurringDetailsService.preCreateValidate(requestMap)

        def actionItemPost = ActionItemPost.fetchJobByPostingId(requestMap.postId )
        def actionItemPostRecurringDetails = ActionItemPostRecurringDetails.fetchByRecurId(actionItemPost.recurringPostDetailsId)
        def  actionItemPostRecurringJobs = ActionItemPost.fetchRecurringScheduledJobs(requestMap.postId,actionItemPost.recurringPostDetailsId)
        def updatedRecurringJobsList=actionItemPostRecurringJobs
        def changedRecurringJobsList

        updatedRecurringJobsList=checkAndEditRecurEndDateLesser(requestMap,actionItemPostRecurringDetails,actionItemPostRecurringJobs)
        updatedRecurringJobsList=checkAndEditRecurEndDateGreater(requestMap,actionItemPostRecurringDetails,actionItemPostRecurringJobs,updatedRecurringJobsList)
        changedRecurringJobsList=updatedRecurringJobsList
        changedRecurringJobsList=checkAndEditDisplayStartDays(requestMap,actionItemPostRecurringDetails,changedRecurringJobsList)
        changedRecurringJobsList=checkAndEditDisplayEndDays(requestMap,actionItemPostRecurringDetails,changedRecurringJobsList)
        changedRecurringJobsList=checkAndEditDisplayEndDateToOffset(requestMap,actionItemPostRecurringDetails,changedRecurringJobsList)
        changedRecurringJobsList=checkAndEditDisplayEndDate(requestMap,actionItemPostRecurringDetails,changedRecurringJobsList)

        updateRecurData(actionItemPostRecurringDetails)
        updateRecurrenceScheduledJob(actionItemPost)

        success=true
        [
                success : success,
                savedJob: actionItemPost
        ]

    }

    /**
     * This method checks the RecurEndDate lesser and fetches the latest recurring jobs
     * @param requestMap post request containing parameters from user input
     * @actionItemPostRecurringDetails contains saved recurringDetails
     * @actionItemPostRecurringJobs actionItemPostRecurringJobs contains latest actionItemRecurringJobs
     * @return
     */

    def checkAndEditRecurEndDateLesser(requestMap,actionItemPostRecurringDetails,actionItemPostRecurringJobs) {

        List<ActionItemPost> editedRecurringJobsList=actionItemPostRecurringJobs
        Date newrecurEndDate=  new Date(requestMap.recurEndDate)
        if (newrecurEndDate.compareTo(actionItemPostRecurringDetails.recurEndDate )<0)
        {
            deleteJobsRecurEndDateLesser(actionItemPostRecurringJobs,requestMap.recurEndDate)
            actionItemPostRecurringDetails.recurEndDate= actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.recurEndDate)
            def newRecurringJobList=ActionItemPost.fetchRecurringScheduledJobs(requestMap.postId,actionItemPostRecurringDetails.id)
            return newRecurringJobList
        }
         return  editedRecurringJobsList
    }

    /**
     * This method checks the RecurEndDate Greater and fetches the latest recurring jobs
     * @param requestMap post request containing parameters from user input
     * @actionItemPostRecurringDetails actionItemPostRecurringDetails contains saved recurringDetails
     * @editedRecurringJobs editedRecurringJobs contains edited actionItemRecurringJobs
     * @return
     */

    def checkAndEditRecurEndDateGreater(requestMap,actionItemPostRecurringDetails,actionItemPostRecurringJobs,editedRecurringJobs) {

        List<ActionItemPost> editedRecurringJobsList=actionItemPostRecurringJobs
        Date newrecurEndDateGreater = new Date(requestMap.recurEndDate)
        if (newrecurEndDateGreater.compareTo(actionItemPostRecurringDetails.recurEndDate) > 0) {
            actionItemPostRecurringDetails.recurEndDate= actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.recurEndDate)
            editedRecurringJobsList = insertJobsRecurEndDateGreater(actionItemPostRecurringDetails, requestMap)
             def newRecurringJobList=ActionItemPost.fetchRecurringScheduledJobs(requestMap.postId,actionItemPostRecurringDetails.id)
            return newRecurringJobList
        }
        return editedRecurringJobs
    }

    /**
     * This method checks the new DisplayStartDays not equal to old DisplayStartDays and edit
     * @param requestMap post request containing parameters from user input
     * @actionItemPostRecurringDetails actionItemPostRecurringDetails contains saved recurringDetails
     * @editedRecurringJobs editedRecurringJobs contains edited actionItemRecurringJobs
     * @return
     */

    def checkAndEditDisplayStartDays(requestMap,actionItemPostRecurringDetails,editedRecurringJobs){

        Integer diffDispStartDaysVal
        if (requestMap.postingDispStartDays != actionItemPostRecurringDetails.postingDispStartDays){
            diffDispStartDaysVal=requestMap.postingDispStartDays-actionItemPostRecurringDetails.postingDispStartDays;
            editedRecurringJobs.each {
                Date calculatedDisplayStartDate = actionItemPostRecurringDetailsService.addDays( it.postingDisplayStartDate,diffDispStartDaysVal)
                it.postingDisplayStartDate = calculatedDisplayStartDate
            }
            actionItemPostRecurringDetails.postingDispStartDays= requestMap.postingDispStartDays
        }
        return editedRecurringJobs
    }

    /**
     * This method checks the new DisplayEndDays not equal to old DisplayEndDays and edit
     * @param requestMap post request containing parameters from user input
     * @actionItemPostRecurringDetails actionItemPostRecurringDetails contains saved recurringDetails
     * @editedRecurringJobs editedRecurringJobs contains edited actionItemRecurringJobs
     * @return
     */
    def checkAndEditDisplayEndDays(requestMap,actionItemPostRecurringDetails,editedRecurringJobs){

        Integer diffDispEndDaysVal
        if (!requestMap.postingDisplayEndDate && !actionItemPostRecurringDetails.postingDisplayEndDate) {
                actionItemPostRecurringDetails.postingDispEndDays ? actionItemPostRecurringDetails.postingDispEndDays : 0
                if (requestMap.postingDispEndDays != actionItemPostRecurringDetails.postingDispEndDays) {
                    diffDispEndDaysVal = requestMap.postingDispEndDays - actionItemPostRecurringDetails.postingDispEndDays;
                    editedRecurringJobs.each {
                        Date calculatedDisplayEndDate = actionItemPostRecurringDetailsService.addDays(it.postingDisplayEndDate, diffDispEndDaysVal)
                        it.postingDisplayEndDate = calculatedDisplayEndDate
                    }
                    actionItemPostRecurringDetails.postingDispEndDays = requestMap.postingDispEndDays
                    actionItemPostRecurringDetails.postingDisplayEndDate = null
                }
        }
        return  editedRecurringJobs
    }

    /**
     * This method checks the change from Display End Date to Offset and edit
     * @param requestMap post request containing parameters from user input
     * @actionItemPostRecurringDetails actionItemPostRecurringDetails contains saved recurringDetails
     * @editedRecurringJobs editedRecurringJobs contains edited actionItemRecurringJobs
     * @return
     */

    def checkAndEditDisplayEndDateToOffset(requestMap,actionItemPostRecurringDetails,editedRecurringJobs){

        if ((requestMap.postingDispEndDays && actionItemPostRecurringDetails.postingDisplayEndDate) || !requestMap.postingDisplayEndDate ){
            editedRecurringJobs.each {
                Date calculatedDisplayEndDate=  actionItemPostRecurringDetailsService.addDays( it.postingScheduleDateTime,requestMap.postingDispEndDays)
                it.postingDisplayEndDate = calculatedDisplayEndDate
            }
            actionItemPostRecurringDetails.postingDispEndDays= requestMap.postingDispEndDays
            actionItemPostRecurringDetails.postingDisplayEndDate= null
        }
        return  editedRecurringJobs
    }

    /**
     * This method compares the new DisplayEndDate to old DisplayEndDate and edit
     * @param requestMap post request containing parameters from user input
     * @actionItemPostRecurringDetails actionItemPostRecurringDetails contains saved recurringDetails
     * @editedRecurringJobs editedRecurringJobs contains edited actionItemRecurringJobs
     * @return
     */

    def checkAndEditDisplayEndDate(requestMap,actionItemPostRecurringDetails,editedRecurringJobs){

        if(requestMap.postingDisplayEndDate) {
            Date newPostingDisplayEndDate=  new Date(requestMap.postingDisplayEndDate)

            editedRecurringJobs.each {
                if (it.postingDisplayStartDate.compareTo(newPostingDisplayEndDate) > 0) {
                    throw new ApplicationException(ActionItemPostService, new BusinessLogicValidationException('preCreate.validation.display.start.date.more.than.display.end.date', []))
                }
            }
            if (actionItemPostRecurringDetails.postingDisplayEndDate.equals(null) ) {
                editedRecurringJobs.each {
                       it.postingDisplayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.postingDisplayEndDate)
                }
            }
            else{
                if( newPostingDisplayEndDate.compareTo(actionItemPostRecurringDetails.postingDisplayEndDate) != 0){
                    editedRecurringJobs.each {
                        it.postingDisplayEndDate = newPostingDisplayEndDate
                    }
                }
            }
            actionItemPostRecurringDetails.postingDispEndDays= null
            actionItemPostRecurringDetails.postingDisplayEndDate= actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.postingDisplayEndDate)
        }
        return editedRecurringJobs
    }

    /**
     * This method compares new RecurEnd Date is less than old Recur End Date. Delete jobs beyond Recur End Date
     * @param requestMap post request containing parameters from user input
     * @actionItemPostRecurringJobs actionItemPostRecurringJobs contains actionItemRecurringJobs
     * @editedRecurEndDate editedRecurEndDate contains new Recur EndDate
     * @return
     */
    @Transactional
    def deleteJobsRecurEndDateLesser(actionItemPostRecurringJobs,editedRecurEndDate)
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd")
        Date newRecurEndDate = sdf.parse(sdf.format(new Date(editedRecurEndDate)))
        actionItemPostRecurringJobs.each {
            Date postingDisplayDate = sdf.parse(sdf.format(it.postingDisplayDateTime))
            if (newRecurEndDate.compareTo(postingDisplayDate)<0)
            {
                deletePostingDetail(it.id)
                deletePost(it.id)
            }
        }
    }

    /**
     * This method compares new RecurEnd Date is greater than old Recur End Date.Insert jobs
     * @recurDetails recurDetails contains new recur Details
     * @requestMap requestMap containing parameters from user input
     * @return
     */
    def insertJobsRecurEndDateGreater (recurDetails,requestMap){

        ActionItemPost post = ActionItemPost.fetchJobByPostingId(requestMap.postId )
        def getAllExistingRecurringJobs=ActionItemPost.fetchAllRecurringJobs(requestMap.postId,recurDetails.id)
        def actionItemPostObjects = createActionItemObjects(recurDetails, post)
        def newRecurringJobs = []

        for (Integer iteration=0;iteration<actionItemPostObjects.size();iteration++)
        {
               if(iteration>=getAllExistingRecurringJobs.size())
               {
                   newRecurringJobs.push(actionItemPostObjects[iteration])
               }
        }

        def actionItemIds = requestMap.actionItemIds
        def result
        def asyncRequestMap = [scheduled    : true,
                               actionItemIds: actionItemIds,
                               postNow      : false,
                               isRecurEdit  : true]


        newRecurringJobs.each { actionItemPost ->
            asyncRequestMap.scheduledStartDate = actionItemPost.postingScheduleDateTime
            asyncRequestMap.recurringActionItemPost = actionItemPost
            result = sendAsynchronousPostItem(asyncRequestMap)
        }
    }

    /**
     * This method to update the recur Details
     * @changedRecurData changedRecurData contains new recur Details
     */
    def  updateRecurData(changedRecurData){
        actionItemPostRecurringDetailsService.update(changedRecurData)
    }

    /**
     * This method to update the recurrenceScheduledJob
     * @recurrenceScheduledJob recurrenceScheduledJob contains recurrenceScheduledJobDetails
     */
    def  updateRecurrenceScheduledJob(recurrenceScheduledJob) {
        actionItemPostService.update(recurrenceScheduledJob)
    }

    /**
     * Creates action item post recurring detail object from input parameters
     * @param requestMap post request containing parameters from user input
     * @return
     */

    ActionItemPostRecurringDetails getActionItemPostRecurringInstance(def requestMap) {

        Date recurStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.recurStartDate)
        Date recurEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.recurEndDate)
        Date postingDisplayEndDate = requestMap.postingDisplayEndDate?actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.postingDisplayEndDate):null
        String scheduledStartTime = requestMap.recurStartTime
        String timezoneStringOffset = requestMap.recurPostTimezone
        Calendar scheduledStartDateCalendar = actionItemProcessingCommonService.getRequestedTimezoneCalendar(recurStartDate, scheduledStartTime, timezoneStringOffset)

        new ActionItemPostRecurringDetails(
                recurFrequency: requestMap.recurFrequency,
                recurFrequencyType: requestMap.recurFrequencyType,
                postingDispStartDays: requestMap.postingDispStartDays,
                postingDispEndDays: requestMap.postingDispEndDays,
                postingDisplayEndDate: postingDisplayEndDate,
                recurStartDate: recurStartDate,
                recurEndDate: recurEndDate,
                recurStartTime: scheduledStartDateCalendar.getTime(),
                recurPostTimezone: requestMap.recurPostTimezone
        )
    }
    /**
     * Creates indivudual action item jobs for a recurring job
     * @param actionItemPostRecurringDetails Containing metadata required for recurring job
     * @param actionItemPost recurring job action item object
     * @return
     */
    List<ActionItemPost> createActionItemObjects(ActionItemPostRecurringDetails actionItemPostRecurringDetails, ActionItemPost actionItemPost) {

        Long numberOfJobs = actionItemPostRecurringDetailsService.getNumberOfJobs(actionItemPostRecurringDetails,actionItemPost.postingDisplayDateTime)
        List<ActionItemPost> individualActionItemPostObjects = new LinkedList<ActionItemPost>()
        ActionItemPost individualActionItemPost

        for (int iteration = 0; iteration <= numberOfJobs; iteration++) {
            Date postingScheduleDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, iteration)
            Date postingDisplayDateTime = actionItemPostRecurringDetailsService.resolvePostingDisplayDateTime(actionItemPostRecurringDetails,actionItemPost,iteration)
            Date postingDisplayStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(postingDisplayDateTime, actionItemPostRecurringDetails)
            Date postingDisplayEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(postingDisplayDateTime, actionItemPostRecurringDetails)
            individualActionItemPost = new ActionItemPost(
                    populationListId: actionItemPost.populationListId,
                    postingActionItemGroupId: actionItemPost.postingActionItemGroupId,
                    postingName: actionItemPost.postingName + " " + "#" + (iteration + 1),
                    postingDisplayStartDate: postingDisplayStartDate,
                    postingDisplayEndDate: postingDisplayEndDate,
                    postingScheduleDateTime: postingScheduleDateTime,
                    postingCreationDateTime: new Date(),
                    populationRegenerateIndicator: actionItemPost.populationRegenerateIndicator,
                    postingDeleteIndicator: false,
                    postingCreatorId: actionItemPost.postingCreatorId,
                    postingCurrentState: ActionItemPostExecutionState.Scheduled,
                    postingDisplayDateTime: postingDisplayDateTime,
                    postingTimeZone: actionItemPost.postingTimeZone,
                    recurringPostInd: false,
                    recurringPostDetailsId: actionItemPost.recurringPostDetailsId
            )
            log.trace "createActionItemObjects - Item Created no ${iteration}, Item created ${individualActionItemPost} "
            individualActionItemPostObjects.add(individualActionItemPost)
        }

        individualActionItemPostObjects
    }

    /**
     * Creates new Instance of Action Item Post
     * @param requestMap
     * @param user
     * @return
     */
    ActionItemPost getActionPostInstance(requestMap, user) {
        Date scheduledStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.scheduledStartDate)
        String scheduledStartTime = requestMap.scheduledStartTime
        String timezoneStringOffset = requestMap.timezoneStringOffset
        Calendar displayDateTimeCalendar = getDisplayDateTimeCalender(requestMap.displayDatetimeZone)
        Calendar scheduledStartDateCalendar = null
        if (!requestMap.postNow && scheduledStartDate && scheduledStartTime) {
            scheduledStartDateCalendar = actionItemProcessingCommonService.getRequestedTimezoneCalendar(scheduledStartDate, scheduledStartTime, timezoneStringOffset)
        }

        new ActionItemPost(
                populationListId: requestMap.populationId,
                postingActionItemGroupId: requestMap.postingActionItemGroupId,
                postingName: requestMap.postingName,
                postingDisplayStartDate: actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.displayStartDate),
                postingDisplayEndDate: actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.displayEndDate),
                postingScheduleDateTime: scheduledStartDateCalendar ? scheduledStartDateCalendar.getTime() : null,
                postingCreationDateTime: new Date(),
                populationRegenerateIndicator: requestMap.populationRegenerateIndicator,
                postingDeleteIndicator: false,
                postingCreatorId: user.oracleUserName,
                postingCurrentState: requestMap.postNow ? ActionItemPostExecutionState.Queued : (requestMap.scheduled ? ActionItemPostExecutionState.Scheduled : ActionItemPostExecutionState.New),
                postingDisplayDateTime: displayDateTimeCalendar ? displayDateTimeCalendar.getTime() : null,
                postingTimeZone: requestMap.displayDatetimeZone.timeZoneVal,
                vpdiCode: getVpdiCode()

        )

    }

    /**Get Display Date and Time
     *
     * @param userEnteredValue
     * @return
     */
    def getDisplayDateTimeCalender(userEnteredValue) {
        Date displayDate = actionItemProcessingCommonService.convertToLocaleBasedDate(userEnteredValue.dateVal)
        def userEnteredHour = userEnteredValue.timeVal.substring(0, 2).toInteger()
        def userEnteredMinute = userEnteredValue.timeVal.substring(2).toInteger()
        Calendar displayDateTimeCalendar = Calendar.getInstance()
        displayDateTimeCalendar.setTime(displayDate)
        displayDateTimeCalendar.set(java.util.Calendar.HOUR, userEnteredHour)
        displayDateTimeCalendar.set(java.util.Calendar.MINUTE, userEnteredMinute)
        displayDateTimeCalendar.set(java.util.Calendar.SECOND, 0)
        displayDateTimeCalendar.set(java.util.Calendar.MILLISECOND, 0)
        displayDateTimeCalendar
    }

    /**
     * update  Instance of Action Item Post
     * @param requestMap
     * @param user
     * @param actionItemPost
     * @return
     */
    ActionItemPost updateActionPostInstance(requestMap, user, actionItemPost) {
        Date scheduledStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.scheduledStartDate)
        String scheduledStartTime = requestMap.scheduledStartTime
        String timezoneStringOffset = requestMap.timezoneStringOffset
        Calendar displayDateTimeCalendar = getDisplayDateTimeCalender(requestMap.displayDatetimeZone)
        Calendar scheduledStartDateCalendar = null
        if (!requestMap.postNow && scheduledStartDate && scheduledStartTime) {
            scheduledStartDateCalendar = actionItemProcessingCommonService.getRequestedTimezoneCalendar(scheduledStartDate, scheduledStartTime, timezoneStringOffset)
        }

        actionItemPost.populationListId = requestMap.populationId
        actionItemPost.postingActionItemGroupId = requestMap.postingActionItemGroupId
        actionItemPost.postingName = requestMap.postingName
        actionItemPost.postingDisplayStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.displayStartDate)
        actionItemPost.postingDisplayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.displayEndDate)
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
    private addPostingDetail(actionItemId, postingId) {
        ActionItemPostDetail groupDetail = new ActionItemPostDetail(
                actionItemPostId: postingId,
                actionItemId: actionItemId
        )
        actionItemPostDetailService.create(groupDetail)
    }

    /**
     * delete Posting Details
     * @param postingId
     * @param user
     * @return
     */
    private deletePostingDetail(postingId) {
        List actionItemPostDetailList = actionItemPostDetailService.fetchByActionItemPostId(postingId)
        actionItemPostDetailList.each {
            actionItemPostDetailService.delete(it)
        }
    }

    /**
     * Marks Action Item Posted
     * @param actionItemId
     * @param user
     * @return
     */
    def markActionItemPosted(actionItemId) {
        ActionItem actionItem = actionItemService.get(actionItemId)
        actionItem.postedIndicator = AIPConstants.YES_IND
        actionItemService.update(actionItem)
    }

    /**
     * Marks Action Item Group Posted
     * @param actionItemGroupId
     * @param user
     * @return
     */
    private markActionItemGroupPosted(actionItemGroupId) {
        ActionItemGroup actionItemGroup = actionItemGroupService.get(actionItemGroupId)
        actionItemGroup.postingInd = AIPConstants.YES_IND
        actionItemGroupService.update(actionItemGroup)
    }

    /**
     * Checks if posting name is already present
     * @param name
     * @return
     */
    private def validateDates(ActionItemPost groupSend, isScheduled) {
        Date currentDate = actionItemProcessingCommonService.getLocaleBasedCurrentDate()
        if (currentDate.compareTo(groupSend.postingDisplayStartDate) > 0) {
            throw new ApplicationException(ActionItemPostService, new BusinessLogicValidationException('preCreate.validation.obsolete.display.start.date', []))
        }
        if (groupSend.postingDisplayStartDate.compareTo(groupSend.postingDisplayEndDate) > 0) {
            throw new ApplicationException(ActionItemPostService, new BusinessLogicValidationException('preCreate.validation.display.start.date.more.than.display.end.date', []))
        }
        if (isScheduled) {
            Date now = new Date(System.currentTimeMillis())
            if (now.after(groupSend.postingScheduleDateTime)) {
                throw ActionItemExceptionFactory.createApplicationException(ActionItemPostService.class, "preCreate.validation.display.obsolete.schedule.date")
            }
        }
    }


    private static void assignPopulationCalculation(ActionItemPost groupSend, String bannerUser) {
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
    void deletePost(Long groupSendId) {
        log.debug( "deleteGroupSend for id = ${groupSendId}.")
        ActionItemPost groupSend = (ActionItemPost) actionItemPostService.get(groupSendId)
        if (!groupSend.postingCurrentState.pending && !groupSend.postingCurrentState.terminal) {
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostCompositeService.class, "cannotDeleteRunningPost")
        }

        // Grab population calculation if only used for this group send
        CommunicationPopulationCalculation calculation = null
        boolean recalculateOnSend = groupSend.populationRegenerateIndicator
        if (groupSend.populationCalculationId != null) {
            calculation = CommunicationPopulationCalculation.get(groupSend.populationCalculationId)
        }

        //if group send is scheduled
        if (groupSend.aSyncJobId != null) {
            schedulerJobService.deleteScheduledJob(groupSend.aSyncJobId, groupSend.aSyncGroupId)
        } else {
            //if Group send is not scheduled then remove job and recipient data
            deleteActionItemJobsByGroupSendId(groupSendId)
        }
        actionItemPostService.delete(groupSendId)

        // Garbage collect the population calculation
        if (calculation != null) {
            if (recalculateOnSend) {
                communicationPopulationCompositeService.deletePopulationCalculation(groupSend.populationCalculationId)
            } else {
                CommunicationPopulationCalculation latestCalculation =
                        CommunicationPopulationCalculation.findLatestByPopulationVersionIdAndCalculatedBy(calculation.populationVersion.id, calculation.createdBy)
                if (calculation.id != latestCalculation.id) {
                    communicationPopulationCompositeService.deletePopulationCalculation(latestCalculation)
                }
            }
        }
    }

    /**
     * Stops a group send. The group send must be running otherwise an application exception will be thrown.
     * @param groupSendId the long id of the group send
     * @return the updated (stopped) group send
     */
    ActionItemPost stopPost(Long groupSendId) {
        log.debug( "Stopping group send with id = ${groupSendId}.")

        ActionItemPost groupSend = (ActionItemPost) actionItemPostService.get(groupSendId)

        if (groupSend.postingCurrentState.isTerminal()) {
            log.warn( "Group send with id = ${groupSend.id} has already concluded with execution state ${groupSend.postingCurrentState.toString()}.")
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostService.class, "cannotStopConcludedPost")
        }

        groupSend.markStopped()
        groupSend = actionItemPostService.update(groupSend)

        if (groupSend.aSyncJobId != null) {
            this.schedulerJobService.deleteScheduledJob(groupSend.aSyncJobId, groupSend.aSyncGroupId)
        }

        // fetch any post jobs for this group send and marked as stopped
        stopPendingAndDispatchedJobs(groupSend.id)
        actionItemPostWorkService.updateStateToStop(groupSend)
        groupSend
    }

    /**
     * Marks a group post as complete.
     * @param groupSendId the id of the group post.
     * @return the updated group post
     */
    ActionItemPost completePost(Long groupSendId) {
        log.debug( "Completing group send with id = " + groupSendId + ".")
        ActionItemPost aGroupSend = (ActionItemPost) actionItemPostService.get(groupSendId)
        aGroupSend.markComplete()
        actionItemPostService.update(aGroupSend)
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // Scheduling service callback job methods for Query Population (leave public)
    //////////////////////////////////////////////////////////////////////////////////////

    public ActionItemPost calculatePopulationVersionForPostFired(SchedulerJobContext jobContext) {
        asynchronousBannerAuthenticationSpoofer.setMepContext(sessionFactory.currentSession.connection(), jobContext.parameters.get("mepCode"))
        markArtifactsAsPosted(jobContext.parameters.get("groupSendId") as Long)
        calculatePopulationVersionForGroupSend(jobContext.parameters)
    }


    public ActionItemPost calculatePopulationVersionForPostFailed(SchedulerErrorContext errorContext) {
        asynchronousBannerAuthenticationSpoofer.setMepContext(sessionFactory.getCurrentSession().connection(),errorContext.jobContext?.parameters?.get("mepCode"))
        scheduledPostCallbackFailed(errorContext)
    }

    /**
     *
     * @param groupSendId
     * @return
     */
    public def markArtifactsAsPosted(groupSendId) {
        ActionItemPost groupSend = actionItemPostService.get(groupSendId)
        markActionItemGroupPosted(groupSend.postingActionItemGroupId)
        List actionItemsIds = actionItemPostDetailService.fetchByActionItemPostId(groupSendId).actionItemId
        actionItemsIds?.each {
            markActionItemPosted(it)
        }
    }

    /**
     *
     * @param jobContext
     * @return
     */
    public ActionItemPost generatePostItemsFired(SchedulerJobContext jobContext) {
        asynchronousBannerAuthenticationSpoofer.setMepContext(sessionFactory.currentSession.connection(), jobContext.parameters.get("mepCode"))
        generatePostItemsFiredImpl(jobContext)
    }

    /**
     *
     * @param jobContext
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    private void generatePostItemsFiredImpl(SchedulerJobContext jobContext) {
        asynchronousBannerAuthenticationSpoofer.setMepContext(sessionFactory.currentSession.connection(), jobContext.parameters.get("mepCode"))
        markArtifactsAsPosted(jobContext.parameters.get("groupSendId") as Long)
        generatePostItems(jobContext.parameters)
    }

    /**
     *
     * @param errorContext
     * @return
     */
    public ActionItemPost generatePostItemsFailed(SchedulerErrorContext errorContext) {
        asynchronousBannerAuthenticationSpoofer.setMepContext(sessionFactory.currentSession.connection(), errorContext.jobContext.parameters.get("mepCode"))
        generatePostItemsFailedImpl(errorContext)
    }

    /**
     *
     * @param errorContext
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
    private void generatePostItemsFailedImpl(SchedulerErrorContext errorContext) {
        asynchronousBannerAuthenticationSpoofer.setMepContext(sessionFactory.currentSession.connection(), errorContext.jobContext.parameters.get("mepCode"))
        scheduledPostCallbackFailed(errorContext)
    }


    CommunicationPopulationVersion assignPopulationVersion(ActionItemPost groupSend) {

        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation(groupSend.populationListId)
        CommunicationPopulationVersion populationVersion

        if (population.changesPending) {
            if (RequestContextHolder?.getRequestAttributes()?.request?.session) {
                def session = RequestContextHolder.currentRequestAttributes()?.request?.session
                def mepCode = session?.getAttribute("mep")
                if (mepCode != null) {
                    population.setMepCode(mepCode)
                }
            }
            populationVersion = communicationPopulationCompositeService.createPopulationVersion(population)
            population.changesPending = false
            communicationPopulationCompositeService.updatePopulation(population)
        } else {
            populationVersion = CommunicationPopulationVersion.findLatestByPopulationId(groupSend.populationListId)
        }
        if (!populationVersion) {
            throw ActionItemExceptionFactory.createApplicationException( ActionItemPostCompositeService.class,
                    "populationVersionNotFound" )
        }
        assert populationVersion.id
        groupSend.populationVersionId = populationVersion.id
        populationVersion
    }


    ActionItemPost scheduledPostCallbackFailed(SchedulerErrorContext errorContext) {
        Long groupSendId = errorContext.jobContext.getParameter("groupSendId") as Long
        log.debug( "${errorContext.jobContext.errorHandle} called for groupSendId = ${groupSendId} with message = ${errorContext?.cause?.message}")
        ActionItemPost groupSend = ActionItemPost.get(groupSendId)
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
        groupSend
    }

    /**
     * This method is called by the scheduler to regenerate a population list specifically for the group send
     * and change the state of the group send to next state.
     */
    ActionItemPost calculatePopulationVersionForGroupSend(Map parameters) {
        Long groupSendId = parameters.get("groupSendId") as Long
        assert (groupSendId)
        log.debug( "Calling calculatePopulationVersionForPost for groupSendId = ${groupSendId}.")
        ActionItemPost groupSend = ActionItemPost.get(groupSendId)
        if (!groupSend) {
            throw new ApplicationException("groupSend", new NotFoundException())
        }

        if (!groupSend.postingCurrentState.isTerminal()) {
            try {
                boolean shouldUpdateGroupSend = false
                CommunicationPopulationVersion populationVersion
                populationVersion = assignPopulationVersion(groupSend)
                shouldUpdateGroupSend = true

                boolean hasQuery = (CommunicationPopulationVersionQueryAssociation.countByPopulationVersion(populationVersion) > 0)

                if (!groupSend.populationCalculationId && hasQuery) {
                    groupSend.setPostingCurrentState ( ActionItemPostExecutionState.Calculating)
                    CommunicationPopulationCalculation calculation = communicationPopulationCompositeService.calculatePopulationVersionForGroupSend(
                            populationVersion)
                    groupSend.populationCalculationId = calculation.id
                    shouldUpdateGroupSend = true
                }
                if (shouldUpdateGroupSend) {
                    groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
                }
                groupSend = generatePostItemsImpl(groupSend)
            } catch (Throwable t) {
                log.error(t.getMessage())
                groupSend.refresh()
                groupSend.markError(ActionItemErrorCode.UNKNOWN_ERROR, t.getMessage())
                groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
            }
        }
        groupSend
    }

    /**
     * This method is called by the scheduler to create the group send items and move the state of
     * the group send to processing.
     */
    ActionItemPost generatePostItems(Map parameters) {
        Long groupSendId = parameters.get("groupSendId") as Long
        assert (groupSendId)
        log.debug( "Calling generateGroupSendItems for groupSendId = ${groupSendId}.")
        ActionItemPost groupSend = ActionItemPost.get(groupSendId)
        if (!groupSend) {
            throw new ApplicationException("groupSend", new NotFoundException())
        }

        if (!groupSend.postingCurrentState.isTerminal()) {
            try {
                groupSend = generatePostItemsImpl(groupSend)
            } catch (Throwable t) {
                log.error( t.getMessage())
                groupSend.markError(ActionItemErrorCode.UNKNOWN_ERROR, t.getMessage())
                groupSend = (ActionItemPost) actionItemPostService.update(groupSend)
            }
        }
        groupSend
    }


    ActionItemPost schedulePostImmediately(ActionItemPost groupSend, String bannerUser) {
        log.debug( " Start creating  jobContext for ${bannerUser}.")
        def mepCode = RequestContextHolder.currentRequestAttributes().request.session.getAttribute('mep')
        SchedulerJobContext jobContext = new SchedulerJobContext(
                groupSend.aSyncJobId != null ? groupSend.aSyncJobId : UUID.randomUUID().toString())
                .setBannerUser(bannerUser)
                .setMepCode(mepCode)
                .setParameter("groupSendId", groupSend.id)

        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation(groupSend.populationListId)
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation(population) > 0)

        if ((hasQuery && groupSend.populationRegenerateIndicator) || !hasQuery) {
            jobContext.setJobHandle("actionItemPostCompositeService", "calculatePopulationVersionForPostFired")
                    .setErrorHandle("actionItemPostCompositeService", "calculatePopulationVersionForPostFailed")
        }
        else {
            jobContext.setJobHandle("actionItemPostCompositeService", "generatePostItemsFired")
                    .setErrorHandle("actionItemPostCompositeService", "generatePostItemsFailed")
        }

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleNowServiceMethod(jobContext)
        log.trace "Quratz Job is scheduled and ID - ${jobReceipt.jobId} "
        groupSend.markQueued(jobReceipt.jobId, jobReceipt.groupId)
        log.debug( " Completing marking posting in Queue.")
        log.trace "ActionItemPost before update $groupSend"
        actionItemPostService.update(groupSend)
    }

    /**
     * Schedules the action item posting
     * @param groupSend
     * @param bannerUser
     * @return
     */
    ActionItemPost schedulePost(ActionItemPost groupSend, String bannerUser, recurEditFlag) {
        Date now = new Date(System.currentTimeMillis())
        if ((now.after(groupSend.postingScheduleDateTime)) && (!recurEditFlag)) {
            throw ActionItemExceptionFactory.createApplicationException(ActionItemPostService.class, "invalidScheduledDate")
        }
        def mepCode = RequestContextHolder.currentRequestAttributes().request.session.getAttribute('mep')
        log.debug "Setting mepCod ${mepCode} in JobContext"
        SchedulerJobContext jobContext = new SchedulerJobContext(
                groupSend.aSyncJobId != null ? groupSend.aSyncJobId : UUID.randomUUID().toString())
                .setBannerUser(bannerUser)
                .setMepCode(mepCode)
                .setScheduledStartDate(groupSend.postingScheduleDateTime)
                .setParameter("groupSendId", groupSend.id)

        CommunicationPopulation population = communicationPopulationCompositeService.fetchPopulation(groupSend.populationListId)
        boolean hasQuery = (CommunicationPopulationQueryAssociation.countByPopulation(population) > 0)

        if ((hasQuery && groupSend.populationRegenerateIndicator) || !hasQuery) {
            jobContext.setJobHandle("actionItemPostCompositeService", "calculatePopulationVersionForPostFired")
                    .setErrorHandle("actionItemPostCompositeService", "calculatePopulationVersionForPostFailed")
        }
        else {
            jobContext.setJobHandle("actionItemPostCompositeService", "generatePostItemsFired")
                    .setErrorHandle("actionItemPostCompositeService", "generatePostItemsFailed")
        }

        SchedulerJobReceipt jobReceipt = schedulerJobService.scheduleServiceMethod(jobContext)
        groupSend.markScheduled(jobReceipt.jobId, jobReceipt.groupId)
        actionItemPostService.update(groupSend)
    }

    /**
     *
     * @param groupSend
     * @return
     */
    ActionItemPost generatePostItemsImpl(ActionItemPost groupSend) {
        createPostItems(groupSend)
        groupSend.markProcessing()
        actionItemPostService.update(groupSend)
    }

    /**
     * Removes all actionItem job records referenced by a group send id.
     *
     * @param groupSendId the long id of the group send.
     */
    void deleteActionItemJobsByGroupSendId(Long groupSendId) {
        actionItemJobService.deleteJobForAPostingId(groupSendId)
    }


    void stopPendingAndDispatchedJobs(Long groupSendId) {
        actionItemJobService.stopPendingAndDispatchedJobs(groupSendId)
    }

    /**
     *
     * @param groupSend
     */
    void createPostItems(ActionItemPost groupSend) {
        log.debug( "Generating group send item records for group send with id = " + groupSend?.id)
        def session = sessionFactory.currentSession
        def timeoutSeconds = (Holders?.config?.banner?.transactionTimeout instanceof Integer ? (Holders?.config?.banner?.transactionTimeout) : 300)
        try {
            Holders.applicationContext.getBean('transactionManager')?.setDefaultTimeout(timeoutSeconds * 2)
            List<ActionItemPostSelectionDetailReadOnly> list = session.getNamedQuery('ActionItemPostSelectionDetailReadOnly.fetchSelectionIds')
                    .setLong('postingId', groupSend.id)
                    .list()
            list?.each { ActionItemPostSelectionDetailReadOnly it ->
                session.createSQLQuery(""" INSERT INTO gcraiim (gcraiim_gcbapst_id, gcraiim_pidm, gcraiim_creationdatetime
                                                                   ,gcraiim_current_state, gcraiim_reference_id, gcraiim_user_id, gcraiim_activity_date,
                                                                   gcraiim_started_date) values (${groupSend.id}, ${
                    it.actionItemPostSelectionPidm
                }, sysdate, '${ActionItemPostWorkExecutionState.Ready.toString()}' ,'$sysGuId', '${
                    it.postingUserId
                }', sysdate, sysdate ) """)
                        .executeUpdate()
            }
            log.debug( "Created " + list?.size() + " group send item records for group send with id = " + groupSend.id)
        } finally {
            Holders.applicationContext.getBean('transactionManager')?.setDefaultTimeout(timeoutSeconds)
        }
    }
    /**
     * Get system GU id
     * @return
     */
    String getSysGuId() {
        sessionFactory.currentSession.createSQLQuery(' select RAWTOHEX(sys_guid()) from dual').uniqueResult()
    }

    /**
     *
     * @return vpidCode
     */
    private def getVpdiCode() {
        def session = RequestContextHolder?.currentRequestAttributes()?.request?.session
        session.getAttribute('mep')
    }

}
