/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.sql.Sql
import net.hedtech.banner.aip.post.job.ActionItemJob
import org.apache.log4j.Logger

import java.sql.SQLException

/**
 * Process a group send item to the point of creating recipient merge data values and submitting an individual actionItem job
 * for the recipient.
 */
class ActionItemPostProcessorService {
    boolean transactional = true
    private static final log = Logger.getLogger(ActionItemPostProcessorService.class)
    def actionItemPostWorkService
    def actionItemJobService
    def sessionFactory
    def asynchronousBannerAuthenticationSpoofer
    private static final int noWaitErrorCode = 54;

    public void performGroupSendItem(Long groupSendItemId) {
        log.debug( "Performing group send item id = " + groupSendItemId )
        boolean locked = lockGroupSendItem( groupSendItemId, ActionItemPostWorkExecutionState.Ready );
        if (!locked) {
            // Do nothing
            return;
        }

        ActionItemPostWork groupSendItem = (ActionItemPostWork) actionItemPostWorkService.get( groupSendItemId )
        ActionItemPost groupSend = groupSendItem.actionItemGroupSend

        // TODO: see if we need this
        asynchronousBannerAuthenticationSpoofer.setMepProcessContext(sessionFactory.currentSession.connection(), groupSendItem.mepCode)
        if (log.isDebugEnabled()) log.debug("Spoofed as ${groupSend.createdBy} for creating recipient data.")

        if (!groupSend.getPostingCurrentState(  ).isTerminal()) {
            /* figure out referenceId in our case. We don't have multiple types
            ActionItemRecipientData recipientData = getRecipientDataFactory().create( groupSendItem )
            log.debug("Created recipient data with referenceId = " + groupSendItem.referenceId + ".")

            log.debug("Creating actionItem job with reference id = " + recipientData.referenceId)
            */
            //ActionItemJob actionItemJob = new ActionItemJob( referenceId: recipientData.referenceId )
            ActionItemJob actionItemJob = new ActionItemJob( referenceId: groupSendItem.referenceId )
            actionItemJobService.create( actionItemJob )

            //log.debug("Updating group send item to mark it complete with reference id = " + recipientData.referenceId)
            def groupSendItemParamMap = [
                id                   : groupSendItem.id,
                version              : groupSendItem.version,
                currentExecutionState: ActionItemPostWorkExecutionState.Complete,
                stopDate             : new Date()
            ]
            actionItemPostWorkService.update( groupSendItemParamMap )
        } else {
            def groupSendItemParamMap = [
                id                   : groupSendItem.id,
                version              : groupSendItem.version,
                currentExecutionState: ActionItemPostWorkExecutionState.Stopped,
                stopDate             : new Date()
            ]
            actionItemPostWorkService.update( groupSendItemParamMap )
        }
    }

    public void failGroupSendItem(Long groupSendItemId, String errorCode, String errorText ) {
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

    /*
    private ActionItemRecipientDataFactory getRecipientDataFactory() {
        return new ActionItemRecipientDataFactory(
                actionItemRecipientDataService: actionItemRecipientDataService,
                asynchronousBannerAuthenticationSpoofer: asynchronousBannerAuthenticationSpoofer
        )
    }
    */
}
