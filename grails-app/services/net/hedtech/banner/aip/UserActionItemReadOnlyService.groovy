/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class UserActionItemReadOnlyService extends ServiceBase {

    def listActionItemsByPidm(Long actionItemPidm) {
        return UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
       //return itemByPidm.encodeAsHTML()
    }
}