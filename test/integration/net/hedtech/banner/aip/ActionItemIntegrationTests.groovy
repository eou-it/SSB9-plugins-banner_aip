/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItem
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
    void testFetchActionItemsString() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemList = actionItems[0]
        assertNotNull( actionItemList.toString() )
        assertFalse actionItems.isEmpty()
    }


    @Test
    void testFetchActionItems() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        assertFalse actionItems.isEmpty()
    }


    @Test
    void testActionItemsHashCode() {

        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemList = actionItems[0]

        def result = actionItemList.hashCode()
        assertNotNull result

        def actionItemListObj = new ActionItem()
        assertNotNull actionItemListObj
    }


    @Test
    void testActionItemsEquals() {

        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemsList = actionItems[0]
        def actionItemNewList = new ActionItem(
                active: actionItemsList.active,
                activityDate: actionItemsList.activityDate,
                createDate: actionItemsList.createDate,
                creatorId: actionItemsList.creatorId,
                dataOrigin: actionItemsList.dataOrigin,
                description: actionItemsList.description,
                title: actionItemsList.title,
                userId: actionItemsList.userId )
        long actionItemListId = actionItemsList.id
        long actionItemListVersion = actionItemsList.version
        actionItemNewList.id = actionItemListId
        actionItemNewList.version = actionItemListVersion

        def result = actionItemsList.equals( actionItemsList )
        assertTrue result

        result = actionItemsList.equals( actionItemNewList )
        assertTrue result

        result = actionItemsList.equals( null )
        assertFalse result

        def actionItemsListNull = new ActionItem( null )
        result = actionItemsList.equals( actionItemsListNull )
        assertFalse result

    }
}