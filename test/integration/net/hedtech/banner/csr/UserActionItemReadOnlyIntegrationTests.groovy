/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr
import org.junit.Before
import org.junit.Test
import org.junit.After

import net.hedtech.banner.testing.BaseIntegrationTestCase


class UserActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchUserActionItemByROPidmService() {
        def actionItemPidm = 124018

        List<UserActionItemReadOnly> userActionItems = UserActionItemReadOnly.fetchUserActionItemROByPidm(actionItemPidm)
        //assert 10 == userActionItems.size()
        println userActionItems
    }
}