/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.aip.block.process

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for ActionItemBlockedProcessReadOnly
 */
class ActionItemBlockedProcessReadOnlyService extends ServiceBase {

    /**
     *
     * @param actionItemId
     * @return
     */
    def fetchByActionItemId( Long actionItemId ) {
        ActionItemBlockedProcessReadOnly.fetchByActionItemId( actionItemId )
    }

    /**
     *
     * @param actionItemIds
     * @return
     */
    def fetchByListOfActionItemIds( List actionItemIds ) {
        ActionItemBlockedProcessReadOnly.fetchByListOfActionItemIds( actionItemIds )
    }
}
