/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import grails.gorm.transactions.Transactional


/**
 * Service class for Action Item Content
 */
@Transactional
class ActionItemContentService extends ServiceBase {



    /**
     *
     * @param actionItemId
     * @return
     */
    @Transactional(readOnly = true)
    def listActionItemContentById( Long actionItemId) {
        ActionItemContent.fetchActionItemContentById( actionItemId )
    }
}
