/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service for Action Item Status Rules Readonly
 */
class ActionItemStatusRuleReadOnlyService extends ServiceBase{

    def listActionItemStatusRulesRO() {
        ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
    }

    def getActionItemStatusRuleROById( Long actionItemStatusRuleId) {
        ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById(actionItemStatusRuleId)
    }

    def getActionItemStatusRulesROByActionItemId(Long actionItemId) {
        ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesROByActionItemId(actionItemId)
    }
}
