package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase


class UserBlockedProcessReadOnlyService extends ServiceBase {

    def getBlockedProcessesByPidmAndActionItemId( Long actionItemPidm, Long actionItemId ) {
        return UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId(
                actionItemPidm, actionItemId )
    }
}
