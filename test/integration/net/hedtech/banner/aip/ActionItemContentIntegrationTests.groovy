/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemContentIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchActionItemDetailById() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        ActionItemContent actionItemDetailList = ActionItemContent.fetchActionItemContentById( actionItemId )
        assertEquals( actionItemId, actionItemDetailList.actionItemId )
    }


    @Test
    void testFetchActionItemDetailString() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        ActionItemContent actionItemDetailList = ActionItemContent.fetchActionItemContentById( actionItemId )
        assertNotNull( actionItemDetailList.toString() )
    }


    @Test
    void testActionItemDetailHashCode() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        ActionItemContent actionItemDetailList = ActionItemContent.fetchActionItemContentById( actionItemId )

        def result = actionItemDetailList.hashCode()
        assertNotNull result

        def actionItemDetailObj = new ActionItemContent()
        result = actionItemDetailObj.hashCode()
        assertNotNull result
    }


    @Test
    void testActionItemDetailEquals() {

        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemsList = actionItems[0]
        def actionItemId = actionItemsList.id
        ActionItemContent actionItemContent = ActionItemContent.fetchActionItemContentById( actionItemId )
        def actionItemNewDetail = new ActionItemContent()

        actionItemNewDetail.id = actionItemContent.id
        actionItemNewDetail.actionItemId = actionItemContent.actionItemId
        actionItemNewDetail.text = actionItemContent.text
        actionItemNewDetail.actionItemTemplateId = actionItemContent.actionItemTemplateId
        actionItemNewDetail.lastModifiedby = actionItemContent.lastModifiedby
        actionItemNewDetail.lastModified = actionItemContent.lastModified
        actionItemNewDetail.version = actionItemContent.version
        actionItemNewDetail.dataOrigin = actionItemContent.dataOrigin
        actionItemNewDetail.actionItemTemplateId = actionItemContent.actionItemTemplateId

        def result = actionItemNewDetail.equals( actionItemContent )
        assertTrue result

        result = actionItemContent.equals( null )
        assertFalse result

        def actionItemsListNull = new ActionItemContent( null )
        result = actionItemContent.equals( actionItemsListNull )
        assertFalse result

    }

}