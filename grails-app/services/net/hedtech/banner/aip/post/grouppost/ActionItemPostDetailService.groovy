/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Posting Details
 */
@Transactional
class ActionItemPostDetailService extends ServiceBase {

    /**
     *
     * @param groupSendId
     * @return
     */
    def fetchByActionItemPostId( groupSendId ) {
        ActionItemPostDetail.fetchByActionItemPostId( groupSendId )
    }
}
