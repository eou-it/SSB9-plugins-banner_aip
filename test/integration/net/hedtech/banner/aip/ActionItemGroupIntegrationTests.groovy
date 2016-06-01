/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemGroupIntegrationTests extends BaseIntegrationTestCase {


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
    void testFetchActionItemGroups() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        assertFalse actionItemGroups.isEmpty()
        //println actionItemGroups
    }


    @Test
    void testFetchActionItemGroupById() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroupId = actionItemGroups[0].id
        def actionItemGroupTitle = actionItemGroups[0].title
        List<ActionItemGroup> actionItemGroup = ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
        assertEquals( actionItemGroupTitle, actionItemGroup[0].title )
        assertEquals( 1, actionItemGroup.size() )
        //println actionItemGroup
    }


    @Test
    void testActionItemGroupHashCode() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def hashCode = actionItemGroups.hashCode()
        assertNotNull hashCode
        // println actionItems
    }


    @Test
    void testActionItemGroupEquals() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def equalsRtn = actionItemGroups.equals( actionItemGroups )
        assertTrue equalsRtn
        // println actionItems
    }

}