/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.MessageUtility
import net.hedtech.banner.service.ServiceBase
import net.hedtech.banner.i18n.MessageHelper

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
        GroupFolderReadOnly.fetchGroupFoldersById( actionItemGroupId )
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
                header: [
                        [name: "groupId", title: "id", options: [visible: false, isSortable: true]],
                        [name: "groupName", title: MessageHelper.message( "aip.common.group.name" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "groupTitle", title: MessageHelper.message( "aip.common.group.title" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "groupStatus", title: MessageHelper.message( "aip.common.status" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "folderName", title: MessageHelper.message( "aip.common.folder" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "groupActivityDate", title: MessageHelper.message( "aip.common.activity.date" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "groupUserId", title: MessageHelper.message( "aip.common.last.updated.by" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0]
                ]
        ]

        resultMap
    }
}
