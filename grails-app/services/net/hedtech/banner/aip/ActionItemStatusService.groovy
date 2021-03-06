/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Status
 */
@Transactional
class ActionItemStatusService extends ServiceBase {
    /**
     *
     * @param statusId
     * @return
     */
    def listActionItemStatusById( Long statusId ) {
        ActionItemStatus.fetchActionItemStatusById( statusId )
    }

    /**
     *
     * @return
     */
    def listActionItemStatuses() {
        ActionItemStatus.fetchActionItemStatuses()?.collect {ActionItemStatus it ->
            [
                    id                            : it.id,
                    version                       : it.version,
                    actionItemStatus              : it.actionItemStatus,
                    actionItemStatusActive        : it.actionItemStatusActive,
                    actionItemStatusActivityDate  : it.lastModified,
                    actionItemStatusBlockedProcess: it.actionItemStatusBlockedProcess,
                    actionItemStatusDataOrigin    : it.dataOrigin,
                    actionItemStatusDefault       : it.actionItemStatusDefault,
                    actionItemStatusSystemRequired: it.actionItemStatusSystemRequired
            ]
        }
    }

    /**
     * @param actionItemStatus
     * @return
     */
    def listActionItemStatusCount(actionItemStatus) {
        ActionItemStatus.fetchActionItemStatusCount(actionItemStatus)
    }

    /**
     * @param actionItemStatus
     * @return
     */
    def checkIfNameAlreadyPresent( actionItemStatus ) {
        ActionItemStatus.checkIfNameAlreadyPresent( actionItemStatus )
    }
}
