/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.common.AipTimezone
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase

import java.text.SimpleDateFormat

/**
 * Service class for Action Item Posting Details Read Only
 */
class ActionItemPostReadOnlyService extends ServiceBase {

    def actionItemProcessingCommonService
    def actionItemPostService

    /**
     * Lists Post Action Items
     * @param params
     * @return
     */
    def listActionItemPostJobList(Map params ) {

        List<AipTimezone> timeZoneList = actionItemProcessingCommonService.populateAvailableTimezones()
        Map<String, AipTimezone> map = timeZoneList.collectEntries() {
            [it.stringOffset + ' ' + it.timezoneId, it]
        }
        def getDisplayTimeZoneInfo = { key ->
            map.get( key )?.displayNameWithoutOffset
        }

        def results =  params?.recurringPostId? fetchRecurringPostActionItemWithPagingAndSortParams(params) : fetchWithPagingAndSortParams( params )

        results = results.collect {
            ActionItemPostReadOnly it ->
                def postingCurrentState = it.recurringPostIndicator? updateRecurringCurrentStatus(it.postingId): it.postingCurrentState
                [
                        id                     : it.postingId,
                        postingId              : it.postingId,
                        postingCurrentState    : postingCurrentState,
                        jobState               : MessageHelper.message( 'aip.action.item.post.job.state.' + ActionItemPostExecutionState.getStateEnum( postingCurrentState ) ),
                        postingName            : it.postingName,
                        postingDisplayStartDate: it.postingScheduleDateTime ? it.postingScheduleDateTime : it.postingDisplayStartDate,
                        postingStartedDate     : it.postingStartedDate,
                        postingDisplayDateTime : it.recurringPostIndicator? "-" :it.postingDisplayDateTime,
                        postingDisplayTime     : it.recurringPostIndicator? "-" :(it.postingDisplayDateTime ? timeFormat().format( it.postingDisplayDateTime ) : it.postingDisplayDateTime),
                        postingTimeZone        : it.recurringPostIndicator? "-" :getDisplayTimeZoneInfo( it.postingTimeZone ),
                        groupFolderName        : it.groupFolderName,
                        postingPopulation      : it.postingPopulation,
                        groupName              : it.groupName,
                        postingCreatorId       : it.postingCreatorId,
                        lastModified           : it.lastModified,
                        lastModifiedBy         : it.lastModifiedBy,
                        version                : it.version,
                        recurringPostIndicator : it.recurringPostIndicator,
                        displayStartDate       : it.postingDisplayStartDate,
                        displayEndDate         : it.postingDisplayEndDate,
                        recurringPostJobError  : it.recurringPostIndicator?ActionItemPostReadOnly.fetchRecurringJobsStateCount(it.recurringPostDetailsId,ActionItemPostExecutionState.Error.name()):0

                ]
        }
        def resultCount = fetchJobsCount( params )
        def resultMap = [
                result: results,
                length: resultCount,
        ]
        resultMap
    }

    /**
     * Lists Post count
     *
     * @return
     */
    def fetchJobsCount( params ) {
        params.searchParam = params.searchParam ? ('%' + params.searchParam.toUpperCase() + '%') : ('%')
        params?.recurringPostId? ActionItemPostReadOnly.fetchRecurringJobsCount( params ) : ActionItemPostReadOnly.fetchJobsCount( params )
    }


    def statusPosted( postingId ) {
        ActionItemPostReadOnly actionItemPostReadOnly = ActionItemPostReadOnly.fetchByPostingId( postingId )
        actionItemPostReadOnly
        if (actionItemPostReadOnly) {
            String Scheduled=ActionItemPostExecutionState.Scheduled.name()
            String RecurrenceScheduled=ActionItemPostExecutionState.RecurrenceScheduled.name()
            String RecurrenceInProgress=ActionItemPostExecutionState.RecurrenceInProgress.name()
            if ( (actionItemPostReadOnly.postingCurrentState.equals(Scheduled )) || (actionItemPostReadOnly.postingCurrentState.equals(RecurrenceScheduled )) || (actionItemPostReadOnly.postingCurrentState.equals(RecurrenceInProgress )))
            {
                return AIPConstants.YES_IND
            }
        }
        AIPConstants.NO_IND
    }


    def JobDetailsByPostId( postingId ) {
        ActionItemPostReadOnly actionItemPostReadOnly = ActionItemPostReadOnly.fetchByPostingId( postingId )
        ActionItemPostRecurringDetails actionItemPostRecurringDetails
        //Fetch Recurrence Details
        if(actionItemPostReadOnly.recurringPostIndicator){
            actionItemPostRecurringDetails=ActionItemPostRecurringDetails.fetchByRecurId(actionItemPostReadOnly.recurringPostDetailsId)
        }

        def result = [:]

        //Initializing the date format
        List timeZoneList = actionItemProcessingCommonService.populateAvailableTimezones()
        TimeZone timezone = TimeZone.getDefault();
        int defaultRowOffset = timezone.getRawOffset()
        AipTimezone serverDefaultTimeZone = timeZoneList.find {
            it.offset == defaultRowOffset //Getting the time zone of the server
        }
        if (actionItemPostReadOnly) {
            result = [
                    groupFolderId                : actionItemPostReadOnly.groupFolderId,
                    groupFolderName              : actionItemPostReadOnly.groupFolderName,
                    groupName                    : actionItemPostReadOnly.groupName,
                    groupStatus                  : actionItemPostReadOnly.groupStatus,
                    groupTitle                   : actionItemPostReadOnly.groupTitle,
                    lastModified                 : actionItemPostReadOnly.lastModified,
                    lastModifiedBy               : actionItemPostReadOnly.lastModifiedBy,
                    populationCalculationId      : actionItemPostReadOnly.populationCalculationId,
                    populationListId             : actionItemPostReadOnly.populationListId,
                    populationRegenerateIndicator: actionItemPostReadOnly.populationRegenerateIndicator,
                    postingActionItemGroupId     : actionItemPostReadOnly.postingActionItemGroupId,
                    postingCreationDateTime      : actionItemPostReadOnly.postingCreationDateTime,
                    postingCreatorId             : actionItemPostReadOnly.postingCreatorId,
                    postingCurrentState          : actionItemPostReadOnly.postingCurrentState,
                    postingDeleteIndicator       : actionItemPostReadOnly.postingDeleteIndicator,
                    postingDisplayEndDate        : actionItemPostReadOnly.postingDisplayEndDate,
                    postingDisplayStartDate      : actionItemPostReadOnly.postingDisplayStartDate,
                    postingErrorCode             : actionItemPostReadOnly.postingErrorCode,
                    postingErrorText             : actionItemPostReadOnly.postingErrorText,
                    postingId                    : actionItemPostReadOnly.postingId,
                    postingName                  : actionItemPostReadOnly.postingName,
                    postingParameterValues       : actionItemPostReadOnly.postingParameterValues,
                    postingPopulation            : actionItemPostReadOnly.postingPopulation,
                    postingScheduleDateTime      : actionItemPostReadOnly.postingScheduleDateTime,
                    postingStartedDate           : actionItemPostReadOnly.postingStartedDate,
                    version                      : actionItemPostReadOnly.version,
                    scheduledStartTime           : actionItemPostReadOnly.postingScheduleDateTime ? timeFormat().format( actionItemPostReadOnly.postingScheduleDateTime ) : actionItemPostReadOnly.postingScheduleDateTime,
                    timezoneStringOffset         : serverDefaultTimeZone,
                    postingDisplayDateTime       : actionItemPostReadOnly.postingDisplayDateTime,
                    postingDisplayTime           : actionItemPostReadOnly.postingDisplayDateTime ? timeFormat().format( actionItemPostReadOnly.postingDisplayDateTime ) : actionItemPostReadOnly.postingDisplayDateTime,
                    postingTimeZone              : actionItemPostReadOnly.postingTimeZone

            ]

        }
        if (actionItemPostRecurringDetails) {
            result.recurringDetails = actionItemPostRecurringDetails
            // Extract time from TimeStamp "recurStartTime"
            Date recurDateTime = new Date(actionItemPostRecurringDetails.recurStartTime.getTime());
            result.calculatedRecurTimeVal=timeFormat().format(recurDateTime);
        }
        result
    }

    /**
     * Function to fetch post action items based on params
     * @param params
     * @return
     */
    def fetchWithPagingAndSortParams( Map params ) {
        ActionItemPostReadOnly.fetchWithPagingAndSortParams(
                [name: params?.searchParam],
                [sortColumn: params.sortColumn, sortAscending: params.sortAscending, max: params.max, offset: params.offset] )
    }

    /**
     * Function to fetch recurring post action items
     * @param params
     * @return
     */
    def fetchRecurringPostActionItemWithPagingAndSortParams( Map params ) {
        Long postingId = params?.recurringPostId?Long.parseLong(params?.recurringPostId):-1
        def actionItemPostReadOnly = ActionItemPostReadOnly.fetchByPostingId(postingId)
        ActionItemPostReadOnly.fetchRecurringPostActionItemsWithPagingAndSortParams(
                [name: params?.searchParam,recurringPostDetailsId:  actionItemPostReadOnly.recurringPostDetailsId],
                [sortColumn: params.sortColumn, sortAscending: params.sortAscending, max: params.max, offset: params.offset] )
    }

    /**
     * Function to fetch recurring action item meta data
     * @param actionItemPostId
     * @return
     */
    def recurringJobMetaData(actionItemPostId){
        def actionItemPostReadOnly = ActionItemPostReadOnly.fetchByPostingId(Long.parseLong(actionItemPostId))
        def actionItemPostRecurringDetails =ActionItemPostRecurringDetails.get(actionItemPostReadOnly.recurringPostDetailsId)
        def postActionItemMetaDataMap = [
                postingName             :actionItemPostReadOnly.postingName,
                recurFrequency          :actionItemPostRecurringDetails.recurFrequency,
                recurFrequencyType      :actionItemPostRecurringDetails.recurFrequencyType,
                postingDispStartDays    :actionItemPostRecurringDetails.postingDispStartDays,
                postingDispEndDays      :actionItemPostRecurringDetails.postingDispEndDays,
                postingDisplayEndDate   :actionItemPostRecurringDetails.postingDisplayEndDate,
                recurringStartDate      :actionItemPostRecurringDetails.recurStartDate,
                postingDisplayTime      :actionItemPostReadOnly.postingDisplayDateTime ? timeFormat().format( actionItemPostReadOnly.postingDisplayDateTime ) : actionItemPostReadOnly.postingDisplayDateTime,
                recurPostTimezone       :actionItemPostRecurringDetails.recurPostTimezone,
                recurringEndDate        :actionItemPostRecurringDetails.recurEndDate,
                completedJobsCount      :ActionItemPostReadOnly.fetchRecurringJobsStateCount(actionItemPostReadOnly.recurringPostDetailsId,ActionItemPostExecutionState.Complete.name()),
                errorJobsCount          :ActionItemPostReadOnly.fetchRecurringJobsStateCount(actionItemPostReadOnly.recurringPostDetailsId,ActionItemPostExecutionState.Error.name()),
                remainingJobCount       :ActionItemPostReadOnly.fetchRecurringJobsStateCount(actionItemPostReadOnly.recurringPostDetailsId,ActionItemPostExecutionState.Scheduled.name()),
        ]
        postActionItemMetaDataMap
    }

    /**
     * Function to update recurring parant job status
     * @param actionItemPostId
     * @return
     */
    def updateRecurringCurrentStatus(actionItemPostId){
        def recurringCurrentStatus = ActionItemPostExecutionState.RecurrenceScheduled.name()
        ActionItemPost actionItemPost = ActionItemPost.fetchJobByPostingId(actionItemPostId)
        def recurringTotalJobcount = ActionItemPostReadOnly.fetchRecurringJobsCount( [recurringPostId:actionItemPostId+''] )
        def recurringRemainJobCount = ActionItemPostReadOnly.fetchRecurringJobsStateCount(actionItemPost.recurringPostDetailsId,ActionItemPostExecutionState.Scheduled.name())
        if(recurringRemainJobCount ==  0){
            recurringCurrentStatus =  ActionItemPostExecutionState.RecurrenceComplete.name()
        }else if (recurringRemainJobCount < recurringTotalJobcount ){
            recurringCurrentStatus =  ActionItemPostExecutionState.RecurrenceInProgress.name()
        }
        if(!recurringCurrentStatus.equals(actionItemPost.postingCurrentState)){
            actionItemPost.postingCurrentState = recurringCurrentStatus
            actionItemPostService.update(actionItemPost)
        }
        return recurringCurrentStatus
    }

    /**
     * Function to get simple time format object class
     * @return
     */
    def timeFormat(){
        SimpleDateFormat timeFormat = new SimpleDateFormat( MessageHelper.message( "default.time.format" ) )
        if (actionItemProcessingCommonService.is12HourClock().use12HourClock) {
            timeFormat = new SimpleDateFormat( "hh:mm a" );
        }
        timeFormat
    }

}