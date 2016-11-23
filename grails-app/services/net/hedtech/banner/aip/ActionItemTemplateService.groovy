package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import org.omg.CORBA.portable.ApplicationException

import javax.swing.Action

class ActionItemTemplateService extends ServiceBase{

    static final String NO_TITLE_ERROR= '@@r1:TitleCanNotBeNullError@@'
    static final String NO_SOURCEIND_ERROR= '@@r1:SourceIndCanNotBeNullError@@'
    static final String NO_SYSTEMREQ_ERROR= '@@r1:SystemReqCanNotBeNullError@@'
    static final String NO_ACTIVEIND_ERROR= '@@r1:ActiveIndCanNotBeNullError@@'

    def listActionItemTemplates() {
        return ActionItemTemplate.fetchActionItemTemplates()
    }

    def getActionItemTemplateById(Long actionItemTemplateId) {
        return ActionItemTemplate.fetchActionItemTemplateById(actionItemTemplateId)
    }

    def preCreate( domainModelOrMap) {
        ActionItemTemplate at = (domainModelOrMap instanceof Map ? domainModelOrMap.domaionModel : domainModelMap) as ActionItemTemplate

        if(!at.validate()) {
            def errorCodes = at.errors.allErrors.codes[0]
            if(errorCodes.contains('actionItemTemplate.title.nullable')) {
                throw new ApplicationException(ActionItemTemplate, NO_TITLE_ERROR, 'actionItemTemplate.title.nullable.error')
            } else if (errorCodes.contains('actionItemTemplate.SourceInd.nullable')) {
                throw new ApplicationException(ActionItemTemplate, NO_SOURCEIND_ERROR, 'actionItemTemplate.sourceInd.nullable.error')
            } else if (errorCodes.contains('actionItemTemplate.SystemReq.nullable')) {
                throw new ApplicationException(ActionItemTemplate, NO_SYSTEMREQ_ERROR, 'actionItemTemplate.systemReq.nullable.error')
            } else if (errorCodes.contains('actionItemTemplate.activeInd.nullable')) {
                throw new ApplicationException(ActionItemTemplate, NO_ACTIVEIND_ERROR, 'actionItemTemplate.activeInd.nullable.error')
            }
        }
    }
}
