/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

class ActionItemGroupAssignReadOnlyService {

    def getAssignedActionItemsInGroup( Long groupId ) {
        return ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignROByGroupId( groupId )
    }


    def listAllActionItemGroupAssignedRO() {
        return ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignRO()
    }

    /**
     *
     * @param groupId
     * @return
     */
    def fetchActiveActionItemByGroupId( groupId ) {
        ActionItemGroupAssignReadOnly.fetchActiveActionItemByGroupId( groupId ).collect {
            [actionItemId        : it[0],
             actionItemName      : it[1],
             actionItemTitle     : it[2],
             actionItemFolderName: it[3]]
        }
    }

    /**
     * Fetches active groups which has active action itesm
     * @return
     */
    def fetchGroupLookup() {
        ActionItemGroupAssignReadOnly.fetchGroupLookup().collect() {
            [folderId: it[0], folderName: it[1], groupId: it[2], groupName: it[3], groupTitle: it[4]]
        }
    }
}
