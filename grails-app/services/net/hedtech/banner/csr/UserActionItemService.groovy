/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

class ActionItemService extends ServiceBase {

    def listActionItemById(Long actionItemId) {
        ActionItem itemById = ActionItem.fetchActionItemById( actionItemId )
        return itemById.actionItem.encodeAsHTML()
    }


    def listActionItemByPidm(Long actionItemPidm) {
        ActionItem itemByPidm = ActionItem.fetchActionItemByPidm( actionItemPidm )
        return itemByPidm.actionItem.encodeAsHTML()
    }
}