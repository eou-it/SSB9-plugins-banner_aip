/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import net.hedtech.banner.MessageUtility
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
import org.springframework.transaction.annotation.Propagation

import java.text.MessageFormat

/**
 * Composite Service for Action Item Group
 */

class ActionItemGroupCompositeService {

    def actionItemGroupService
    def groupFolderReadOnlyService
    def actionItemGroupAssignService
    def actionItemGroupAssignReadOnlyService

    /**
     * Creates a new group
     * @param map
     * @return
     */
    def createGroup( map ) {
        def success = false
        def message
        def group = new ActionItemGroup(
                title: map.groupTitle,
                name: map.groupName,
                folderId: map.folderId,
                description: map.groupDesc,
                postingInd: 'N',
                status: map.groupStatus ? (AIPConstants.STATUS_MAP.get( map.groupStatus )) : null,
                )
        def groupNew
        try {
            groupNew = actionItemGroupService.create( group )
            success = true
        } catch (ApplicationException e) {
            if (ActionItemGroupService.FOLDER_VALIDATION_ERROR.equals( e.getMessage() )) {
                message = MessageUtility.message( e.getDefaultMessage(), MessageFormat.format( "{0,number,#}", map.folderId ) )
            } else {
                message = MessageUtility.message( e.getDefaultMessage() )
            }
        }
        [
                success: success,
                message: message,
                group  : groupFolderReadOnlyService.getActionItemGroupById( groupNew.id.toInteger() )
        ]


    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def updateActionItemGroupAssignment(aipUser, map ) {
        def groupId = map.groupId
        def inputGroupAssignments = map.assignment
        def groupAssignment = actionItemGroupAssignService.fetchByGroupId(groupId)
        List<Long>inputAssignActionItemIds = inputGroupAssignments.actionItemId.toList().collect{new Long(it)}
        List<Long>existingAssignId = groupAssignment.actionItemId.toList()
        def deleteActionItems = existingAssignId - inputAssignActionItemIds

        //prepare create/update items
        List<ActionItemGroupAssign> assignList = []
        inputGroupAssignments.each { assignment ->
            def assign = actionItemGroupAssignService.fetchByActionItemIdAndGroupId(assignment.actionItemId ,groupId)
            if( assign.hasProperty("id")) {
                //update
                assign.groupId = groupId
                assign.actionItemId = assignment.actionItemId
                assign.seqNo = assignment.seq
                assign.lastModified = new Date()
                assign.lastModifiedBy = aipUser.username
            } else {
                //create
                assign = new ActionItemGroupAssign(
                        groupId: groupId,
                        actionItemId: assignment.actionItemId,
                        seqNo: assignment.seq
                )
            }
            assignList.push(assign)
        }
        List<Long> deleteList = []
        deleteActionItems.each { actionItemId ->
            def assign = actionItemGroupAssignService.fetchByActionItemIdAndGroupId(actionItemId, groupId)
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
            assignList.each {item -> actionItemGroupAssignService.preUpdate(item)}
        } catch (net.hedtech.banner.exceptions.ApplicationException ae) {
            weGood = false
            throw new ApplicationException("rollback", ae.message, ae.defaultMessage)
        }
        if (weGood) {
            try {
                actionItemGroupAssignService.delete(deleteList)
                actionItemGroupAssignService.createOrUpdate(assignList, false)
                updatedActionItemGroupAssignment = actionItemGroupAssignReadOnlyService.getAssignedActionItemsInGroup(groupId)
            } catch (ApplicationException ae) {
                throw new ApplicationException("rollback", ae.message, ae.defaultMessage)
            }
        }
        return updatedActionItemGroupAssignment
    }
}
