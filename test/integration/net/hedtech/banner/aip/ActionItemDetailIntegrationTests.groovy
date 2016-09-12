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


class ActionItemDetailIntegrationTests extends BaseIntegrationTestCase {

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
        List<ActionItemDetail> actionItemDetailList = ActionItemDetail.fetchActionItemDetailById( actionItemId )
        assertEquals( actionItemId, actionItemDetailList[0].actionItemId )
        assertEquals( 1, actionItemDetailList.size() )
    }


    @Test
    void testFetchActionItemDetailString() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        List<ActionItemDetail> actionItemDetailList = ActionItemDetail.fetchActionItemDetailById( actionItemId )
        assertNotNull( actionItemDetailList.toString() )
        assertFalse actionItemDetailList.isEmpty()
    }


    @Test
    void testActionItemDetailHashCode() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        List<ActionItemDetail> actionItemDetailList = ActionItemDetail.fetchActionItemDetailById( actionItemId )

        def result = actionItemDetailList.hashCode()
        assertNotNull result

        def actionItemDetailObj = new ActionItemDetail()
        result = actionItemDetailObj.hashCode()
        assertNotNull result
    }


    @Test
    void testActionItemDetailEquals() {

        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemsList = actionItems[0]
        def actionItemId = actionItemsList.id
        List<ActionItemDetail> actionItemDetailList = ActionItemDetail.fetchActionItemDetailById( actionItemId )
        def actionItemDetail = actionItemDetailList[0]
        def actionItemNewDetail = new ActionItemDetail()

        actionItemNewDetail.id = actionItemDetail.id
        actionItemNewDetail.actionItemId = actionItemDetail.actionItemId
        actionItemNewDetail.text = actionItemDetail.text
        actionItemNewDetail.actionItemTemplateId = actionItemDetail.actionItemTemplateId
        actionItemNewDetail.userId = actionItemDetail.userId
        actionItemNewDetail.activityDate = actionItemDetail.activityDate
        actionItemNewDetail.version = actionItemDetail.version
        actionItemNewDetail.dataOrigin = actionItemDetail.dataOrigin
        actionItemNewDetail.actionItemTemplateId = actionItemDetail.actionItemTemplateId

        def result = actionItemNewDetail.equals( actionItemDetail )
        assertTrue result

        result = actionItemDetailList.equals( null )
        assertFalse result

        def actionItemsListNull = new ActionItemDetail( null )
        result = actionItemDetailList.equals( actionItemsListNull )
        assertFalse result

    }

}