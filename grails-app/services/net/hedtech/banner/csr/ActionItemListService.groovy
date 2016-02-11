/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

class ActionItemListService extends ServiceBase {

    //simple return of all action items
        def listActionItems() {
            return ActionItemList.fetchActionItems()
        }

}

