/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr
import org.junit.Before
import org.junit.Test
import org.junit.After

import net.hedtech.banner.testing.BaseIntegrationTestCase


class ActionItemIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchActionItems( ) {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        assertFalse actionItems.isEmpty(  )
    }


    @Test
    void testFetchActionItemsService( ) {
        List<ActionItem> actionItems = actionItemService.listActionItems( )
        assertFalse actionItems.isEmpty(  )
    }


    @Test
    void testFetchActionItemByIdService() {
        def actionItemId = 2

        String actionItemText = actionItemService.listActionItemById(actionItemId)
        assertEquals('Drug and Alcohol Policy', actionItemText)
    }
}