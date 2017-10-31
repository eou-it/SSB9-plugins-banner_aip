/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase
import org.springframework.transaction.annotation.Propagation

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
