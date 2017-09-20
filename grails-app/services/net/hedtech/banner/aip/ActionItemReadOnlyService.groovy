/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import net.hedtech.banner.i18n.MessageHelper

class ActionItemReadOnlyService extends ServiceBase {

    def getActionItemROById( Long aiId ) {
        return ActionItemReadOnly.fetchActionItemROById( aiId )
    }

    //simple return of all action items
    def listActionItemRO() {
        return ActionItemReadOnly.fetchActionItemRO()
    }


    def listActionItemROCount() {
        return ActionItemReadOnly.fetchActionItemROCount()
    }


    def listActionItemROByFolder( Long folderId ) {
        return ActionItemReadOnly.fetchActionItemROByFolder( folderId )
    }

    /**
     * Lists action Items
     * @param params
     * @return
     */
    def listActionItemsPageSort( Map params ) {

        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending: params?.sortAscending, max: params?.max, offset: params?.offset] )

        def resultCount = listActionItemROCount()
        def resultMap = [
                result: results?.collect {actionItem ->
                    [
                            id                     : actionItem.actionItemId,
                            actionItemId           : actionItem.actionItemId,
                            actionItemName         : actionItem.actionItemName,
                            actionItemTitle        : actionItem.actionItemTitle,
                            folderId               : actionItem.folderId,
                            folderName             : actionItem.folderName,
                            folderDesc             : actionItem.folderDesc,
                            actionItemStatus       : actionItem.actionItemStatus ? MessageHelper.message( "aip.status.${actionItem.actionItemStatus}" ) : null,
                            actionItemActivityDate : actionItem.actionItemActivityDate,
                            actionItemUserId       : actionItem.actionItemUserId,
                            actionItemContentUserId: actionItem.actionItemContentUserId,
                            actionItemCreatorId    : actionItem.actionItemCreatorId,
                            actionItemCreateDate   : actionItem.actionItemCreateDate,
                            actionItemCompositeDate: actionItem.actionItemCompositeDate,
                            actionItemLastUserId   : actionItem.actionItemLastUserId,
                            actionItemVersion      : actionItem.actionItemVersion,
                            actionItemTemplateId   : actionItem.actionItemTemplateId,
                            actionItemTemplateName : actionItem.actionItemTemplateName,
                            actionItemPageName     : actionItem.actionItemPageName,
                            actionItemContentId    : actionItem.actionItemContentId,
                            actionItemContentDate  : actionItem.actionItemContentDate,
                            actionItemContent      : actionItem.actionItemContent
                    ]
                },
                length: resultCount[0],
                header: [
                        [name: "actionItemId", title: "id", options: [visible: false, isSortable: true]],
                        [name: "actionItemName", title: MessageHelper.message( "aip.common.title" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemStatus", title: MessageHelper.message( "aip.common.status" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "folderName", title: MessageHelper.message( "aip.common.folder" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemActivityDate", title: MessageHelper.message( "aip.common.activity.date" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemLastUserId", title: MessageHelper.message( "aip.common.last.updated.by" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0]
                ]
        ]

        return resultMap

    }

}
