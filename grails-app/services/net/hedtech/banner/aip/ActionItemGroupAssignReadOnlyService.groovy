/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

class ActionItemGroupAssignReadOnlyService {

    def getAssignedActionItemsInGroup( Long groupId ) {
        ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignROByGroupId( groupId )
    }


    def listAllActionItemGroupAssignedRO() {
        ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignRO()
    }

    /**
     *
     * @param groupId
     * @param paginationParam
     * @return
     */
    def fetchActiveActionItemByGroupId( groupId, paginationParam ) {
        ActionItemGroupAssignReadOnly.fetchActiveActionItemByGroupId( groupId, paginationParam ).collect {
            [actionItemId        : it[0],
             actionItemName      : it[1],
             actionItemTitle     : it[2],
             actionItemFolderName: it[3]]
        }
    }

    /**
     *
     * @param searchParam
     * @param paginationParam
     * @return
     */
    def fetchGroupLookup( searchParam , paginationParam) {
        def data = ActionItemGroupAssignReadOnly.fetchGroupLookup( '%' + (searchParam ? searchParam.toUpperCase() : '') + '%', paginationParam ).collect() {
            [folderId: it[0], folderName: it[1], groupId: it[2], groupName: it[3], groupTitle: it[4]]
        }
        Map map = [:]
        if (data) {
            Map folder = data.collectEntries() {
                [it.folderId, it.folderName]
            }
            println folder.keySet()

            folder.keySet().each {key ->
                map.put( folder.get( key ), data.findAll() {
                    (it.folderId == key)
                }.collect() {
                    [groupId   : it.groupId,
                     groupName : it.groupName,
                     groupTitle: it.groupTitle]
                } )
            }
        }
        map
    }
}
