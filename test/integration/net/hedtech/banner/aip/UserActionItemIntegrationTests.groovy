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
    void testUserActionItemHashCode() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemByPidm( actionItemPidm )
        // select the first id of an action from that id list
        def actionItemId = userActionItems[0].id
        def hashCode = userActionItems.hashCode()
        assertNotNull hashCode
    }


    @Test
    void testUserActionItemEquals() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = UserActionItem.fetchUserActionItemByPidm( actionItemPidm )
        // select the first id of an action from that id list
        def actionItemId = userActionItems[0].id
        def equalsRtn = userActionItems.equals( userActionItems )
        assertTrue equalsRtn
    }


}