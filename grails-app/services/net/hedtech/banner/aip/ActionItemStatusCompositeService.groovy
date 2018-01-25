/*********************************************************************************
 Copyright 2017-2018 Ellucian Company L.P. and its affiliates.
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
    def actionItemCompositeService

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
    def statusSave( def paramMap ) {
        def user = springSecurityService.getAuthentication()?.user
        if (!user) {
            throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'user.id.not.valid', [] ) )
        }
        if (actionItemStatusService.checkIfNameAlreadyPresent( paramMap.title )) {
            throw new ApplicationException( ActionItemStatusCompositeService, new BusinessLogicValidationException( 'actionItemStatus.status.unique', [] ) )
        }
        ActionItemStatus status = new ActionItemStatus(
                actionItemStatus: paramMap.title,
                actionItemStatusActive: AIPConstants.YES_IND,
                actionItemStatusBlockedProcess: paramMap.block==true? AIPConstants.YES_IND:AIPConstants.NO_IND,
                actionItemStatusSystemRequired: AIPConstants.NO_IND
        )
        ActionItemStatus newStatus
        def success = false
        newStatus = actionItemStatusService.create( status )
        success = true
        [
                success: success,
                status : newStatus
        ]
    }

    /**
     * Updates action item status rules
     * @param jsonObj
     * @return
     */
    def updateActionItemStatusRule( jsonObj ) {
        def success = false
        def message
        def inputRules = jsonObj.rules
        def result = actionItemCompositeService.validateEditActionItemContent( jsonObj.actionItemId.toLong() )
        if (!result.editable) {
            def model = [
                    success: false,
                    errors : result.message
            ]
            return model
        }
        List<ActionItemStatusRule> currentRules = actionItemStatusRuleService.getActionItemStatusRuleByActionItemId( jsonObj.actionItemId.toLong() )
        List<Long> newRuleIdList = inputRules.statusRuleId.toList()
        List<Long> existingRuleIdList = currentRules.id.toList()
        def deleteRules = existingRuleIdList.minus( newRuleIdList )

        //delete those that have been removed
        actionItemStatusRuleService.delete( deleteRules )

        //create or update rules
        try {
            List<ActionItemStatusRule> ruleList = []
            inputRules.each {rule ->
                def statusRule
                def statusId
                if (rule.status.id) {
                    statusId = rule.status.id
                } else if (rule.status.actionItemStatusId) {
                    statusId = rule.status.actionItemStatusId
                } else {
                    message = MessageHelper.message( "actionItemStatusRule.statusId.nullable.error" )
                    throw new ApplicationException( message )
                }

                if (rule.statusRuleId) {
                    statusRule = ActionItemStatusRule.get( rule.statusRuleId )
                    statusRule.seqOrder = rule.statusRuleSeqOrder.toInteger()
                    statusRule.labelText = rule.statusRuleLabelText
                    statusRule.actionItemStatusId = statusId
                    statusRule.actionItemId = jsonObj.actionItemId.toLong()
                } else {
                    statusRule = new ActionItemStatusRule(
                            seqOrder: rule.statusRuleSeqOrder,
                            labelText: rule.statusRuleLabelText,
                            actionItemId: jsonObj.actionItemId.toLong(),
                            actionItemStatusId: statusId
                    )
                }
                ruleList.push( statusRule )
            }
            ruleList.each {rule ->
                actionItemStatusRuleService.createOrUpdate( rule ) //list of domain objects to be updated or created
            }
            success = true
        } catch (ApplicationException e) {
            LOGGER.error( e.getMessage() )
        }
        List<ActionItemStatusRule> updatedActionItemStatusRules =
                actionItemStatusRuleService.getActionItemStatusRuleByActionItemId( jsonObj.actionItemId.toLong() )
        [
                success: success,
                message: message,
                rules  : updatedActionItemStatusRules
        ]
    }
}
