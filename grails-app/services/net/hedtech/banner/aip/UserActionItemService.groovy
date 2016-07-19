/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class UserActionItemService extends ServiceBase {

    def getActionItemById(Long actionItemId) {
        return UserActionItem.fetchUserActionItemById( actionItemId )
    }


    def listActionItemsByPidm(Long actionItemPidm) {
       return UserActionItem.fetchUserActionItemsByPidm( actionItemPidm )
    }
}