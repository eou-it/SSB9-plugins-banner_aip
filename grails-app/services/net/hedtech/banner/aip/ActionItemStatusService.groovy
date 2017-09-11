/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemStatusService extends ServiceBase {

    def listActionItemStatusById( Long statusId ) {
        return ActionItemStatus.fetchActionItemStatusById( statusId )
    }

    //simple return of all action items
    def listActionItemStatuses() {
        return ActionItemStatus.fetchActionItemStatuses()
    }

    def listActionItemStatusCount() {
        return ActionItemStatus.fetchActionItemStatusCount()
    }
}
