/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import org.springframework.transaction.annotation.Transactional


/**
 * Service class for Action Item Content
 */
class ActionItemContentService extends ServiceBase {

    boolean transactional = true

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
