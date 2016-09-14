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



    def update (  Long detailId, /*def templateText,*/ def templateId, def userId ) {

        def ActionItemDetail actionItemDetail = ActionItemDetail.fetchActionItemDetailById(detailId)

        if (!actionItemDetail) {
            throw new ApplicationException( actionItemDetail, "@@r1:invalidTemplate:@@" )
        }

        //actionItem.text = templateText
        actionItemDetail.activityDate = new Date()
        actionItemDetail.actionItemTemplateId = templateId
        actionItemDetail.userId = userId
        actionItemDetail.save()
    }

}