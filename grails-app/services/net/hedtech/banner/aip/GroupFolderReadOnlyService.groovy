/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class GroupFolderReadOnlyService extends ServiceBase {

    //simple return of all action items
    def listActionItemGroups() {
        return GroupFolderReadOnly.fetchGroupFolders( )
    }

    def getActionItemGroupById(Long actionItemGroupId) {
        return GroupFolderReadOnly.fetchGroupFoldersById( actionItemGroupId )
    }

    def listGroupFolderROCount() {
        return GroupFolderReadOnly.fetchGroupFolderROCount(  )
    }

    def listGroupFolderPageSort(Map params ) {
        def results =  GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending:params?.sortAscending, max: params?.max, offset: params?.offset] )

        def resultCount = listGroupFolderROCount(  )
        def resultMap = [
                result: results,
                length: resultCount[0]
        ]

        return resultMap
    }

}