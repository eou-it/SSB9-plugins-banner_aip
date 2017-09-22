/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for UserActionItem
 */
class UserActionItemService extends ServiceBase {

    /**
     *
     * @param actionItemId
     * @return
     */
    def getActionItemById(Long actionItemId) {
        UserActionItem.fetchUserActionItemById( actionItemId )
    }

    /**
     *
     * @param actionItemPidm
     * @return
     */
    def listActionItemsByPidm(Long actionItemPidm) {
       UserActionItem.fetchUserActionItemsByPidm( actionItemPidm )
    }
}
