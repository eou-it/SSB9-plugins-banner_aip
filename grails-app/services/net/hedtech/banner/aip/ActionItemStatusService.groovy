/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Status
 */
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
        ActionItemStatus.fetchActionItemStatuses()
    }

    /**
     *
     * @return
     */
    def listActionItemStatusCount() {
        ActionItemStatus.fetchActionItemStatusCount()
    }

    /**
     * @param actionItemStatus
     * @return
     */
    def checkIfNameAlreadyPresent( actionItemStatus ) {
        ActionItemStatus.checkIfNameAlreadyPresent( actionItemStatus )
    }
}
