/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.service.ServiceBase

/**
 * DAO service interface for ActionItem group send item objects.
 */
class ActionItemPostWorkService extends ServiceBase {

    /**
     *
     * @param domainModelOrMap
     * @return
     */
    def preCreate( domainModelOrMap ) {
        ActionItemPostWork groupSendItem = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as
                ActionItemPostWork
        if (!groupSendItem.creationDateTime) {
            groupSendItem.creationDateTime = new Date()
        }
    }

    /**
     *
     * @param groupSend
     * @return
     */
    def updateStateToStop( ActionItemPost groupSend ) {
        ActionItemPostWork.updateStateToStop( groupSend )
    }
}
