/*******************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.gorm.transactions.Transactional
import groovy.sql.Sql
import groovy.util.logging.Slf4j
import net.hedtech.banner.aip.post.ActionItemErrorCode


import java.sql.SQLException

/**
 * Process a group send item to the point of creating recipient merge data values and submitting an individual ActionItem job
 * for the recipient.
 */
@Slf4j
@Transactional
class ActionItemPostWorkProcessorService {



    def actionItemPerformPostService
    def actionItemPostWorkService
    def sessionFactory
    def asynchronousBannerAuthenticationSpoofer

    private static final int noWaitErrorCode = 54

    void performPostItem( ActionItemPostWork actionItemPostWork ) {

        asynchronousBannerAuthenticationSpoofer.setMepContext( sessionFactory.currentSession.connection(), actionItemPostWork.mepCode )

        def groupSendItemId = actionItemPostWork.id
        log.debug( "Performing group send item id = " + groupSendItemId )
        boolean locked = lockGroupSendItem( groupSendItemId, ActionItemPostWorkExecutionState.Ready )
        if (!locked) {
            // Do nothing
            return
        }
        actionItemPostWorkService.update( actionItemPerformPostService.postActionItems( actionItemPostWork ) )
    }

    /**
     *
     * @param groupSendItemId
     * @param errorCode
     * @param errorText
     */
     void failGroupSendItem( Long groupSendItemId, String errorCode, String errorText ) {
        ActionItemPostWork groupSendItem = (ActionItemPostWork) actionItemPostWorkService.get( groupSendItemId )

        asynchronousBannerAuthenticationSpoofer.setMepContext( sessionFactory.currentSession.connection(), groupSendItem.mepCode )

        def groupSendItemParamMap = [
                id                   : groupSendItem.id,
                version              : groupSendItem.version,
                currentExecutionState: ActionItemPostWorkExecutionState.Failed,
                stopDate             : new Date(),
                errorText            : errorText,
                errorCode            : ActionItemErrorCode.valueOf( errorCode )
        ]

        log.warn( "Group send item failed id = ${groupSendItemId}, errorText = ${errorText}." )

        actionItemPostWorkService.update( groupSendItemParamMap )
    }

    /**
     * Attempts to create a pessimistic lock on the group send item record.
     * @param groupSendItemId the primary key of the group send item.
     * @param state the group send item execution state
     * @return true if the record was successfully locked and false otherwise
     */
     boolean lockGroupSendItem( final Long groupSendItemId, final ActionItemPostWorkExecutionState state ) {
        Sql sql = null
        try {
            sql = new Sql( sessionFactory.getCurrentSession().connection() )
            def rows = sql.rows( "select GCRAIIM_SURROGATE_ID from GCRAIIM where GCRAIIM_SURROGATE_ID = ? and GCRAIIM_CURRENT_STATE = ? for update " +
                                         "nowait",
                                 [groupSendItemId, state.name()],
                                 0, 2
            )
            return rows.size() == 1
        } catch (SQLException e) {
            if (e.getErrorCode() == noWaitErrorCode) {
                return false
            } else {
                throw e
            }
        }
    }
}
