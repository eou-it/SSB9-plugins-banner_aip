/*********************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.service.ServiceBase
import org.springframework.security.core.context.SecurityContextHolder

/**
 * Manages action item group post instances.
 */
class ActionItemPostService extends ServiceBase {

    def preCreate( domainModelOrMap ) {
        ActionItemPost groupSend = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemPost
        if (groupSend.getPostingCreatorId(  ) == null) {
            groupSend.setPostingCreatorId( SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName() )
        }
        if (groupSend.getPostingCreationDateTime() == null) {
            groupSend.setPostingCreationDateTime( new Date() )
        }
        if (groupSend.getLastModified() == null) {
            groupSend.setLastModified( new Date() )
        }
        if (groupSend.getLastModifiedBy() == null) {
            groupSend.setLastModifiedBy( SecurityContextHolder?.context?.authentication?.principal?.getOracleUserName() )
        }
        if (groupSend.getPostingScheduleDateTime(  ) == null) {
            groupSend.setPostingScheduleDateTime( new Date() )
        }
        groupSend.setPostingDeleteIndicator( false );
    }

    public List findRunning() {
        return ActionItemPost.findRunning()
    }

}
