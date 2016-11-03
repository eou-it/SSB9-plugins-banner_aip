/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

class ActionItemStatusRuleReadOnlyService extends ServiceBase{

    def listActionItemStatusRulesRO() {
        return ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO()
    }

    def getActionItemStatusRuleROById( Long actionItemStatusRuleId) {
        return ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById(actionItemStatusRuleId)
    }

    def getActionItemStatusRuleROByActionItemId(Long actionItemId) {
        return ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROByActionItemId(actionItemId)
    }
}
