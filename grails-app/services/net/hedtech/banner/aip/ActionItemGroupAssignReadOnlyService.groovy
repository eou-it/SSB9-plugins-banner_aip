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
}
