/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase

class ActionItemReadOnlyCompositeService extends ServiceBase {
    def actionItemReadOnlyService
    def actionItemGroupAssignService

    /**
     *
     * @param actionItemId
     * @param postingInd
     * @return
     */
    def checkIfActionItemDeletable( actionItemId, postingInd ) {
        def res = [message: null, deletable: true]
        if (postingInd == AIPConstants.YES_IND) {
            res.deletable = false
            res.message = MessageHelper.message( 'action.item.not.deletable.mark.as.posted' )
            return res
        }
        if (actionItemGroupAssignService.checkIfAssignedGroupPresentForSpecifiedActionItem( actionItemId )) {
            res.deletable = false
            res.message = MessageHelper.message( 'action.item.not.deletable.assigned.to.group' )
        }
        res
    }

    /**
     * Lists action Items
     * @param params
     * @return
     */
    def listActionItemsPageSort( Map params ) {
        def results = actionItemReadOnlyService.fetchWithPagingAndSortParams( params )
        def resultCount = actionItemReadOnlyService.listActionItemROCount( [name: params?.filterName] )
        def resultMap = [
                result: results?.collect {ActionItemReadOnly actionItem ->
                    def deletableResult = checkIfActionItemDeletable( actionItem.actionItemId, actionItem.actionItemPostedStatus )
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
                            actionItemContent      : actionItem.actionItemContent,
                            deletable              : deletableResult.deletable,
                            cantDeleteMessage      : deletableResult.message
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