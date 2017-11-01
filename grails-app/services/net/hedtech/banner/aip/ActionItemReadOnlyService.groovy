/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase

class ActionItemReadOnlyService extends ServiceBase {

    /**
     *
     * @param aiId
     * @return
     */
    def getActionItemROById( Long aiId ) {
        ActionItemReadOnly.fetchActionItemROById( aiId )
    }

    /**
     *
     * @return
     */
    def listActionItemRO() {
        ActionItemReadOnly.fetchActionItemRO()
    }

    /**
     *
     * @param param
     * @return
     */
    def listActionItemROCount( param ) {
        ActionItemReadOnly.fetchActionItemROCount( param )
    }

    /**
     * Lists action Items
     * @param params
     * @return
     */
    def listActionItemsPageSort( Map params ) {

        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: params?.filterName],
                [sortColumn: params.sortColumn, sortAscending: params.sortAscending, max: params.max, offset: params.offset] )

        def resultCount = listActionItemROCount( [name: params?.filterName] )
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
                            actionItemStatus       : actionItem.actionItemStatus ? MessageHelper.message( "aip.status.${actionItem.actionItemStatus.trim()}" ) : null,
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
                length: resultCount,
                header: [
                        [name: "actionItemId", title: "id", options: [visible: false, isSortable: true]],
                        [name: "actionItemName", title: MessageHelper.message( "aip.common.title" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemStatus", title: MessageHelper.message( "aip.common.status" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "folderName", title: MessageHelper.message( "aip.common.folder" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemActivityDate", title: MessageHelper.message( "aip.common.activity.date" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemLastUserId", title: MessageHelper.message( "aip.common.last.updated.by" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0]
                ]
        ]
        resultMap
    }

}
