/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemContentServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemContentService

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
    void testFetchActionItemDetailByIdService() {
        List<ActionItem> actionItemsList = actionItemService.listActionItems()
        def actionItemId = actionItemsList[0].id
        ActionItemContent actionItemDetailId = actionItemContentService.listActionItemContentById( actionItemId )
        assertEquals( actionItemId, actionItemDetailId.actionItemId )
    }

}
