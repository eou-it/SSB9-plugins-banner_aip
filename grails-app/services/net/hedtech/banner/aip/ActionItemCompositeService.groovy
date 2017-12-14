/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.i18n.MessageHelper
import org.springframework.transaction.annotation.Propagation

import java.text.MessageFormat

/**
 * Class for ActionItemCompositeService.
 */
class ActionItemCompositeService {
    static transactional = true
    def actionItemService
    def springSecurityService
    def actionItemReadOnlyCompositeService
    def actionItemStatusRuleService
    def actionItemContentService
    def actionItemReadOnlyService

    /**
     * Adds Action Item
     * @param map
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def addActionItem( map ) {
        def user = springSecurityService.getAuthentication()?.user
        ActionItem savedActionItem
        def success = false
        def message
        def aipUser = AipControllerUtils.getPersonForAip( [studentId: map.studentId], user.pidm )
        if (aipUser) {
            ActionItem ai = new ActionItem(
                    folderId: map.folderId,
                    status: map.status ? AIPConstants.STATUS_MAP.get( map.status ) : null,
                    postedIndicator: AIPConstants.NO_IND,
                    title: map.title,
                    name: map.name,
                    creatorId: user.username,
                    description: map.description,
                    )
            try {
                savedActionItem = actionItemService.create( ai )
                success = true
            } catch (ApplicationException e) {
                if (ActionItemService.FOLDER_VALIDATION_ERROR.equals( e.getMessage() )) {
                    message = MessageHelper.message( e.getDefaultMessage(), MessageFormat.format( "{0,number,#}", ai.folderId ) )
                } else {
                    message = MessageHelper.message( e.getDefaultMessage() )
                }
            }
        }
        [
                success      : success,
                message      : message,
                newActionItem: savedActionItem
        ]
    }

    /**
     * Edit Action item
     * @param map
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def editActionItem( map ) {
        ActionItem ai
        def success = false
        def message
        try {
            ai = actionItemService.get( map.actionItemId )
            def oldFolderId = ai.folderId
            if (map.name != ai.name) {
                message = MessageHelper.message( 'action.item.name.cannot.be.modified' )
            } else {
                ai.folderId = map.folderId
                ai.status = map.status ? AIPConstants.STATUS_MAP.get( map.status ) : null
                ai.title = map.title
                ai.description = map.description
                actionItemService.validateUpdate( ai, oldFolderId )
                actionItemService.update( ai )
                success = true
            }
        } catch (ApplicationException e) {
            if (ActionItemService.FOLDER_VALIDATION_ERROR.equals( e.getMessage() )) {
                message = MessageHelper.message( e.getDefaultMessage(), MessageFormat.format( "{0,number,#}", ai.folderId ) )
            } else {
                message = MessageHelper.message( e.getDefaultMessage() )
            }
        }
        [
                success          : success,
                message          : message,
                updatedActionItem: ai
        ]
    }
    /**
     * Deletes Action items
     *
     * @param actionItemId
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def deleteActionItem( actionItemId ) {
        def success = false
        def message
        def postingInd = actionItemService.getActionItemById( actionItemId ).postedIndicator
        def checkDeletable = actionItemReadOnlyCompositeService.checkIfActionItemDeletable( actionItemId, postingInd )
        if (!checkDeletable.deletable) {
            return [success: success,
                    message: checkDeletable.message]
        }
        ActionItemContent actionItemContent = actionItemContentService.listActionItemContentById( actionItemId )
        if (actionItemContent) {
            actionItemContentService.delete( actionItemContent )
        }
        List<ActionItemStatusRule> list = actionItemStatusRuleService.getActionItemStatusRuleByActionItemId( actionItemId )
        list.each {
            actionItemStatusRuleService.delete( it )
        }
        try {
            ActionItem actionItem = actionItemService.get( actionItemId )
            actionItemService.delete( actionItem )
            success = true
            message = MessageHelper.message( 'action.item.delete.success' )
        } catch (ApplicationException e) {
            success = false
            message = e.message
        }
        [
                success: success,
                message: message
        ]
    }

    /**
     *
     * @return
     */
    def getActionItemsListForSelect() {
        List<ActionItem> results = actionItemService.list()
        List folderIds = results?.collect {it.folderId}.unique()
        List<CommunicationFolder> folderList = CommunicationFolder.findAll()?.findAll() {CommunicationFolder it ->
            it.id in folderIds
        }
        Map folderMap = folderList?.collectEntries {CommunicationFolder it ->
            [it.id, it]
        }
        def resultMap = results?.collect {actionItem ->
            CommunicationFolder folder = folderMap.get( actionItem.folderId )
            [
                    actionItemId    : actionItem.id,
                    actionItemName  : actionItem.name,
                    actionItemTitle : actionItem.title,
                    folderId        : actionItem.folderId,
                    folderName      : folder.name,
                    folderDesc      : folder.description,
                    actionItemStatus: actionItem.status ? MessageHelper.message( "aip.status.${actionItem.status.trim()}" ) : null

            ]
        }
        resultMap
    }

    /**
     *
     * @param map
     * @return
     */
    def updateActionItemDetailWithTemplate( map ) {
        def templateId = map.templateId.toInteger()
        def actionItemId = map.actionItemId.toInteger()
        def actionItemDetailText = map.actionItemContent
        def success = false
        def result = validateEditActionItemContent( actionItemId )
        if (!result.editable) {
            def model = [
                    success: false,
                    errors : result.message
            ]
            return model
        }
        ActionItemContent aic = actionItemContentService.listActionItemContentById( actionItemId )
        if (!aic) {
            aic = new ActionItemContent()
        }
        aic.actionItemId = actionItemId
        aic.actionItemTemplateId = templateId
        aic.text = actionItemDetailText

        ActionItemContent newAic = actionItemContentService.createOrUpdate( aic )
        def errors = []
        ActionItemReadOnly actionItemRO = actionItemReadOnlyService.getActionItemROById( newAic.actionItemId )
        success = true
        [
                success   : success,
                errors    : errors,
                actionItem: actionItemRO,
        ]
    }
    /**
     * Validate if action item is editable or not
     * @param actionItemId
     */
    def validateEditActionItemContent( actionItemId ) {
        def result = [editable: true, message: null]
        try {
            ActionItem actionItem = actionItemService.get( actionItemId )
            if (actionItem.postedIndicator == AIPConstants.YES_IND) {
                result.editable = false
                result.message = MessageHelper.message( 'action.item.content.cannot.be.edited' )
            }
        } catch (ApplicationException ae) {
            result.editable = false
            result.message = MessageHelper.message( 'action.item.not.present' )
        }
        result
    }

}

