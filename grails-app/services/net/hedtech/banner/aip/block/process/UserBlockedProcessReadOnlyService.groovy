/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.aip.block.process

import net.hedtech.banner.aip.blocking.process.UserBlockedProcessReadOnly
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for UserBlockedProcessReadOnly
 */
class UserBlockedProcessReadOnlyService extends ServiceBase {

    def getBlockedProcessesByPidmAndActionItemId( Long actionItemPidm, Long actionItemId ) {
        UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId(
                actionItemPidm, actionItemId )
    }
}
