/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemReadOnlyService extends ServiceBase {

    //simple return of all action items
    def listActionItemRO() {
        return ActionItemReadOnly.fetchActionItemRO()
    }

    def listActionItemROCount() {
        return ActionItemReadOnly.fetchActionItemROCount()
    }

    def listActionItemROByFolder(Long folderId) {
        return ActionItemReadOnly.fetchActionItemROByFolder( folderId )
    }

    def listActionItemsPageSort(Map params ) {

        def results =  ActionItemReadOnly.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending:params?.sortAscending, max: params?.max, offset: params?.offset] )

        def resultCount = listActionItemROCount(  )


       def resultMap = [
             result: results,
             length: resultCount[0]
       ]

        return resultMap

    }

}