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

    def actionItemDetailService

    def actionItemReadOnlyService

    def actionItemStatusRuleService

    def actionItemService

    def springSecurityService


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def addActionItem( map ) {
        def user = springSecurityService.getAuthentication()?.user
        println user
        ActionItem savedActionItem
        def success = false
        def message
        def aipUser = AipControllerUtils.getPersonForAip( [studentId: map.studentId], user.pidm )
        ActionItem ai = new ActionItem(
                folderId: map.folderId ?: null,
                status: map.status ? AIPConstants.STATUS_MAP.get( map.status ): null,
                postedIndicator:'N',
                title: map.title ?: null,
                name: map.name ?: null,
                creatorId: user.username ?: null,
                userId: aipUser.bannerId ?: null,
                description: map.description ?: null,
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


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def updateDetailsAndStatusRules( aipUser, inputRules, templateId, actionItemId, actionItemDetailText ) {
        def answer = [:]
        ActionItemDetail aid = actionItemDetailService.listActionItemDetailById( actionItemId )
        aid.actionItemId = actionItemId
        aid.actionItemTemplateId = templateId
        aid.lastModifiedby = aipUser.bannerId
        aid.lastModified = new Date()
        aid.text = actionItemDetailText

        List<Long> tempRuleIdList = inputRules.statusRuleId.toList()
        List<ActionItemStatusRule> actionItemStatusRules = actionItemStatusRuleService.getActionItemStatusRuleByActionItemId( actionItemId )
        def existingRuleId = actionItemStatusRules.id
        def deleteRules = existingRuleId - tempRuleIdList

        //update&create
        List<ActionItemStatusRule> ruleList = []
        inputRules.each {rule ->
            def statusRule
            if (rule.statusRuleId) {
                statusRule = ActionItemStatusRule.get( rule.statusRuleId )
                statusRule.seqOrder = rule.statusRuleSeqOrder
                statusRule.labelText = !rule.statusRuleLabelText ? null : rule.statusRuleLabelText
                statusRule.actionItemStatusId = rule.statusId
                statusRule.actionItemId = actionItemId
                statusRule.userId = aipUser.bannerId
                statusRule.activityDate = new Date()
            } else {
                statusRule = new ActionItemStatusRule(
                        seqOrder: rule.statusRuleSeqOrder,
                        labelText: !rule.statusRuleLabelText ? null : rule.statusRuleLabelText,
                        actionItemId: actionItemId,
                        actionItemStatusId: rule.statusId,
                        userId: aipUser.bannerId,
                        activityDate: new Date()
                )
            }
            ruleList.push( statusRule )
        }
        //delete

        ActionItemDetail newAid
        ActionItemReadOnly actionItemRO
        List<ActionItemStatusRule> updatedActionItemStatusRules
        def weGood = true
        try {
            ruleList.each {rule -> actionItemStatusRuleService.preUpdate( rule )}
        } catch (ApplicationException ae) {
            weGood = false
            throw new ApplicationException( "rollback", ae.message, ae.defaultMessage )
        }
        if (weGood) {
            try {
                newAid = actionItemDetailService.createOrUpdate( aid, false )
                //todo: add new method to service for action item detail to retreive an action item by detail id and action item id
                actionItemRO = actionItemReadOnlyService.getActionItemROById( newAid.actionItemId )
                actionItemStatusRuleService.delete( deleteRules ) //list of ids to be deleted
                actionItemStatusRuleService.createOrUpdate( ruleList, false )
                //list of domain objects to be updated or created
                updatedActionItemStatusRules = actionItemStatusRuleService.getActionItemStatusRuleByActionItemId( actionItemId )

            } catch (ApplicationException e) {
                throw new ApplicationException( "rollback", e.message, e.defaultMessage )
            }
        }

        answer = ['actionItemRO': actionItemRO, 'statusRules': updatedActionItemStatusRules]
        return answer
    }
}

