/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemContentServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemContentService

    def actionItemService


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
    void testFetchActionItemDetailByIdService() {
        List<ActionItem> actionItemsList = actionItemService.listActionItems()
        def actionItemId = actionItemsList[7].id
        ActionItemContent actionItemDetailId = actionItemContentService.listActionItemContentById( actionItemId )
        assertEquals( actionItemId, actionItemDetailId.actionItemId )
    }

}
