/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


@Integration
@Rollback
class UserActionItemServiceIntegrationTests extends BaseIntegrationTestCase {

    def userActionItemService
    ActionItem drugAndAlcoholPolicyActionItem

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        drugAndAlcoholPolicyActionItem = ActionItem.findByName("Drug and Alcohol Policy");
        assertNotNull drugAndAlcoholPolicyActionItem
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchUserActionItemByPidm() {
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> userActionItems = userActionItemService.listActionItemsByPidm(actionItemPidm)
        assertEquals(10, userActionItems.size())
        //println userActionItems
    }

    @Test
    void testCountUserActionItemByActionItemIdAndPidm() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def pidm = PersonUtility.getPerson("CSRSTU001").pidm

        def result = userActionItemService.countUserActionItemByActionItemIdAndPidm(actionItemId, pidm)
        assertEquals 1, result
    }

    @Test
    void testFetchActionItemByNonExisitingSpridenIdCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        Long pidm = 99999999L
        def result = userActionItemService.countUserActionItemByActionItemIdAndPidm(actionItemId, pidm)
        assertEquals 0, result
    }

    @Test
    void testFetchUserActionItemById() {
        //get the user's action items by a pidm
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> userActionItems = userActionItemService.listActionItemsByPidm(actionItemPidm)
        // select the first id of an action from that id list
        def userActionItemsId = userActionItems[0].id
        //get action item by id for that user
        UserActionItem userActionItemId = userActionItemService.getActionItemById(userActionItemsId)
        assertEquals(userActionItemsId, userActionItemId.id)
    }

    @Test
    void testFetchByActionItemIdOnlyExistingCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = userActionItemService.countUserActionItemByActionItemId(actionItemId)
        assertNotNull result
        assertEquals 13, result
    }

    @Test
    void testFetchByActionItemIdOnlyNonExistingCount() {
        Long actionItemId = 3999L
        def result = userActionItemService.countUserActionItemByActionItemId(actionItemId)
        assertNotNull result
        assertEquals 0, result
    }

    @Test
    void testFetchByPersonIdNonExistingCount() {
        Long pidm = 9999999L
        def result = userActionItemService.countUserActionItemByPidm(pidm)
        assertEquals 0, result

    }

    @Test
    void testPreCreateInvalidData() {
        Map userActionItem = [actionItemId: -1, pidm: 1, status: 'P', displayEndDate: new Date(), displayStartDate: new Date(), groupId: 1, creatorId: 'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA']
        try {
            userActionItemService.preCreate([domainModel: userActionItem])
        } catch (ApplicationException ae) {
            assertApplicationException(ae, '@@r1:BadDataError@@')
        }
    }


    @Test
    void testRejectDuplicateDateOverlap() {
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> existingUserActionItems = userActionItemService.listActionItemsByPidm(actionItemPidm)
        def existingUserActionItem = existingUserActionItems[0]
        def origSize = existingUserActionItems.size()
        UserActionItem userActionItem = new UserActionItem()
        userActionItem.pidm = actionItemPidm
        userActionItem.actionItemId = existingUserActionItem.actionItemId
        userActionItem.status = 1
        userActionItem.displayStartDate = existingUserActionItem.displayStartDate + 1
        userActionItem.displayEndDate = existingUserActionItem.displayEndDate
        userActionItem.groupId = existingUserActionItem.groupId
        userActionItem.creatorId = existingUserActionItem.creatorId
        userActionItem.createDate = new Date()
        userActionItem.dataOrigin = existingUserActionItem.dataOrigin
        def message = shouldFail(ApplicationException) {
            userActionItemService.create(userActionItem)
        }
        assertEquals("@@r1:AlreadyExistsCondition@@", message)

        List<UserActionItem> newUserActionItems = userActionItemService.listActionItemsByPidm(actionItemPidm)
        assertEquals(origSize, newUserActionItems.size())
    }


    @Test
    void testAcceptDuplicateNoDateOverlap() {
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> existingUserActionItems = userActionItemService.listActionItemsByPidm(actionItemPidm)
        // make start and end well past this range
        def startDate = new Date().copyWith(year: 2024, month: Calendar.APRIL, dayOfMonth: 3)
        def endDate = new Date().copyWith(year: 2024, month: Calendar.JULY, dayOfMonth: 3)
        def existingUserActionItem = existingUserActionItems[0]
        def origSize = existingUserActionItems.size()
        UserActionItem userActionItem = new UserActionItem()
        userActionItem.pidm = actionItemPidm
        userActionItem.actionItemId = existingUserActionItem.actionItemId
        userActionItem.status = ActionItemStatus.fetchDefaultActionItemStatus().id
        userActionItem.displayStartDate = startDate
        userActionItem.displayEndDate = endDate
        userActionItem.groupId = existingUserActionItem.groupId
        userActionItem.creatorId = existingUserActionItem.creatorId
        userActionItem.createDate = new Date()
        userActionItem.dataOrigin = existingUserActionItem.dataOrigin
        userActionItemService.create(userActionItem)
        List<UserActionItem> newUserActionItems = userActionItemService.listActionItemsByPidm(actionItemPidm)

        assertEquals(origSize + 1, newUserActionItems.size())
    }
}
