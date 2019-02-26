/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for UserActionItem
 */
class UserActionItemService extends ServiceBase {
    static final String BAD_DATA_ERROR = '@@r1:BadDataError@@' // generic unable to insert
    static final String ALREADY_EXISTS_CONDITION = '@@r1:AlreadyExistsCondition@@'
    // violated insert rule (expected condition)

    /**
     *
     * @param userActionItemsId
     * @return
     */
    def getActionItemById(Long userActionItemsId) {
        UserActionItem.fetchUserActionItemById(userActionItemsId)
    }
    /**
     *
     * @param userActionItemsId
     * @return
     */
    def countUserActionItemByActionItemId(Long actionItemId) {
        UserActionItem.countUserActionItemByActionItemId(actionItemId)
    }
    /**
     *
     * @param pidm
     * @return
     */
    def countUserActionItemByPidm(Long actionItemPidm) {
        UserActionItem.countUserActionItemByPidm(actionItemPidm)
    }
    /**
     *
     * @param pidm
     * @return
     */
    def countUserActionItemByActionItemIdAndPidm(Long actionItemId, Long actionItemPidm) {
        UserActionItem.countUserActionItemByActionItemIdAndPidm(actionItemId, actionItemPidm)
    }

    /**
     *
     * @param actionItemPidm
     * @return
     */
    def listActionItemsByPidm(Long actionItemPidm) {
        UserActionItem.fetchUserActionItemsByPidm(actionItemPidm)
    }

    /**
     *
     * @param userActionItemId
     * @return
     */
    def getUserActionItemById(Long userActionItemId) {
        UserActionItem.findById(userActionItemId)
    }
    /**
     *
     * @param userActionItemId
     * @return
     */
    def getUserActionItemPidmById(Long userActionItemId) {
        UserActionItem.fetchUserActionItemPidmById(userActionItemId)
    }


    def preCreate(domainModelOrMap) {
        UserActionItem uat = (domainModelOrMap instanceof Map ? domainModelOrMap.domainModel : domainModelOrMap) as UserActionItem
        // guard against other violations? start date after end date? end date prior to t
        if (UserActionItem.isExistingInDateRangeForPidmAndActionItemId(uat))
            throw new ApplicationException(UserActionItem, ALREADY_EXISTS_CONDITION)
        if (!uat.validate()) {
            throw new ApplicationException(UserActionItem, BAD_DATA_ERROR)
        }
    }
}
