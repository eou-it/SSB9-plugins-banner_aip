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
import org.springframework.security.core.context.SecurityContextHolder

class ActionItemStatusCompositeService {
    private static final def LOGGER = Logger.getLogger( this.class )
    def actionItemStatusService
    def actionItemStatusRuleService

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
        results?.each {
            def person = PersonUtility.getPerson( it.actionItemStatusUserId )
            if (person) {
                it.putAt( 'actionItemStatusUserId', PersonUtility.getPerson( person.pidm as int )?.fullName )
            }
        }
        [
                result: results,
                length: resultCount[0],
                header: [
                        [name: "id", title: "id", options: [visible: false, isSortable: true]],
                        [name: "actionItemStatus", title: MessageHelper.message( "aip.common.status" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemBlockedProcess", title: MessageHelper.message( "aip.common.block.process" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemSystemRequired", title: MessageHelper.message( "aip.common.system.required" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemStatusUserId", title: MessageHelper.message( "aip.common.last.updated.by" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemStatusActivityDate", title: MessageHelper.message( "aip.common.activity.date" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0]
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
        if (status.actionItemStatusSystemRequired == AIPConstants.YES_IND) {
            LOGGER.error( "Action Item Status $id cannot be deleted as actionItemStatusSystemRequired is yes" )
            throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'action.item.status.cannot.be.deleted', [] ) )
        }
        if (status.actionItemStatusSystemRequired == AIPConstants.NO_IND) {
            def statusRulePresent = actionItemStatusRuleService.checkIfPresent( id )
            def statusRulePresentAndAssociatedToContent = actionItemStatusRuleService.checkIfPresentAndAssociatedToActionItemContent( id )
            if (statusRulePresent && !statusRulePresentAndAssociatedToContent) {
                LOGGER.error( "The Status Rule is associated to Action Item Content and cannot be deleted" )
                throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'action.item.status.associated.to.status.rule', [] ) )
            }
            if (statusRulePresent && statusRulePresentAndAssociatedToContent) {
                LOGGER.error( "The Status Rule is associated to Action Item Content and an assigned Action Item, hence cannot be deleted." )
                throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'action.item.status.associated.to.status.rule.and.content', [] ) )
            }
            actionItemStatusService.delete( status )
            success = true
            message = MessageHelper.message( 'action.status.delete.success', [status.actionItemStatus].toArray() )
        }
        [
                success: success,
                message: message
        ]
    }

    /**
     * Saved Action Item Status
     * @return
     */
    def statusSave( def title ) {
        def user = SecurityContextHolder?.context?.authentication?.principal
        if (!user.pidm) {
            throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'user.id.not.valid', [] ) )
        }
        def aipUser = PersonUtility.getPerson( user.pidm )
        ActionItemStatus status = new ActionItemStatus(
                actionItemStatus: title,
                actionItemStatusActive: 'Y',
                actionItemStatusBlockedProcess: 'N',
                actionItemStatusActivityDate: new Date(),
                actionItemStatusUserId: aipUser.bannerId,
                actionItemStatusSystemRequired: "N",
                actionItemStatusVersion: null,
                actionItemStatusDataOrigin: null
        )
        ActionItemStatus newStatus
        def success = false
        def message

        try {
            newStatus = actionItemStatusService.create( status );
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
