/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service classes for Action Item Group Assign
 */
class ActionItemGroupAssignService extends ServiceBase {

    /**
     *
     * @param id
     * @return
     */
    def fetchByGroupId( Long id ) {
        ActionItemGroupAssign.fetchByGroupId( id )
    }

    /**
     *
     * @param actionItemId
     * @return
     */
    def checkIfAssignedGroupPresentForSpecifiedActionItem( Long actionItemId ) {
        ActionItemGroupAssign.checkIfAssignedGroupPresentForSpecifiedActionItem( actionItemId )
    }

    /**
     *
     * @param actionItemId
     * @param groupId
     * @return
     */
    def fetchByActionItemIdAndGroupId( Long actionItemId, Long groupId ) {
        ActionItemGroupAssign.fetchByActionItemIdAndGroupId( actionItemId, groupId )
    }
}
