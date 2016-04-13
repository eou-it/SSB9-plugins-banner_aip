/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr

import net.hedtech.banner.general.person.PersonUtility
import org.junit.Before
import org.junit.Test
import org.junit.After
import net.hedtech.banner.testing.BaseIntegrationTestCase


class UserActionItemIntegrationTests extends BaseIntegrationTestCase {

    def userActionItemService
    def actionItemService

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
        List<ActionItem> actionItemList = actionItemService.listActionItems()

        def actionItemId = actionItemList[0].id
        List<UserActionItem> userActionItemId = userActionItemService.listActionItemById( actionItemId )
        assert 1 >= userActionItemId.size()
        println userActionItemId.size()
    }

    @Test
    void testFetchUserActionItemByPidm() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm
        List<UserActionItem> userActionItems = userActionItemService.listActionItemByPidm( actionItemPidm )
        assert 10 == userActionItems.size()
        println userActionItems
    }
}