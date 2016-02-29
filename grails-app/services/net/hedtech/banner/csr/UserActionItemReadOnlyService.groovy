/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

class UserActionItemReadOnlyService extends ServiceBase {

    /*
    def listActionItemById(Long actionItemId) {
        ActionItem itemById = UserActionItemReadOnly.fetchActionItemById( actionItemId )
        return itemById.actionItem.encodeAsHTML()
    }
    */

    def listActionItemByPidm(Long actionItemPidm) {
        UserActionItemReadOnly itemByPidm = UserActionItemReadOnly.fetchUserActionItemROByPidm( actionItemPidm )
        return itemByPidm.encodeAsHTML()
    }
}