/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.service.ServiceBase
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Service class for Action Item Posting Details
 */
class ActionItemPostDetailService extends ServiceBase {
    def preCreate( domainModelOrMap ) {
            ActionItemPostDetail groupSend = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemPostDetail
            if (groupSend.getLastModifiedBy(  ) == null) {
                groupSend.setLastModifiedBy( SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName() )
            }
            if (groupSend.getLastModified() == null) {
                groupSend.setLastModified( new Date() )
            }
        }
}
