/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
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
    def actionItemContentService

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
        ActionItem ai = new ActionItem(
                folderId: map.folderId,
                status: map.status ? AIPConstants.STATUS_MAP.get( map.status ) : null,
                postedIndicator: 'N',
                title: map.title,
                name: map.name,
                creatorId: user.username,
                userId: aipUser.bannerId,
                description: map.description,
                activityDate: new Date()
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
        [
                success      : success,
                message      : message,
                newActionItem: savedActionItem
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
        def postingInd=actionItemService.getActionItemById( actionItemId ).postedIndicator
        def checkDeletable = actionItemReadOnlyCompositeService.checkIfActionItemDeletable( actionItemId, postingInd )
        if (!checkDeletable.deletable) {
            return [success: success,
                    message: checkDeletable.message]
        }
        ActionItemContent actionItemContent = actionItemContentService.listActionItemContentById( actionItemId )
        if (actionItemContent) {
            actionItemContentService.delete( actionItemContent )
        }
        try {
            ActionItem actionItem = actionItemService.get( actionItemId )
            actionItemService.delete( actionItem )
            success = true
        } catch (ApplicationException e) {
            success = false
            message = e.message
        }
        [
                success: success,
                message: message
        ]
    }

}

