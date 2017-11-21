/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for UserActionItemReadOnly domain
 */
class UserActionItemReadOnlyService extends ServiceBase {

    /**
     * Lists user specific action items
     * @param pidm
     * @return
     */
    def listActionItemByPidmWithinDate( Long pidm ) {
        return UserActionItemReadOnly.fetchUserActionItemsROByPidmDate( pidm )
    }

    /**
     * Check if Lists of user specific action items present
     * @param pidm
     * @return
     */
    def checkIfActionItemPresent( Long pidm ) {
        return UserActionItemReadOnly.checkIfActionItemPresent( pidm )
    }

}
