/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service for Action Item Status Rules Readonly
 */
class ActionItemStatusRuleReadOnlyService extends ServiceBase {

    def listActionItemStatusRulesRO() {
        ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
    }


    def getActionItemStatusRuleROById(Long actionItemStatusRuleId) {
        ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById(actionItemStatusRuleId)
    }


    def getActionItemStatusRulesROByActionItemId(Long actionItemId) {
        def actionItemStatusRulesReadOnly = ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesROByActionItemId(actionItemId)
        actionItemStatusRulesReadOnly.collect { ActionItemStatusRuleReadOnly it ->
            [
                    version               : it.statusRuleVersion,
                    statusRuleId          : it.statusRuleId,
                    statusActiveInd       : it.statusActiveInd,
                    statusActivityDate    : it.statusActivityDate,
                    statusBlockProcessInd : it.statusBlockProcessInd,
                    statusId              : it.statusId,
                    statusName            : it.statusName,
                    statusRuleActionItemId: it.statusRuleActionItemId,
                    statusRuleActivityDate: it.statusRuleActivityDate,
                    statusRuleLabelText   : it.statusRuleLabelText,
                    statusRuleSeqOrder    : it.statusRuleSeqOrder,
                    statusSystemRequired  : it.statusSystemRequired,
                    statusReviewReqInd    : it.statusReviewRequired
            ]
        }
    }
}
