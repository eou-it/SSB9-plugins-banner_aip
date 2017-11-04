/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

/**
 * Service classes for Action Item Group Assign
 */
class ActionItemGroupAssignService extends ServiceBase {

    def fetchByGroupId( Long id ) {
        ActionItemGroupAssign.fetchByGroupId( id )
    }


    def fetchByActionItemIdAndGroupId( Long actionItemId, Long groupId ) {
        ActionItemGroupAssign.fetchByActionItemIdAndGroupId( actionItemId, groupId )
    }

    def preCreate( domainModelOrMap ) {
        ActionItemGroupAssign aiga = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemGroupAssign

        if (!aiga.validate()) {
            throw new ApplicationException(ActionItemGroupAssign,"@@r1:DomainInvalidError@@", "actionItemGroupAssign.invalid.error" )
        }
    }

    def preUpdate (domainModelOrMap) {
        preCreate( domainModelOrMap)
    }
}
