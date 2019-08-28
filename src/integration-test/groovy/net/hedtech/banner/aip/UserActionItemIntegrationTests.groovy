/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import net.zorched.test.User
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

@Integration
@Rollback
class UserActionItemIntegrationTests extends BaseIntegrationTestCase {
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
    void testFetchActionItemByActionItemAndPidmCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id

        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student

        def result = UserActionItem.countUserActionItemByActionItemIdAndPidm(actionItemId, student.pidm)
        assertEquals 1, result

    }

    @Test
    void testCountUserActionItemByActionItemId() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = UserActionItem.countUserActionItemByActionItemId(actionItemId)
        assertEquals 13, result

    }

    @Test
    void testFetchByPidmExistingCount() {
        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student
        def result = UserActionItem.countUserActionItemByPidm(student.pidm)
        assertEquals 5, result
    }

    @Test
    void testFetchByActionItemIdOnlyExistingCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = UserActionItem.countUserActionItemByActionItemId(actionItemId)
        assertNotNull result
        assertEquals 13, result
    }

    @Test
    void testFetchActionItemByNonExisitingPidmCount() {
        Long pidm = 99999999L
        def result = UserActionItem.fetchUserActionItemsByPidm(pidm)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchUserActionItemByPidmService() {
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm(actionItemPidm)
        assertEquals(10, userActionItems.size())
    }

    @Test
    void testFetchUserActionItemByIdService() {
        //get the user's action items by a pidm
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm(actionItemPidm)
        // select the first id of an action from that id list
        def actionItemId = userActionItems[0].id
        //get action item by id for that user
        UserActionItem userActionItemId = UserActionItem.fetchUserActionItemById(actionItemId)
        assertEquals(actionItemId, userActionItemId.id)
    }


    @Test
    void testUserActionItemsToString() {
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm(actionItemPidm)
        assertNotNull(userActionItems.toString())
        assertFalse userActionItems.isEmpty()
    }

    // check that new UserActionItem is the first to be added for pidm and ActionItem.
    // No date range to check (most common condition)
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdNoneFound() {
        ActionItem actionItem = ActionItem.findByName('Registration Process Training')
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson("CSRSTU002").pidm

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2017")
        itemWeWantToTest.displayEndDate = new SimpleDateFormat("dd-MM-yyyy").parse("02-01-2017")
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus().id
        assertFalse(UserActionItem.isExistingInDateRangeForPidmAndActionItemId(itemWeWantToTest))
    }

    // check that new ActionItem overlapping start date returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdFront() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson("CSRSTU002").pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date() + 1
        itemToTestAgainst.displayEndDate = new Date() + 10
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus().id

        itemToTestAgainst.save(failOnError: true, flush: true)

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date()
        itemWeWantToTest.displayEndDate = new Date() + 7
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus().id
        assertTrue(UserActionItem.isExistingInDateRangeForPidmAndActionItemId(itemWeWantToTest))
    }

    // check that new ActionItem overlapping end date returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdBack() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson("CSRSTU002").pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date()
        itemToTestAgainst.displayEndDate = new Date() + 7
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus().id

        itemToTestAgainst.save(failOnError: true, flush: true)

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date() + 5
        itemWeWantToTest.displayEndDate = new Date() + 10
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus().id
        assertTrue(UserActionItem.isExistingInDateRangeForPidmAndActionItemId(itemWeWantToTest))
    }

    // check that new ActionItem contained within existing date range returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdContained() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson("CSRSTU002").pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date()
        itemToTestAgainst.displayEndDate = new Date() + 15
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus().id

        itemToTestAgainst.save(failOnError: true, flush: true)

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date() + 5
        itemWeWantToTest.displayEndDate = new Date() + 10
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus().id
        assertTrue(UserActionItem.isExistingInDateRangeForPidmAndActionItemId(itemWeWantToTest))
    }
    // check that new ActionItem containing existing date range returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdContaining() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson("CSRSTU002").pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date() + 5
        itemToTestAgainst.displayEndDate = new Date() + 10
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus().id

        itemToTestAgainst.save(failOnError: true, flush: true)

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date()
        itemWeWantToTest.displayEndDate = new Date() + 15
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        assertTrue(UserActionItem.isExistingInDateRangeForPidmAndActionItemId(itemWeWantToTest))
    }

    // check that new ActionItem prior to existing date range returns false
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdPrior() {
        ActionItem actionItem = ActionItem.findByName('Registration Process Training')
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson("CSRSTU002").pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date() + 10
        itemToTestAgainst.displayEndDate = new Date() + 15
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus().id

        itemToTestAgainst.save(failOnError: true, flush: true)

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2017")
        itemWeWantToTest.displayEndDate = new SimpleDateFormat("dd-MM-yyyy").parse("06-01-2017")
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        assertFalse(UserActionItem.isExistingInDateRangeForPidmAndActionItemId(itemWeWantToTest))
    }
    // check that new ActionItem after existing date range returns false
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdAfter() {
        ActionItem actionItem = ActionItem.findByName('Registration Process Training')
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson("CSRSTU002").pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date()
        itemToTestAgainst.displayEndDate = new Date() + 5
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus().id

        itemToTestAgainst.save(failOnError: true, flush: true)

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2017")
        itemWeWantToTest.displayEndDate = new SimpleDateFormat("dd-MM-yyyy").parse("15-01-2017")
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        assertFalse(UserActionItem.isExistingInDateRangeForPidmAndActionItemId(itemWeWantToTest))
    }


    @Test
    void testUserActionItemHashCode() {
        def actionItemPidm = PersonUtility.getPerson("CSRSTU018").pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm(actionItemPidm)

        def userActionItemId = userActionItems[0].id
        UserActionItem userActionItemList = UserActionItem.fetchUserActionItemById(userActionItemId)

        def result = userActionItemList.hashCode()
        assertNotNull result

        def userActionITemListObj = new UserActionItem()
        result = userActionITemListObj.hashCode()
        assertNotNull result

    }


    private UserActionItem newActionItem() {
        def userActionItemNew = new UserActionItem()
        userActionItemNew.actionItemId = 1
        userActionItemNew.pidm = 1
        userActionItemNew.status = ActionItemStatus.fetchDefaultActionItemStatus().id
        userActionItemNew.userResponseDate = null
        userActionItemNew.displayStartDate = null
        userActionItemNew.displayEndDate = null
        userActionItemNew.groupId = 1
        userActionItemNew.creatorId = "GRAILS"
        userActionItemNew.createDate = new Date()
        userActionItemNew.dataOrigin = "GRAILS"

        return userActionItemNew
    }

}
