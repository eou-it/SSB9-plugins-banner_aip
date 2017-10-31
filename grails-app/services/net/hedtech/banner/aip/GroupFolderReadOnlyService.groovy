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
    // FIXME: is there one folder for a groupId? We should name the query singularly and return a single object
    def getActionItemGroupById( Long actionItemGroupId ) {
        def groupRO = GroupFolderReadOnly.fetchGroupFoldersById( actionItemGroupId )
        [
                groupId          : groupRO.groupId[0],
                groupTitle       : groupRO.groupTitle[0],
                groupName        : groupRO.groupName[0],
                groupStatus      : MessageHelper.message( "aip.status.${groupRO.groupStatus[0]}" ),
                folderId         : groupRO.folderId[0],
                folderName       : groupRO.folderName[0],
                folderDesc       : groupRO.folderDesc[0],
                groupUserId      : groupRO.groupUserId[0],
                groupDesc        : groupRO.groupDesc[0],
                groupActivityDate: groupRO.groupActivityDate[0],
                groupVersion     : groupRO.groupVersion[0]
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
