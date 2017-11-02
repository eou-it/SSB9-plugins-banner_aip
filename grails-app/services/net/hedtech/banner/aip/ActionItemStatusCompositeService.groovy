/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.i18n.MessageHelper
import org.apache.log4j.Logger

class ActionItemStatusCompositeService {
    private static final def LOGGER = Logger.getLogger( this.class )
    def actionItemStatusService
    def actionItemStatusRuleService
    def springSecurityService

    /**
     * Lists Action Item status
     * @param params
     * @return
     */
    def listActionItemsPageSort( Map params ) {
        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending: params?.sortAscending, max: params?.max, offset: params?.offset] )
        def resultCount = actionItemStatusService.listActionItemStatusCount()
        results = results?.collect {ActionItemStatus it ->
            def deleteMessage = checkIfDeleteable( it )
            def person = PersonUtility.getPerson( it.lastModifiedBy )
            [
                    id                            : it.id,
                    version                       : it.version,
                    actionItemStatus              : it.actionItemStatus,
                    actionItemStatusActive        : it.actionItemStatusActive,
                    actionItemStatusActivityDate  : it.lastModified,
                    actionItemStatusBlockedProcess: it.actionItemStatusBlockedProcess,
                    actionItemStatusDataOrigin    : it.dataOrigin,
                    actionItemStatusDefault       : it.actionItemStatusDefault,
                    actionItemStatusSystemRequired: it.actionItemStatusSystemRequired,
                    actionItemStatusUserId        :
                            person ? PersonUtility.getPerson( person.pidm as int )?.fullName : it.lastModifiedBy,
                    deletable                     : deleteMessage.canBeDeleted,
                    deleteRestrictionReason       : deleteMessage.message]
        }
        [
                result: results,
                length: resultCount,
                header: [
                        [name: "id", title: "id", options: [visible: false, isSortable: true]],
                        [name: "actionItemStatus", title: MessageHelper.message( "aip.common.status" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemBlockedProcess", title: MessageHelper.message( "aip.common.block.process" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemSystemRequired", title: MessageHelper.message( "aip.common.system.required" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "lastModifiedBy", title: MessageHelper.message( "aip.common.last.updated.by" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "lastModified", title: MessageHelper.message( "aip.common.activity.date" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0]
                ]
        ]
    }

    /**
     * Remove Action Item Status
     * @return
     */
    def removeStatus( def id ) {
        def success = false, message
        ActionItemStatus status
        try {
            status = actionItemStatusService.get( id )
        }
        catch (ApplicationException e) {
            LOGGER.error( "Action Item Status is not present in System for id $id" )
            throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'action.item.status.not.in.system', [] ) )
        }
        def deleteMessage = checkIfDeleteable( status )
        if (deleteMessage.canBeDeleted == AIPConstants.YES_IND) {
            actionItemStatusService.delete( status )
            success = true
            message = MessageHelper.message( 'action.status.delete.success', [status.actionItemStatus].toArray() )
        } else {
            message = deleteMessage.message
        }
        [
                success: success,
                message: message
        ]
    }

    /**
     * Remove Action Item Status
     * @return
     */
    private def checkIfDeleteable( ActionItemStatus status ) {
        def canBeDeleted = AIPConstants.YES_IND, message
        if (status.actionItemStatusSystemRequired == AIPConstants.YES_IND) {
            canBeDeleted = AIPConstants.NO_IND
            message = MessageHelper.message( 'action.item.status.cannot.be.deleted' )
        }
        if (status.actionItemStatusSystemRequired == AIPConstants.NO_IND) {
            def statusRulePresent = actionItemStatusRuleService.checkIfPresent( status.id )
            def statusRulePresentAndAssociatedToContent = actionItemStatusRuleService.checkIfPresentAndAssociatedToActionItemContent( status.id )
            if (statusRulePresent && !statusRulePresentAndAssociatedToContent) {
                canBeDeleted = AIPConstants.NO_IND
                message = MessageHelper.message( 'action.item.status.associated.to.status.rule' )
            }
            if (statusRulePresent && statusRulePresentAndAssociatedToContent) {
                canBeDeleted = AIPConstants.NO_IND
                message = MessageHelper.message( 'action.item.status.associated.to.status.rule.and.content' )
            }
        }
        [
                canBeDeleted: canBeDeleted,
                message     : message
        ]
    }

    /**
     * Saved Action Item Status
     * @return
     */
    def statusSave( def title ) {
        def user = springSecurityService.getAuthentication()?.user
        if (!user) {
            throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'user.id.not.valid', [] ) )
        }
        ActionItemStatus status = new ActionItemStatus(
                actionItemStatus: title,
                actionItemStatusActive: 'Y',
                actionItemStatusBlockedProcess: 'N',
                actionItemStatusSystemRequired: "N"
        )
        ActionItemStatus newStatus
        def success = false

        try {
            newStatus = actionItemStatusService.create( status )
            success = true
        } catch (ApplicationException e) {
            throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'actionItemStatus.status.unique', [] ) )
        }
        [
                success: success,
                status : newStatus
        ]
    }
}
