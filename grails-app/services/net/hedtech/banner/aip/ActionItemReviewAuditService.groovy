/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Review Audit
 */
class ActionItemReviewAuditService extends ServiceBase {

    /**
     *
     * @param userActionItemId
     * @return
     */
    def fetchReviewAuditByUserActionItemId( Long userActionItemId ) {
        ActionItemReviewAudit.fetchReviewAuditByUserActionItemId(userActionItemId)
    }

}
