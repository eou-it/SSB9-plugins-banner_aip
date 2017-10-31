/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for GroupFolder ( read only ) Domain
 */
class GroupFolderReadOnlyService extends ServiceBase {

    /**
     * Return of all action items
     * @return
     */
    def listActionItemGroups() {
        GroupFolderReadOnly.fetchGroupFolders()
    }

    /**
     * Gets Action Item Group by Id
     * @param actionItemGroupId
     * @return
     */
    def getActionItemGroupById( Long actionItemGroupId ) {
        GroupFolderReadOnly groupRO = GroupFolderReadOnly.fetchGroupFolderById( actionItemGroupId )
        [
                groupId          : groupRO.groupId,
                groupTitle       : groupRO.groupTitle,
                groupName        : groupRO.groupName,
                groupStatus      : MessageHelper.message( "aip.status.${groupRO.groupStatus}" ),
                folderId         : groupRO.folderId,
                folderName       : groupRO.folderName,
                folderDesc       : groupRO.folderDesc,
                groupUserId      : groupRO.groupUserId,
                groupDesc        : groupRO.groupDesc,
                groupActivityDate: groupRO.groupActivityDate,
                groupVersion     : groupRO.groupVersion
        ]
    }

    /**Lists Group Folder count
     * @param params
     * @return
     */
    def listGroupFolderROCount( params ) {
        GroupFolderReadOnly.fetchGroupFolderROCount( params )
    }

    /**
     * Lists Group Folders
     * @param params
     * @param paginationParams
     * @return
     */
    def listGroupFolderPageSort( Map params, paginationParams ) {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams( params, paginationParams )
        results = results.collect {
            [id               : it.groupId,
             groupId          : it.groupId,
             groupTitle       : it.groupTitle,
             groupName        : it.groupName,
             groupDesc        : it.groupDesc,
             groupVersion     : it.groupVersion,
             groupStatus      : MessageHelper.message( "aip.status.$it.groupStatus" ),
             folderDesc       : it.folderDesc,
             folderId         : it.folderId,
             folderName       : it.folderName,
             groupUserId      : it.groupUserId,
             groupActivityDate: it.groupActivityDate
            ]
        }
        def resultCount = listGroupFolderROCount( params )
        def resultMap = [
                result: results,
                length: resultCount,
        ]

        resultMap
    }
}
