/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.sql.Sql
import net.hedtech.banner.aip.UserActionItem
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.job.ActionItemJob
import net.hedtech.banner.aip.post.job.ActionItemJobStatus
import org.apache.log4j.Logger

import java.sql.SQLException

/**
 * Process a group send item to the point of creating recipient merge data values and submitting an individual ActionItem job
 * for the recipient.
 */
class ActionItemPostWorkProcessorService {
    boolean transactional = true
    private static final log = Logger.getLogger(ActionItemPostWorkProcessorService.class)
    def actionItemPostWorkService
    def userActionItemService
    def actionItemJobService
    def sessionFactory

    private static final int noWaitErrorCode = 54;


    public void performPostItem( ActionItemPostWork actionItemPostWork ) {
        def groupSendItemId = actionItemPostWork.id
        log.debug( "Performing group send item id = " + groupSendItemId )
        boolean locked = lockGroupSendItem( groupSendItemId, ActionItemPostWorkExecutionState.Ready );
        if (!locked) {
            println "CRR: !locked"
            // Do nothing
            return;
        }

        ActionItemPost groupSend = actionItemPostWork.actionItemGroupSend
        println "CRR: groupSend: " + groupSend
        def currentExecutionState = ActionItemPostWorkExecutionState.Stopped
        def errorCode = null
        def errorText = null
        if (!groupSend.getPostingCurrentState().isTerminal()) {
            ActionItemJob actionItemJob = new ActionItemJob( referenceId: actionItemPostWork.referenceId, status: ActionItemJobStatus.PENDING )
            actionItemJobService.create( actionItemJob )

            //FIXME: CRR Do all the work of posting action Item here.
            //FIXME: factor out
            // Assign an action item to the pidm for each in group
            // get all the actionItem ids (ACTM_ID) from
            // GCRAPST -> list of GCBAPST_ID -> each GCBACTM_ID

            def userPidm = actionItemPostWork.recipientPidm
            List<ActionItemPostDetail> details = ActionItemPostDetail.fetchByActionItemPostId( groupSend.id )
            def total = details.size()
            def successful = 0

            details.each {
                println userPidm + ":" + it.actionItemId
                UserActionItem userActionItem = new UserActionItem()
                userActionItem.pidm = userPidm
                userActionItem.actionItemId = it.actionItemId
                userActionItem.status = 1
                userActionItem.displayStartDate = groupSend.postingDisplayStartDate
                userActionItem.displayEndDate = groupSend.postingDisplayEndDate
                userActionItem.groupId = groupSend.postingActionItemGroupId
                userActionItem.userId = groupSend.postingCreatorId
                userActionItem.activityDate = new Date()
                userActionItem.creatorId = groupSend.postingCreatorId
                userActionItem.createDate = new Date()
                userActionItem.dataOrigin = groupSend.postingCreatorId

                if (userActionItem.validate()) {
                    if (!UserActionItem.isExistingInDateRangeForPidmAndActionItemId( userActionItem )) {
                        userActionItemService.create( userActionItem )
                        successful++
                    }
                } else {
                    println "CRR: something else happened"
                    userActionItem.errors.allErrors.each {
                        errorText += it
                    }
                    errorCode = ActionItemErrorCode.INVALID_DATA_FIELD
                }
            }

            // FIXME: refactor all of this redundant code
            if (successful < total) { // FIXME: test for "not all sent"
                println "crr success: " + successful
                println "total: " + total
                currentExecutionState = ActionItemPostWorkExecutionState.Partial
            } else {
                currentExecutionState = ActionItemPostWorkExecutionState.Complete
            }

            // TODO: make sure this is what we want to do. Probably no need to keep around these success logs
            // deletesuccessfuljobs a configuration option?
            // actionItemJobService.delete( actionItemJob )
        }
        def groupSendParamMap = [
                id                   : actionItemPostWork.id,
                version              : actionItemPostWork.version,
                currentExecutionState: currentExecutionState,
                errorCode            : errorCode,
                errorText            : errorText,
                stopDate             : new Date()
        ]
        actionItemPostWorkService.update( groupSendParamMap )
    }


    public void failGroupSendItem( Long groupSendItemId, String errorCode, String errorText ) {
        ActionItemPostWork groupSendItem = (ActionItemPostWork) actionItemPostWorkService.get( groupSendItemId )
        def groupSendItemParamMap = [
                id                   : groupSendItem.id,
                version              : groupSendItem.version,
                currentExecutionState: ActionItemPostWorkExecutionState.Failed,
                stopDate             : new Date(),
                errorText            : errorText,
                errorCode            : errorCode
        ]

        log.warn("Group send item failed id = ${groupSendItemId}, errorText = ${errorText}.")

        actionItemPostWorkService.update(groupSendItemParamMap)
    }


    /**
     * Attempts to create a pessimistic lock on the group send item record.
     * @param groupSendItemId the primary key of the group send item.
     * @param state the group send item execution state
     * @return true if the record was successfully locked and false otherwise
     */
    public boolean lockGroupSendItem(final Long groupSendItemId, final ActionItemPostWorkExecutionState state) {
        Sql sql = null
        try {
            sql = new Sql(sessionFactory.getCurrentSession().connection())
            def rows = sql.rows("select GCRAIIM_SURROGATE_ID from GCRAIIM where GCRAIIM_SURROGATE_ID = ? and GCRAIIM_CURRENT_STATE = ? for update " +
                    "nowait",
                    [groupSendItemId, state.name()],
                    0, 2
            )

            if (rows.size() > 1) {
                throw new RuntimeException("Found more than one GCRAIIM row for a single group send item id")
            } else {
                return rows.size() == 1
            }

        } catch (SQLException e) {
            if (e.getErrorCode() == noWaitErrorCode) {
                return false
            } else {
                throw e
            }
        } finally {
            sql?.close() // note that the test will close the connection, since it's our current session's connection
        }
    }
}
