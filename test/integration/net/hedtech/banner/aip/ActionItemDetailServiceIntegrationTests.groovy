/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.aip.ActionItemDetail
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemDetailServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemDetailService

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
        ActionItemDetail actionItemDetailId = actionItemDetailService.listActionItemDetailById( actionItemId )
        assertEquals( actionItemId, actionItemDetailId.actionItemId )
        //println actionItemDetailId
    }

}