/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase
import org.springframework.transaction.annotation.Transactional



class ActionItemDetailService extends ServiceBase {

    boolean transactional = true

    @Transactional(readOnly = true)
    def listActionItemDetailById(Long actionItemId) {
        return ActionItemDetail.fetchActionItemDetailById( actionItemId )
    }



    def updateTemplateContent (  Long actionItemId, def templateText ) {

        def ActionItemDetail actionItem = ActionItemDetail.fetchActionItemDetailById(actionItemId)

        if (!actionItem) {
            throw new ApplicationException( actionItem, "@@r1:invalidTemplate:@@" )
        }

        actionItem.text = templateText
        actionItem.save()
    }

}