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
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemByPidm( actionItemPidm )
        assertEquals( 10, userActionItems.size() )
        //println userActionItems
    }


    @Test
    void testFetchUserActionItemByIdService() {
        //get the user's action items by a pidm
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemByPidm( actionItemPidm )
        // select the first id of an action from that id list
        def actionItemId = userActionItems[0].id
        //get action item by id for that user
        List<UserActionItem> userActionItemId = UserActionItem.fetchUserActionItemById( actionItemId )
        assertEquals( actionItemId, userActionItemId[0].id )
        assertEquals( 1, userActionItemId.size() )
        //println userActionItemId.size()
    }


    @Test
    void testUserActionItemsToString() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemByPidm( actionItemPidm )
        assertNotNull( userActionItems.toString() )
        assertFalse userActionItems.isEmpty()
    }


    @Test
    void testUserActionItemHashCode() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemByPidm( actionItemPidm )

        def userActionItemId = userActionItems[0].id
        List<UserActionItem> userActionItemList = UserActionItem.fetchUserActionItemById( userActionItemId )

        def result = userActionItemList.hashCode()
        assertNotNull result

        def userActionITemListObj = new UserActionItem()
        result = userActionITemListObj.hashCode()
        assertNotNull result

    }


    @Test
    void testUserActionItemEquals() {

        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemByPidm( actionItemPidm )

        def userActionItemId = userActionItems[0].id
        List<UserActionItem> userActionItemListById = UserActionItem.fetchUserActionItemById( userActionItemId )

        def userActionItemList = userActionItemListById[0]
        def userActionItemNewList = new UserActionItem()

        userActionItemNewList.id = userActionItemList.id
        userActionItemNewList.actionItemId = userActionItemList.actionItemId
        userActionItemNewList.pidm = userActionItemList.pidm
        userActionItemNewList.status = userActionItemList.status
        userActionItemNewList.completedDate = userActionItemList.completedDate
        userActionItemNewList.userId = userActionItemList.userId
        userActionItemNewList.activityDate = userActionItemList.activityDate
        userActionItemNewList.creatorId = userActionItemList.creatorId
        userActionItemNewList.createDate = userActionItemList.createDate
        userActionItemNewList.version = userActionItemList.version
        userActionItemNewList.dataOrigin = userActionItemList.dataOrigin

        def result = userActionItemNewList.equals( userActionItemList )
        assertTrue result

        result = userActionItemListById.equals( null )
        assertFalse result

        def userActionItemListNull = new UserActionItem( null )
        result = userActionItemListById.equals( userActionItemListNull )
        assertFalse result

    }
}