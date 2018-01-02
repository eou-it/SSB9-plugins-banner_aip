/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class UserActionItemIntegrationTests extends BaseIntegrationTestCase {

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchUserActionItemByPidmService() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm( actionItemPidm )
        assertEquals( 10, userActionItems.size() )
        //println userActionItems
    }


    @Test
    void testFetchUserActionItemByIdService() {
        //get the user's action items by a pidm
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm( actionItemPidm )
        // select the first id of an action from that id list
        def actionItemId = userActionItems[0].id
        //get action item by id for that user
        UserActionItem userActionItemId = UserActionItem.fetchUserActionItemById( actionItemId )
        assertEquals( actionItemId, userActionItemId.id )
    }


    @Test
    void testUserActionItemsToString() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm( actionItemPidm )
        assertNotNull( userActionItems.toString() )
        assertFalse userActionItems.isEmpty()
    }

    // check that new UserActionItem is the first to be added for pidm and ActionItem.
    // No date range to check (most common condition)
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdNoneFound() {
        ActionItem actionItem = ActionItem.findByName('Registration Process Training')
        ActionItemGroup actionItemGroup= ActionItemGroup.fetchActionItemGroups(  )[0]
        def pidmToTest = PersonUtility.getPerson( "CSRSTU002" ).pidm

        UserActionItem itemWeWantToTest = newActionItem(  )
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date()
        itemWeWantToTest.displayEndDate = new Date() + 1
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id
        println UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest )
        assertFalse( UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest ) )
    }

    // check that new ActionItem overlapping start date returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdFront() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson( "CSRSTU002" ).pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date() + 1
        itemToTestAgainst.displayEndDate = new Date() + 10
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id

        itemToTestAgainst.save( failOnError:true, flush: true )

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date()
        itemWeWantToTest.displayEndDate = new Date() + 7
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id
        println UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest )
        assertTrue( UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest ) )
    }

    // check that new ActionItem overlapping end date returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdBack() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson( "CSRSTU002" ).pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date()
        itemToTestAgainst.displayEndDate = new Date() + 7
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id

        itemToTestAgainst.save( failOnError:true, flush: true )

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date() + 5
        itemWeWantToTest.displayEndDate = new Date() + 10
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id
        println UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest )
        assertTrue( UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest ) )
    }

    // check that new ActionItem contained within existing date range returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdContained() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson( "CSRSTU002" ).pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date()
        itemToTestAgainst.displayEndDate = new Date() + 15
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id

        itemToTestAgainst.save( failOnError:true, flush: true )

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date() + 5
        itemWeWantToTest.displayEndDate = new Date() + 10
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        itemWeWantToTest.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id
        println UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest )
        assertTrue( UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest ) )
    }
    // check that new ActionItem containing existing date range returns true
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdContaining() {
        ActionItem actionItem = ActionItem.fetchActionItems()[0]
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson( "CSRSTU002" ).pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date() + 5
        itemToTestAgainst.displayEndDate = new Date() + 10
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id

        itemToTestAgainst.save( failOnError:true, flush: true )

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date()
        itemWeWantToTest.displayEndDate = new Date() + 15
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        println UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest )
        assertTrue( UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest ) )
    }

    // check that new ActionItem prior to existing date range returns false
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdPrior() {
        ActionItem actionItem = ActionItem.findByName('Registration Process Training')
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson( "CSRSTU002" ).pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date() + 10
        itemToTestAgainst.displayEndDate = new Date() + 15
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id

        itemToTestAgainst.save( failOnError:true, flush: true )

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date()
        itemWeWantToTest.displayEndDate = new Date() + 5
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        println UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest )
        assertFalse( UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest ) )
    }
    // check that new ActionItem after existing date range returns false
    @Test
    void testIsExistingInDateRangeByPidmAndACTMIdAfter() {
        ActionItem actionItem = ActionItem.findByName('Registration Process Training')
        ActionItemGroup actionItemGroup = ActionItemGroup.fetchActionItemGroups()[0]
        def pidmToTest = PersonUtility.getPerson( "CSRSTU002" ).pidm

        UserActionItem itemToTestAgainst = newActionItem()
        itemToTestAgainst.pidm = pidmToTest
        itemToTestAgainst.displayStartDate = new Date()
        itemToTestAgainst.displayEndDate = new Date() + 5
        itemToTestAgainst.actionItemId = actionItem.id
        itemToTestAgainst.groupId = actionItemGroup.id
        itemToTestAgainst.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id

        itemToTestAgainst.save( failOnError:true, flush: true )

        UserActionItem itemWeWantToTest = newActionItem()
        itemWeWantToTest.pidm = pidmToTest
        itemWeWantToTest.displayStartDate = new Date() + 10
        itemWeWantToTest.displayEndDate = new Date() + 15
        itemWeWantToTest.actionItemId = actionItem.id
        itemWeWantToTest.groupId = actionItemGroup.id
        println UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest )
        assertFalse( UserActionItem.isExistingInDateRangeForPidmAndActionItemId( itemWeWantToTest ) )
    }


    @Test
    void testUserActionItemHashCode() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm( actionItemPidm )

        def userActionItemId = userActionItems[0].id
        UserActionItem userActionItemList = UserActionItem.fetchUserActionItemById( userActionItemId )

        def result = userActionItemList.hashCode()
        assertNotNull result

        def userActionITemListObj = new UserActionItem()
        result = userActionITemListObj.hashCode()
        assertNotNull result

    }


    @Test
    void testUserActionItemEquals() {

        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemsByPidm( actionItemPidm )

        def userActionItemId = userActionItems[0].id
        UserActionItem userActionItem = UserActionItem.fetchUserActionItemById( userActionItemId )

        def userActionItemNewList = new UserActionItem()

        userActionItemNewList.id = userActionItem.id
        userActionItemNewList.actionItemId = userActionItem.actionItemId
        userActionItemNewList.pidm = userActionItem.pidm
        userActionItemNewList.status = userActionItem.status
        userActionItemNewList.userResponseDate = userActionItem.userResponseDate
        userActionItemNewList.displayStartDate = userActionItem.displayStartDate
        userActionItemNewList.displayEndDate = userActionItem.displayEndDate
        userActionItemNewList.groupId = userActionItem.groupId
        userActionItemNewList.creatorId = userActionItem.creatorId
        userActionItemNewList.createDate = userActionItem.createDate
        userActionItemNewList.lastModifiedBy = userActionItem.lastModifiedBy
        userActionItemNewList.lastModified = userActionItem.lastModified
        userActionItemNewList.version = userActionItem.version
        userActionItemNewList.dataOrigin = userActionItem.dataOrigin

        def result = userActionItemNewList.equals( userActionItem )
        assertTrue result

        result = userActionItem.equals( '' )
        assertFalse result

        def userActionItemNull = new UserActionItem( null )
        result = userActionItem.equals( userActionItemNull )
        assertFalse result

    }

    private UserActionItem newActionItem() {
        def userActionItemNew = new UserActionItem()
                userActionItemNew.actionItemId = 1
                userActionItemNew.pidm = 1
                userActionItemNew.status = ActionItemStatus.fetchDefaultActionItemStatus(  ).id
                userActionItemNew.userResponseDate = null
                userActionItemNew.displayStartDate = null
                userActionItemNew.displayEndDate = null
                userActionItemNew.groupId = 1
                userActionItemNew.creatorId = "GRAILS"
                userActionItemNew.createDate = new Date ()
                userActionItemNew.dataOrigin = "GRAILS"

        return userActionItemNew
    }

}
