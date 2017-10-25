/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import net.hedtech.banner.service.ServiceBase
import org.omg.CORBA.portable.ApplicationException
import org.springframework.transaction.annotation.Propagation

class ActionItemGroupAssignService  extends ServiceBase  {

    def actionItemGroupAssignReadOnlyService

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def updateActionItemGroupAssignment(aipUser, inputGroupAssignments, Long groupId ) {
        def groupAssignment = ActionItemGroupAssign.fetchByGroupId(groupId)
        List<Long>inputAssignActionItemIds = inputGroupAssignments.actionItemId.toList().collect{new Long(it)}
        List<Long>existingAssignId = groupAssignment.actionItemId.toList()
        def deleteActionItems = existingAssignId - inputAssignActionItemIds

        //prepare create/update items
        List<ActionItemGroupAssign> assignList = []
        inputGroupAssignments.each { assignment ->
            def assign = ActionItemGroupAssign.fetchByActionItemIdAndGroupId(assignment.actionItemId ,groupId)
            if( assign.hasProperty("id")) {
                //update
                assign.groupId = groupId
                assign.actionItemId = assignment.actionItemId
                assign.seqNo = assignment.seq
                assign.activityDate = new Date()
                assign.userId = aipUser.username
            } else {
                //create
                assign = new ActionItemGroupAssign(
                        groupId: groupId,
                        actionItemId: assignment.actionItemId,
                        seqNo: assignment.seq,
                        activityDate: new Date(),
                        userId: aipUser.username
                )
            }
            assignList.push(assign)
        }
        List<Long> deleteList = []
        deleteActionItems.each { actionItemId ->
            def assign = ActionItemGroupAssign.fetchByActionItemIdAndGroupId(actionItemId, groupId)
            if (assign.hasProperty("id")) {
                deleteList.push(assign.id)
            } else {
                throw ApplicationException("Deleting item does not exist")
            }
        }

        def weGood = true
        List<ActionItemGroupAssignReadOnly> updatedActionItemGroupAssignment
        try {
            //do preupdate to test
            assignList.each {item -> this.preUpdate(item)}
        } catch (net.hedtech.banner.exceptions.ApplicationException ae) {
            weGood = false
            throw new ApplicationException("rollback", ae.message, ae.defaultMessage)
        }
        if (weGood) {
            try {
                //delete
                this.delete(deleteList)
                //createOrUpdate
                this.createOrUpdate(assignList, false)
                updatedActionItemGroupAssignment = actionItemGroupAssignReadOnlyService.getAssignedActionItemsInGroup(groupId)
            } catch (ApplicationException ae) {
                throw new ApplicationException("rollback", ae.message, ae.defaultMessage)
            }
        }
        return updatedActionItemGroupAssignment
    }

    def preCreate(domainModelOrMap) {

    }

    def preUpdate (domainModelOrMap) {
        preCreate(domainModelOrMap)
    }
}
