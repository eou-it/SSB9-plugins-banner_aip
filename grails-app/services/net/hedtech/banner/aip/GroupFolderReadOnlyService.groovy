/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class GroupFolderReadOnlyService extends ServiceBase {

    //simple return of all action items
    def listActionItemGroups() {
        return GroupFolderReadOnly.fetchGroupFolders( )
    }

    def getActionItemGroupById(Long actionItemGroupId) {
        return GroupFolderReadOnly.fetchGroupFoldersById( actionItemGroupId )
    }

}