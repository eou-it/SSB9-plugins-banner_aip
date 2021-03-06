/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.actionitem

import grails.gorm.transactions.Transactional
import net.hedtech.banner.aip.ActionItemStatus
import net.hedtech.banner.aip.UserActionItem
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.grouppost.ActionItemPost
import net.hedtech.banner.aip.post.grouppost.ActionItemPostDetail
import net.hedtech.banner.aip.post.grouppost.ActionItemPostWork
import net.hedtech.banner.aip.post.grouppost.ActionItemPostWorkExecutionState
import net.hedtech.banner.aip.post.job.ActionItemJob
import net.hedtech.banner.aip.post.job.ActionItemJobStatus

/**
 * Service class for Action Item Perform Posting
 */
@Transactional
class ActionItemPerformPostService {
    def userActionItemService
    def actionItemJobService


    Map postActionItems( ActionItemPostWork actionItemPostWork ) {
        ActionItemPost groupSend = actionItemPostWork.actionItemGroupSend
        def currentExecutionState = ActionItemPostWorkExecutionState.Stopped
        def errorCode = null
        def errorText = null
        def successful = 0
        def insertedIds = []
        if (!groupSend.getPostingCurrentState().isTerminal()) {
            ActionItemJob actionItemJob = new ActionItemJob( referenceId: actionItemPostWork.referenceId, status: ActionItemJobStatus.PENDING, creationDateTime: new Date() )
            actionItemJobService.create( actionItemJob )

            def userPidm = actionItemPostWork.recipientPidm
            List<ActionItemPostDetail> details = ActionItemPostDetail.fetchByActionItemPostId( groupSend.id )
            def total = details.size()
            details.each {
                UserActionItem userActionItem = new UserActionItem()
                userActionItem.pidm = userPidm
                userActionItem.actionItemId = it.actionItemId
                userActionItem.status = ActionItemStatus.fetchDefaultActionItemStatus(actionItemPostWork.mepCode).id
                userActionItem.displayStartDate = groupSend.postingDisplayStartDate
                userActionItem.displayEndDate = groupSend.postingDisplayEndDate
                userActionItem.groupId = groupSend.postingActionItemGroupId
                userActionItem.creatorId = groupSend.postingCreatorId
                userActionItem.createDate = new Date()
                userActionItem.dataOrigin = groupSend.postingCreatorId

                if (userActionItem.validate()) {
                    if (!UserActionItem.isExistingInDateRangeForPidmAndActionItemId( userActionItem )) {
                        userActionItemService.create( userActionItem )
                        successful++
                        insertedIds.add( userActionItem.actionItemId )
                    }
                } else {
                    userActionItem.errors.allErrors.each {
                        errorText += it
                    }
                    errorCode = ActionItemErrorCode.INVALID_DATA_FIELD
                }
            }

            if (successful < total) { // FIXME: test for "not all sent"
                currentExecutionState = ActionItemPostWorkExecutionState.Partial
            } else {
                currentExecutionState = ActionItemPostWorkExecutionState.Complete
            }
        }
        [
                id                   : actionItemPostWork.id,
                version              : actionItemPostWork.version,
                currentExecutionState: currentExecutionState,
                insertedCount        : successful,
                insertedItemIds      : insertedIds,
                errorCode            : errorCode,
                errorText            : errorText,
                stopDate             : new Date()
        ]
    }
}
