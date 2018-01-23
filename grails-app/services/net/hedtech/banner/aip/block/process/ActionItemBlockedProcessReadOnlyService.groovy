/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.aip.block.process

import net.hedtech.banner.aip.blocking.process.ActionItemBlockedProcessReadOnly
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for ActionItemBlockedProcessReadOnly
 */
class ActionItemBlockedProcessReadOnlyService extends ServiceBase {

    def fetchByActionItemId( Long actionItemId ) {
        ActionItemBlockedProcessReadOnly.fetchByActionItemId( actionItemId )
    }
}
