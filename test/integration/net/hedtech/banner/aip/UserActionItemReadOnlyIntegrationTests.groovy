/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchUserActionItemByROPidm() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemsROByPidmDate( actionItemPidm )
        assertEquals( 10, userActionItems.size() )
    }


    @Test
    void checkIfActionItemPresent() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        assert UserActionItemReadOnly.checkIfActionItemPresent( actionItemPidm ) == true
    }


    @Test
    void testFetchUserActionItemByROPidmNoReuslts() {
        def actionItemPidm = PersonUtility.getPerson( "STUADV425" ).pidm
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemsROByPidmDate( actionItemPidm )
        assertEquals( 0, userActionItems.size() )
        //println userActionItems
    }


    @Test
    void testUserActionItemROToString() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItemsRO = UserActionItemReadOnly.fetchUserActionItemsROByPidmDate( actionItemPidm )
        assertNotNull( userActionItemsRO.toString() )
        assertFalse userActionItemsRO.isEmpty()
    }


    @Test
    void testUserActionItemROHashCode() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItemsRO = UserActionItemReadOnly.fetchUserActionItemsROByPidmDate( actionItemPidm )

        def result = userActionItemsRO.hashCode()
        assertNotNull result

        def userActionItemROObj = new UserActionItemReadOnly()
        result = userActionItemROObj.hashCode()
        assertNotNull result

    }

}
