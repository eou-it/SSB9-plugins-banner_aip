/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

class ActionItemGroupAssignReadOnlyService {

    def getAssignedActionItemsInGroup( Long groupId ) {
        ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignROByGroupId( groupId )
    }

    //simple return of all assigned items within all groups
    def listAllActionItemGroupAssignedRO() {
        ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignRO()
    }
}
