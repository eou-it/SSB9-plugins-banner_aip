/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemGroupAssignService  extends ServiceBase  {

    def fetchByGroupId(Long id) {
        ActionItemGroupAssign.fetchByGroupId( id )
    }

    def fetchByActionItemIdAndGroupId (Long actionItemId, Long groupId) {
        ActionItemGroupAssign.fetchByActionItemIdAndGroupId(actionItemId, groupId)
    }


    def preCreate(domainModelOrMap) {

    }

    def preUpdate (domainModelOrMap) {
        preCreate(domainModelOrMap)
    }
}
