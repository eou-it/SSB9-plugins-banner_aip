/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.service.ServiceBase

class ActionItemStatusRuleService extends ServiceBase{

    static final String NO_ACTIONITEMID_ERROR = "@@r1:ActionItemIdCanNotBeNullError@@"
    static final String NO_SEQ_ORDER_ERROR = "@@r1:SeqOrderCanNotBeNullError@@"
    static final String NO_LABELTEXT_ERROR = "@@r1:LabelTextCanNotBeNullError@@"
    static final String MAX_SIZE_ERROR = "@@r1:MaxSizeError@@"
    static final String OTHER_VALIDATION_ERROR = "@@r1:ValidationError@@"

    def listActionItemStatusRules() {
        return ActionItemStatusRule.fetchActionItemStatusRules()
    }

    def getActionItemStatusRuleById( Long actionItemStatusRuleId ) {
        return ActionItemStatusRule.fetchActionItemStatusRuleById(actionItemStatusRuleId)
    }

    def getActionItemStatusRuleByActionItemId (Long actionItemId) {
        return ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId(actionItemId)
    }

    def preCreate (domainModelOrMap) {
        ActionItemStatusRule aisr = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemStatusRule

        if(!aisr.validate()) {
            def errorCodes = ai.errors.allErrors.codes[0]

            if(errorCodes.contains("actionItemStatusRule.actionItemId.nullable")) {
                throw new ApplicationException(ActionItemStatusRule, NO_ACTIONITEMID_ERROR, "actionItemStatusRule.actionItemId.nullable.error")
            } else if(errorCodes.contains("actionItemStatusRule.seqOrder.nullable")) {
                throw new ApplicationException(ActionItemStatusRule, NO_SEQ_ORDER_ERROR, "actionItemStatusRule.seqOrder.nullable.error")
            } else if(errorCodes.contains("actionItemStatusRule.labelText.nullable")) {
                throw new ApplicationException(ActionItemStatusRule, NO_LABELTEXT_ERROR, "actionItemStatusRule.labelText.nullable.error")
            } else if(errorCodes.contains("maxSize.exceeded")) {
                throw new ApplicationException(ActionItemStatusRule, MAX_SIZE_ERROR, "actionItemStatusRule.max.size.error")
            } else {
                throw new ApplicationException( ActionItemStatusRule, OTHER_VALIDATION_ERROR, 'actionItemStatusRule.operation.not.permitted' )
            }
        }
    }
}
