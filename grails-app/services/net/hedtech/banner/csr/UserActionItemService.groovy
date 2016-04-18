/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import net.hedtech.banner.service.ServiceBase

class UserActionItemService extends ServiceBase {

    def listActionItemById(Long actionItemId) {
        return UserActionItem.fetchUserActionItemById( actionItemId )
        //return itemById.encodeAsHTML()
    }


    def listActionItemByPidm(Long actionItemPidm) {
       return UserActionItem.fetchUserActionItemByPidm( actionItemPidm )
        //return itemByPidm.encodeAsHTML()
    }
}