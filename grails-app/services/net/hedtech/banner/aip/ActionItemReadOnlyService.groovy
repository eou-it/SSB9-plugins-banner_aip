/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemReadOnlyService extends ServiceBase {

    //simple return of all action items
    def listActionItemRO() {
        return ActionItemReadOnly.fetchActionItemRO()
    }

    def listActionItemROByFolder(Long folderId) {
        return ActionItemReadOnly.fetchActionItemROByFolder( folderId )
    }

}