/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.UserActionItem
import net.hedtech.banner.general.person.PersonUtility
import org.junit.Before
import org.junit.Test
import org.junit.After
import net.hedtech.banner.testing.BaseIntegrationTestCase


class UserActionItemIntegrationTests extends BaseIntegrationTestCase {

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
        userActionItemNewList.userId = userActionItem.userId
        userActionItemNewList.activityDate = userActionItem.activityDate
        userActionItemNewList.creatorId = userActionItem.creatorId
        userActionItemNewList.createDate = userActionItem.createDate
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
}
