/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Posting Details Read Only
 */
class ActionItemPostReadOnlyService extends ServiceBase {

    /**
     * Lists Group Folders
     * @param params
     * @return
     */
    def listActionItemPostJobList( params, paginationParams ) {
        params.searchParam = params.searchParam ? ('%' + params.searchParam.toUpperCase() + '%') : ('%')
        def results = ActionItemPostReadOnly.fetchJobs( params, paginationParams )
        results.each {ActionItemPostReadOnly it ->
            it.postingCurrentState = MessageHelper.message( 'aip.action.item.post.job.state.' + ActionItemPostExecutionState.getStateEnum( it.postingCurrentState ) )
        }
        def resultCount = fetchJobsCount( params )
        def resultMap = [
                result: results,
                length: resultCount,
                header: [
                        [name: "jobId", title: "actionItemPostId", options: [visible: false]],
                        [name: "jobStatus", title: MessageHelper.message( "aip.admin.actionItem.post.grid.job.status" ), options: [visible: true], width: 0],
                        [name: "jobName", title: MessageHelper.message( "aip.admin.actionItem.post.grid.job.name" ), options: [visible: true], width: 0],
                        [name: "postingStartScheduleDate", title: MessageHelper.message( "aip.admin.actionItem.post.grid.job.start-schedule.date" ), options: [visible: true], width: 0],
                        [name: "groupFolder", title: MessageHelper.message( "aip.admin.actionItem.post.grid.job.group.folder" ), options: [visible: true], width: 0],
                        [name: "population", title: MessageHelper.message( "aip.admin.actionItem.post.grid.job.population" ), options: [visible: true], width: 0],
                        [name: "groupName", title: MessageHelper.message( "aip.admin.actionItem.post.grid.job.group.name" ), options: [visible: true], width: 0],
                        [name: "submittedBy", title: MessageHelper.message( "aip.admin.actionItem.post.grid.job.submittedBy" ), options: [visible: true], width: 0]
                ]
        ]
        println resultMap
        resultMap
    }

    /**
     * Lists Post count
     *
     * @return
     */
    def fetchJobsCount( params ) {
        params.searchParam = params.searchParam ? ('%' + params.searchParam.toUpperCase() + '%') : ('%')
        ActionItemPostReadOnly.fetchJobsCount( params )
    }
}
