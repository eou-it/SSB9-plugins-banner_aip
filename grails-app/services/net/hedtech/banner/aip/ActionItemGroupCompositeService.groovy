/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import grails.validation.ValidationException
import net.hedtech.banner.MessageUtility
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.i18n.MessageHelper
import org.springframework.transaction.annotation.Propagation

import java.text.MessageFormat

/**
 * Composite Service for Action Item Group
 */

class ActionItemGroupCompositeService {

    def actionItemGroupService
    def actionItemGroupAssignService
    def actionItemGroupAssignReadOnlyService
    def groupFolderReadOnlyService

    /**
     * Creates a new group or update existing
     * @param map
     * @return
     */
    def createOrUpdateGroup( map ) {
        def success = false
        def message
        def group
        if (map.group.groupId) {
            group = actionItemGroupService.getActionItemGroupById(map.group.groupId.longValue())
            group.description = map.group.groupDesc
            group.title = map.group.groupTitle
            group.status = AIPConstants.STATUS_MAP.get(map.group.groupStatus)
        } else {
            group = new ActionItemGroup(
                    title: map.group.groupTitle,
                    name: map.group.groupName,
                    folderId: map.group.folderId,
                    description: map.group.groupDesc,
                    postingInd: AIPConstants.NO_IND,
                    status: map.group.groupStatus ? (AIPConstants.STATUS_MAP.get(map.group.groupStatus)) : null,
            )
        }
        def groupNew
        try {
            groupNew = actionItemGroupService.createOrUpdate( group )
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
                group  : success ? groupFolderReadOnlyService.getActionItemGroupById( groupNew.id.toInteger() ) : null
        ]


    }

    /**
     * Delete an existing group
     * @param map
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def deleteGroup( map ) {
        def success = false
        def message
        try {
            ActionItemGroup group = actionItemGroupService.get( map.groupId )
            if (group.postingInd == AIPConstants.YES_IND) {
                return [success: success, message: MessageHelper.message( 'group.not.deletable.mark.as.posted' )]
            }
            List<ActionItemGroupAssign> actionItemGroupAssignList = actionItemGroupAssignService.fetchByGroupId( group.id )
            actionItemGroupAssignList.each {
                actionItemGroupAssignService.delete( it )
            }
            actionItemGroupService.delete( group )
            success = true
            message = MessageHelper.message( "action.group.delete.success" )
        } catch (ApplicationException e) {
            success = false
            message = e.message
        }
        [
                success: success,
                message: message
        ]
    }


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def updateGroupAssignment( map ) {
        def groupId = map.groupId
        def inputGroupAssignments = map.assignment
        def groupAssignment = actionItemGroupAssignService.fetchByGroupId( groupId )
        List<Long> inputAssignActionItemIds = inputGroupAssignments.actionItemId.toList().collect {new Long( it )}
        List<Long> existingAssignId = groupAssignment.actionItemId.toList()
        def deleteActionItems = existingAssignId - inputAssignActionItemIds

        //prepare create/update items
        List<ActionItemGroupAssign> assignList = []
        inputGroupAssignments.each {assignment ->
            def assign = actionItemGroupAssignService.fetchByActionItemIdAndGroupId( assignment.actionItemId, groupId )
            if (assign.hasProperty( "id" )) {
                //update
                assign.groupId = groupId
                assign.actionItemId = assignment.actionItemId
                assign.seqNo = assignment.seq
            } else {
                //create
                assign = new ActionItemGroupAssign(
                        groupId: groupId,
                        actionItemId: assignment.actionItemId,
                        seqNo: assignment.seq
                )
            }
            assignList.push( assign )
        }
        List<Long> deleteList = []
        deleteActionItems.each {actionItemId ->
            def assign = actionItemGroupAssignService.fetchByActionItemIdAndGroupId( actionItemId, groupId )
            if (assign.hasProperty( "id" )) {
                deleteList.push( assign.id )
            } else {
                throw ApplicationException( "Deleting item does not exist" )
            }
        }

        def weGood = false
        List<ActionItemGroupAssignReadOnly> updatedActionItemGroupAssignment
        try {
            //do preupdate to test
            assignList.each {item -> actionItemGroupAssignService.validate( item )}
            weGood = true
        } catch (ValidationException ae) {
            throw new ApplicationException( ActionItemGroupAssign, "@@r1:DomainInvalidError@@", "actionItemGroupAssign.invalid.error" )
        }
        if (weGood) {
            try {
                actionItemGroupAssignService.delete( deleteList )
                actionItemGroupAssignService.createOrUpdate( assignList, false )
                updatedActionItemGroupAssignment = actionItemGroupAssignReadOnlyService.getAssignedActionItemsInGroup( groupId )
            } catch (ApplicationException ae) {
                throw new ApplicationException( "rollback", ae.message, ae.defaultMessage )
            }
        }
        updatedActionItemGroupAssignment
    }

    /**
     * Update Action item Group Assignment
     * @return
     */
    def updateActionItemGroupAssignment( map ) {
        def result
        try {
            List<ActionItemGroupAssignReadOnly> assignActionItem = updateGroupAssignment( map )
            def resultMap
            if (assignActionItem) {
                resultMap = assignActionItem.collect {it ->
                    [
                            id                  : it.id,
                            actionItemId        : it.actionItemId,
                            sequenceNumber      : it.sequenceNumber,
                            actionItemName      : it.actionItemName,
                            actionItemStatus    : it.actionItemStatus ? MessageHelper.message( "aip.status.${it.actionItemStatus.trim()}" ) : null,
                            actionItemFolderName: it.actionItemFolderName,
                            actionItemTitle     : it.actionItemTitle,
                            actionItemFolderId  : it.actionItemFolderId
                    ]
                }
            }
            result = [
                    success              : true,
                    actionItemGroupAssign: resultMap
            ]
        } catch (ApplicationException ae) {
            result = [
                    success              : false,
                    message              : MessageUtility.message( ae.getDefaultMessage() ),
                    actionItemGroupAssign: ""
            ]
        }
        result
    }
}
