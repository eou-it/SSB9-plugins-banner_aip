/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

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
     * Return of all groups. It has folder information
     * @return
     */
    def fetchGroupLookup( searchParam ) {
        def data = GroupFolderReadOnly.fetchGroupLookup( '%' + (searchParam ? searchParam.toUpperCase() : '') + '%' ).collect() {
            [folderId: it[0], folderName: it[1], groupId: it[2], groupName: it[3], groupTitle: it[4]]
        }
        Map map = [:]
        if (data) {
            Map folder = data.collectEntries() {
                [it.folderId, it.folderName]
            }
            println folder.keySet()

            folder.keySet().each {key ->
                map.put( folder.get( key ), data.findAll() {
                    (it.folderId == key)
                }.collect() {
                    [groupId   : it.groupId,
                     groupName : it.groupName,
                     groupTitle: it.groupTitle]
                } )
            }
        }
        map
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
     *
     * @return
     */
    def listGroupFolderROCount() {
        GroupFolderReadOnly.fetchGroupFolderROCount()
    }

    /**
     * Lists Group Folders
     * @param params
     * @return
     */
    def listGroupFolderPageSort( Map params ) {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending: params?.sortAscending, max: params?.max, offset: params?.offset] )
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
        def resultCount = listGroupFolderROCount()
        def resultMap = [
                result: results,
                length: resultCount[0]
        ]

        resultMap
    }
}
