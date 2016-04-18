/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import net.hedtech.banner.service.ServiceBase

class ActionItemService extends ServiceBase {

    //simple return of all action items
    def listActionItems() {
        return ActionItem.fetchActionItems( )
    }

}