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
     * @param pidm, actionItemId
     * @return
     */
    def fetchReviewAuditByPidmAndActionItemId( Long pidm, Long actionItemId ) {
        ActionItemReviewAudit.fetchReviewAuditByPidmAndActionItemId( pidm ,actionItemId)
    }

}
