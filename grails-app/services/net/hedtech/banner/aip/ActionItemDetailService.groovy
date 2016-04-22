/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemDetailService extends ServiceBase {

    def listActionItemDetailById(Long actionItemId) {
        return ActionItemDetail.fetchActionItemDetailById( actionItemId )
    }

}