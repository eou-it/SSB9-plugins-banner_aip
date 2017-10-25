/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class UserActionItemServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemService

    def userActionItemService


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchUserActionItemByPidm() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = userActionItemService.listActionItemsByPidm( actionItemPidm )
        assertEquals( 10, userActionItems.size() )
        //println userActionItems
    }


    @Test
    void testFetchUserActionItemById() {
        //get the user's action items by a pidm
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = userActionItemService.listActionItemsByPidm( actionItemPidm )
        // select the first id of an action from that id list
        def actionItemId = userActionItems[0].id
        //get action item by id for that user
        UserActionItem userActionItemId = userActionItemService.getActionItemById( actionItemId )
        assertEquals( actionItemId, userActionItemId.id )
    }

    @Test
    void testRejectDuplicateDateOverlap() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> existingUserActionItems = userActionItemService.listActionItemsByPidm( actionItemPidm )

        // existing date range (at time of writing this test) is 2/9/2016 - 12/9/2017 . Increment start by one day
        def existingUserActionItem = existingUserActionItems[0]
        def origSize = existingUserActionItems.size(  )
        UserActionItem userActionItem = new UserActionItem()
        userActionItem.pidm = actionItemPidm
        userActionItem.actionItemId = existingUserActionItem.actionItemId
        userActionItem.status = 1
        userActionItem.displayStartDate = existingUserActionItem.displayStartDate + 1
        userActionItem.displayEndDate = existingUserActionItem.displayEndDate
        userActionItem.groupId = existingUserActionItem.groupId
        userActionItem.userId = existingUserActionItem.userId
        userActionItem.activityDate = new Date()
        userActionItem.creatorId = existingUserActionItem.creatorId
        userActionItem.createDate = new Date()
        userActionItem.dataOrigin = existingUserActionItem.dataOrigin
        def message = shouldFail( ApplicationException ) {
            userActionItemService.create( userActionItem )
        }
        assertEquals( "@@r1:AlreadyExistsCondition@@", message)

        List<UserActionItem> newUserActionItems = userActionItemService.listActionItemsByPidm( actionItemPidm )
        assertEquals( origSize, newUserActionItems.size() )
    }


    @Test
    void testAcceptDuplicateNoDateOverlap() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> existingUserActionItems = userActionItemService.listActionItemsByPidm( actionItemPidm )

        // existing date range (at time of writing this test) is 2/9/2016 - 12/9/2017 .
        // make start and end well past this range
        def startDate = new Date().copyWith( year: 2024, month: Calendar.APRIL, dayOfMonth: 3)
        def endDate = new Date().copyWith( year: 2024, month: Calendar.JULY, dayOfMonth: 3)
        def existingUserActionItem = existingUserActionItems[0]
        def origSize = existingUserActionItems.size()
        UserActionItem userActionItem = new UserActionItem()
        userActionItem.pidm = actionItemPidm
        userActionItem.actionItemId = existingUserActionItem.actionItemId
        userActionItem.status = 1
        userActionItem.displayStartDate = startDate
        userActionItem.displayEndDate = endDate
        userActionItem.groupId = existingUserActionItem.groupId
        userActionItem.userId = existingUserActionItem.userId
        userActionItem.activityDate = new Date()
        userActionItem.creatorId = existingUserActionItem.creatorId
        userActionItem.createDate = new Date()
        userActionItem.dataOrigin = existingUserActionItem.dataOrigin
        userActionItemService.create( userActionItem )
        List<UserActionItem> newUserActionItems = userActionItemService.listActionItemsByPidm( actionItemPidm )

        assertEquals( origSize + 1, newUserActionItems.size() )
    }
}