/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemStatusRuleReadOnlyService extends ServiceBase{

    def listActionItemStatusRulesRO() {
        return ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
    }

    def getActionItemStatusRuleROById( Long actionItemStatusRuleId) {
        return ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById(actionItemStatusRuleId)
    }

    def getActionItemStatusRulesROByActionItemId(Long actionItemId) {
        return ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesROByActionItemId(actionItemId)
    }
}
