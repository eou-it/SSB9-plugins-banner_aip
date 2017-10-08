/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.UserActionItemReadOnly
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class UserActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchUserActionItemByROPidm() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
        assertEquals( 10, userActionItems.size() )
    }


    @Test
    void testFetchUserActionItemByROPidmNoReuslts() {
        def actionItemPidm = PersonUtility.getPerson( "STUADV425" ).pidm
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
        assertEquals( 0, userActionItems.size() )
        //println userActionItems
    }

/*
    @Test
    void testFetchUserActionItemByROPidmHashCode() {
        def actionItemPidm = PersonUtility.getPerson( "STUADV425" ).pidm
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
        // select the first id of an action from that id list
        def hashCode = userActionItems.hashCode()
        assertNotNull hashCode
    }


    @Test
    void testFetchUserActionItemByROPidmEquals() {
        def actionItemPidm = PersonUtility.getPerson( "STUADV425" ).pidm
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
        def equalsRtn = userActionItems.equals( userActionItems )
        assertTrue equalsRtn
    }

*/


    @Test
    void testUserActionItemROToString() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItemsRO = UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )
        assertNotNull( userActionItemsRO.toString() )
        assertFalse userActionItemsRO.isEmpty()
    }


    @Test
    void testUserActionItemROHashCode() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItemsRO = UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )

        def result = userActionItemsRO.hashCode()
        assertNotNull result

        def userActionItemROObj = new UserActionItemReadOnly()
        result = userActionItemROObj.hashCode()
        assertNotNull result

    }


    @Test
    void testUserActionItemROEquals() {

        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItemsRO = UserActionItemReadOnly.fetchUserActionItemsROByPidm( actionItemPidm )


        def userActionItemListRO = userActionItemsRO[0]
        def userActionItemRONewList = new UserActionItemReadOnly()

        userActionItemRONewList.id = userActionItemListRO.id
        userActionItemRONewList.title = userActionItemListRO.title
        userActionItemRONewList.name = userActionItemListRO.name
        userActionItemRONewList.activeTmpl = userActionItemListRO.activeTmpl
        userActionItemRONewList.activityDateTmpl = userActionItemListRO.activityDateTmpl
        userActionItemRONewList.userIdTmpl = userActionItemListRO.userIdTmpl
        userActionItemRONewList.description = userActionItemListRO.description
        userActionItemRONewList.creatorIdTmpl = userActionItemListRO.creatorIdTmpl
        userActionItemRONewList.createDateTmpl = userActionItemListRO.createDateTmpl
        userActionItemRONewList.versionTmpl = userActionItemListRO.versionTmpl
        userActionItemRONewList.pidm = userActionItemListRO.pidm
        userActionItemRONewList.status = userActionItemListRO.status
        userActionItemRONewList.isBlocking = userActionItemListRO.isBlocking
        userActionItemRONewList.completedDate = userActionItemListRO.completedDate
        userActionItemRONewList.activityDate = userActionItemListRO.activityDate
        userActionItemRONewList.userId = userActionItemListRO.userId
        userActionItemRONewList.creatorId = userActionItemListRO.creatorId
        userActionItemRONewList.createDate = userActionItemListRO.createDate
        userActionItemRONewList.version = userActionItemListRO.version
        userActionItemRONewList.displayStartDate= userActionItemListRO.displayStartDate
        userActionItemRONewList.displayEndDate= userActionItemListRO.displayEndDate

        def result = userActionItemRONewList.equals( userActionItemListRO )
        assertTrue result

        result = userActionItemListRO.equals( null )
        assertFalse result

        def userActionItemListNull = new UserActionItemReadOnly( null )
        result = userActionItemListRO.equals( userActionItemListNull )
        assertFalse result

    }
}
