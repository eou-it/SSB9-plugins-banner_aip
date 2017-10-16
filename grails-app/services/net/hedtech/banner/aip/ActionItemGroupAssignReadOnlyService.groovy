/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

class ActionItemGroupAssignReadOnlyService {

    def getAssignedActionItemsInGroup( Long groupId ) {
        return ActionItemGroupAssignReadOnly.fetchByGroupId( groupId )
    }

    //simple return of all assigned items within all groups
    def listAllActionItemGroupAssignedRO() {
        return ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignRO()
    }
}
