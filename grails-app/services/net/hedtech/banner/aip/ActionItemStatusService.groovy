/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemStatusService extends ServiceBase {

    def listActionItemStatusById( Long statusId) {
        return ActionItemStatus.fetchActionItemStatusById( statusId )
    }

    //simple return of all action items
    def listActionItemStatuses() {
        return ActionItemStatus.fetchActionItemStatuses()
    }


    def listActionItemStatusCount() {
        return ActionItemStatus.fetchActionItemStatusCount()
    }

    def listActionItemsPageSort( Map params ) {

        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending: params?.sortAscending, max: params?.max, offset: params?.offset] )

        def resultCount = listActionItemStatusCount()


        def resultMap = [
                result: results,
                length: resultCount[0]
        ]

        return resultMap

    }

}