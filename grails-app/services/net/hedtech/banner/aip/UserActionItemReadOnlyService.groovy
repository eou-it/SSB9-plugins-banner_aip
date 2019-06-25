/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for UserActionItemReadOnly domain
 */
@Transactional
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
