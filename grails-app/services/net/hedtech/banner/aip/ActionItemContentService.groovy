/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import org.springframework.transaction.annotation.Transactional



class ActionItemContentService extends ServiceBase {

    boolean transactional = true

    @Transactional(readOnly = true)
    def listActionItemContentById( Long actionItemId) {
        return ActionItemContent.fetchActionItemContentById( actionItemId )
    }
}
