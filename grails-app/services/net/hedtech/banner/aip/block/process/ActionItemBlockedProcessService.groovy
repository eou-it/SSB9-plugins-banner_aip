/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase


/**
 * Service class for Action item Blocked Process
 */
@Transactional
class ActionItemBlockedProcessService extends ServiceBase {

    /**
     *
     * @return
     */
    def listBlockedActionItems() {
        ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
    }

    /**
     *
     * @param actionItemBlockId
     * @return
     */
    def listBlockedProcessById( Long actionItemBlockId ) {
        ActionItemBlockedProcess.fetchActionItemBlockProcessById( actionItemBlockId )
    }

    /**
     *
     * @param actionItemId
     * @return
     */
    def listBlockedProcessByActionItemId( Long actionItemId ) {
        ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId( actionItemId )
    }
}
