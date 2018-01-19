package net.hedtech.banner.aip

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
