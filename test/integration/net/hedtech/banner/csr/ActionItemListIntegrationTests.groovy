/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr
import org.junit.Before
import org.junit.Test
import org.junit.After

import net.hedtech.banner.testing.BaseIntegrationTestCase


class ActionItemListIntegrationTests extends BaseIntegrationTestCase {

    def actionItemListService

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
        List<ActionItemList> actionItems = ActionItemList.fetchActionItems()
        assertFalse actionItems.isEmpty(  )
    }


    @Test
    void testFetchActionItemsService( ) {
        List<ActionItemList> actionItemsList = actionItemListService.listActionItems( )
        assertFalse actionItemsList.isEmpty(  )
    }


    @Test
    void testFetchActionItemByIdService() {
        def actionItemId = 2

        String actionItemText = actionItemListService.listActionItemById(actionItemId)
        assertEquals('Drug and Alcohol Policy', actionItemText)
    }
}