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
        results = results.collect {ActionItemPostReadOnly it ->
            [
                    id                     : it.postingId,
                    postingId              : it.postingId,
                    postingCurrentState    : it.postingCurrentState,
                    jobState               : MessageHelper.message( 'aip.action.item.post.job.state.' + ActionItemPostExecutionState.getStateEnum( it.postingCurrentState ) ),
                    postingName            : it.postingName,
                    postingDisplayStartDate: it.postingDisplayStartDate,
                    postingStartedDate     : it.postingStartedDate,
                    groupFolderName        : it.groupFolderName,
                    postingPopulation      : it.postingPopulation,
                    groupName              : it.groupName,
                    postingCreatorId       : it.postingCreatorId,
                    lastModified           : it.lastModified,
                    lastModifiedBy         : it.lastModifiedBy,
                    version                : it.version,


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
        ActionItemPostReadOnly.fetchJobsCount( params )
    }
}
