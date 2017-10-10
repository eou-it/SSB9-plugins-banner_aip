package net.hedtech.banner.aip

import grails.transaction.Transactional

@Transactional
class ActionItemGroupAssignReadOnlyService {

    def getAssignedActionItemsInGroup (Long groupId) {
        return ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignROByGroupId(groupId)
    }

    //simple return of all assigned items within all groups
    def listAllActionItemGroupAssignedRO () {
        return ActionItemGroupAssignReadOnly.fetchActionItemGroupAssignRO()
    }
}
