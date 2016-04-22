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
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemROByPidm(actionItemPidm)
        assert 10 == userActionItems.size()
    }


    @Test
    void testFetchUserActionItemByROPidmNoReuslts() {
        def actionItemPidm = PersonUtility.getPerson( "STUADV425" ).pidm
        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemROByPidm( actionItemPidm )
        assert 0 == userActionItems.size()
        println userActionItems
    }

}