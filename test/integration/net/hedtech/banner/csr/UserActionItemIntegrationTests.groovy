/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr
import org.junit.Before
import org.junit.Test
import org.junit.After

import net.hedtech.banner.testing.BaseIntegrationTestCase


class UserActionItemIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchUserActionItemById() {
        def actionItemId = 114

        List<UserActionItem> userActionItemId = userActionItemService.listActionItemById( actionItemId )
        //println userActionItemId
        assertFalse userActionItemId.isEmpty(  )
        assert 1 == userActionItemId.size()

    }

    @Test
    void testFetchUserActionItemByPidm() {
        def actionItemPidm = 124018

        List<UserActionItem> userActionItems = userActionItemService.listActionItemByPidm( actionItemPidm )
        assert 10 == userActionItems.size()
    }
}