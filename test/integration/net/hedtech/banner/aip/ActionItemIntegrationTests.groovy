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
    void testFetchActionItems() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        assertFalse actionItems.isEmpty()
        // println actionItems
        // println actionItems.hashCode()
    }


    @Test
    void testActionItemsHashCode() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def hashCode = actionItems.hashCode()
        assertNotNull hashCode
        // println actionItems
    }


    @Test
    void testActionItemsEquals() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def equalsRtn = actionItems.equals( actionItems )
        assertTrue equalsRtn
        // println actionItems
    }


}