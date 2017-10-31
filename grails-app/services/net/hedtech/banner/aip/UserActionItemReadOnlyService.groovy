/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class UserActionItemReadOnlyService extends ServiceBase {

    def listActionItems() {
        return UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
        //return itemByPidm.encodeAsHTML()
    }

    def listActionItemsByPidm(Long actionItemPidm) {
        return UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
       //return itemByPidm.encodeAsHTML()
    }

    def listActionItemByPidmWithinDate(Long ActionItemPidm) {
        return UserActionItemReadOnly.fetchUserActionItemsROByPidmDate( ActionItemPidm )
    }


    def listBlockingActionItemsByPidm( Long actionItemPidm ) {
        return UserActionItemReadOnly.fetchBlockingUserActionItemsROByPidm( actionItemPidm )
    }
}