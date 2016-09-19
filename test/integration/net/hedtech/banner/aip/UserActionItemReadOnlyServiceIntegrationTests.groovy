/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class UserActionItemReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def userActionItemReadOnlyService

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
    void testFetchUserActionItemByROPidmNoResults() {
        def actionItemPidm = PersonUtility.getPerson( "STUADV425" ).pidm
        List<UserActionItemReadOnly> userActionItems = userActionItemReadOnlyService.listActionItemsByPidm( actionItemPidm )
        assertEquals( 0, userActionItems.size() )
    }


    @Test
    void testFetchUserActionItemByROPidmService() { // FIXME: do in service test
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItems = userActionItemReadOnlyService.listActionItemsByPidm( actionItemPidm )
        assertEquals( 10, userActionItems.size() )
    }


    @Test
    void testFetchBlockingUserActionItemsROByPidm() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItemReadOnly> userActionItems = userActionItemReadOnlyService.listBlockingActionItemsByPidm( actionItemPidm )
        userActionItems.each { item ->
            assertTrue item.isBlocking
        }
        assertTrue( userActionItems.size() >= 5 )
        assertTrue( userActionItems.size() < 10 )
    }


    @Test
    void testFetchBlockingUserActionItemsROByPidmAllComplete() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU013" ).pidm
        List<UserActionItemReadOnly> userActionItems = userActionItemReadOnlyService.listBlockingActionItemsByPidm( actionItemPidm )
        assertTrue( userActionItems.size() == 0 )
    }
}