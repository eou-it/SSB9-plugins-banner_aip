/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemGroupService extends ServiceBase {

    //simple return of all action items
    def listActionItemGroups() {
        return ActionItemGroup.fetchActionItemGroups( )
    }

    def listActionItemGroupById(Long actionItemGroupId) {
        return ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
    }

}