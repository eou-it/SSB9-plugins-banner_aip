/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
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

class ActionItemContentIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchActionItemDetailById() {
        def actionItemId
        List<ActionItem> actionItemsList
        ActionItemContent actionItemDetailList

        actionItemsList = ActionItem.fetchActionItems()
        actionItemId = actionItemsList[4].id
        assertNotNull actionItemId

        actionItemDetailList = ActionItemContent.fetchActionItemContentById(actionItemId)
        assertNotNull actionItemDetailList
        assertEquals(actionItemId, actionItemDetailList.actionItemId)

    }


    @Test
    void testFetchActionItemDetailString() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[4].id
        ActionItemContent actionItemDetailList = ActionItemContent.fetchActionItemContentById(actionItemId)
        assertNotNull(actionItemDetailList.toString())
    }


    @Test
    void testActionItemDetailHashCode() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[4].id
        ActionItemContent actionItemDetailList = ActionItemContent.fetchActionItemContentById(actionItemId)

        def result = actionItemDetailList.hashCode()
        assertNotNull result

        def actionItemDetailObj = new ActionItemContent()
        result = actionItemDetailObj.hashCode()
        assertNotNull result
    }

}
