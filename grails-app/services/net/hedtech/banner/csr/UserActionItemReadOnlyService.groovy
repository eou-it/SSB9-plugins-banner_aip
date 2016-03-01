/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

class UserActionItemReadOnlyService extends ServiceBase {

    def listActionItemByPidm(Long actionItemPidm) {
        return UserActionItemReadOnly.fetchUserActionItemROByPidm( actionItemPidm )
       //return itemByPidm.encodeAsHTML()
    }
}