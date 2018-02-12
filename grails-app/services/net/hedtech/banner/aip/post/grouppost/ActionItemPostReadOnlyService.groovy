/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Service class for Action Item Posting Details Read Only
 */
class ActionItemPostReadOnlyService extends ServiceBase {

    /**
     * Lists Group Folders
     * @param params
     * @return
     */
    def listActionItemPostJobList(params, paginationParams) {
        params.searchParam = params.searchParam ? ('%' + params.searchParam.toUpperCase() + '%') : ('%')
        def results = ActionItemPostReadOnly.fetchJobs(params, paginationParams)
        results = results.collect {
            ActionItemPostReadOnly it -> [
                    id: it.postingId,
                    postingId: it.postingId,
                    postingCurrentState: it.postingCurrentState,
                    jobState: MessageHelper.message('aip.action.item.post.job.state.' + ActionItemPostExecutionState.getStateEnum(it.postingCurrentState)),
                    postingName: it.postingName,
                    postingDisplayStartDate: it.postingScheduleDateTime ? it.postingScheduleDateTime : it.postingDisplayStartDate,
                    postingStartedDate: it.postingStartedDate,
                    groupFolderName: it.groupFolderName,
                    postingPopulation: it.postingPopulation,
                    groupName: it.groupName,
                    postingCreatorId: it.postingCreatorId,
                    lastModified: it.lastModified,
                    lastModifiedBy: it.lastModifiedBy,
                    version: it.version


            ]
        }
        def resultCount = fetchJobsCount(params)
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
    def fetchJobsCount(params) {
        params.searchParam = params.searchParam ? ('%' + params.searchParam.toUpperCase() + '%') : ('%')
        ActionItemPostReadOnly.fetchJobsCount(params)
    }

    def statusPosted(postingId) {
        ActionItemPostReadOnly actionItemPostReadOnly = ActionItemPostReadOnly.fetchByPostingId(postingId)
        actionItemPostReadOnly
        if (actionItemPostReadOnly) {
            if (actionItemPostReadOnly.postingCurrentState.equals( "Scheduled") ){
                return AIPConstants.YES_IND
            }
        }
        return AIPConstants.NO_IND
    }
    def  JobDetailsByPostId(postingId) {
        ActionItemPostReadOnly actionItemPostReadOnly = ActionItemPostReadOnly.fetchByPostingId(postingId)
        def result ={}
        SimpleDateFormat timeFormat = new SimpleDateFormat(MessageHelper.message("default.time.format"));//Initializing the date format
        def timeZone = TimeZone.getDefault();//Getting the time zone of the server

        if (actionItemPostReadOnly)
        {
            result = [
                groupFolderId:actionItemPostReadOnly.groupFolderId,
                groupFolderName:actionItemPostReadOnly.groupFolderName,
                groupName:actionItemPostReadOnly.groupName,
                groupStatus:actionItemPostReadOnly.groupStatus,
                groupTitle:actionItemPostReadOnly.groupTitle,
                lastModified:actionItemPostReadOnly.lastModified,
                lastModifiedBy:actionItemPostReadOnly.lastModifiedBy,
                populationCalculationId:actionItemPostReadOnly.populationCalculationId,
                populationListId:actionItemPostReadOnly.populationListId,
                populationRegenerateIndicator:actionItemPostReadOnly.populationRegenerateIndicator,
                postingActionItemGroupId:actionItemPostReadOnly.postingActionItemGroupId,
                postingCreationDateTime:actionItemPostReadOnly.postingCreationDateTime,
                postingCreatorId:actionItemPostReadOnly.postingCreatorId,
                postingCurrentState:actionItemPostReadOnly.postingCurrentState,
                postingDeleteIndicator:actionItemPostReadOnly.postingDeleteIndicator,
                postingDisplayEndDate:actionItemPostReadOnly.postingDisplayEndDate,
                postingDisplayStartDate:actionItemPostReadOnly.postingDisplayStartDate,
                postingErrorCode:actionItemPostReadOnly.postingErrorCode,
                postingErrorText:actionItemPostReadOnly.postingErrorText,
                postingId:actionItemPostReadOnly.postingId,
                postingName:actionItemPostReadOnly.postingName,
                postingParameterValues:actionItemPostReadOnly.postingParameterValues,
                postingPopulation:actionItemPostReadOnly.postingPopulation,
                postingScheduleDateTime:actionItemPostReadOnly.postingScheduleDateTime,
                postingStartedDate:actionItemPostReadOnly.postingStartedDate,
                version:actionItemPostReadOnly.version,
                scheduledStartTime : actionItemPostReadOnly.postingScheduleDateTime ? timeFormat.format(actionItemPostReadOnly.postingScheduleDateTime) : actionItemPostReadOnly.postingScheduleDateTime,
                timezoneStringOffset  : timeZone

            ]

        }
        return result
    }

}
