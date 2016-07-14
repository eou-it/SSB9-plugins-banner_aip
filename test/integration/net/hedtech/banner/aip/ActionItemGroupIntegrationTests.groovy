/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

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
    }


    @Test
    void testFetchActionItemGroupById() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroupId = actionItemGroups[0].id
        def actionItemGroupTitle = actionItemGroups[0].title
        def actionItemGroup = ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
        assertEquals( actionItemGroupTitle, actionItemGroup.title )
    }


    @Test
    void testActionItemGroupToString() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups(  )
        def actionItemGroupId = actionItemGroups[0].id
        def expected = actionItemGroups[0].title
        ActionItemGroup actionItemGroupById = ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
        assertNotNull( actionItemGroups.toString() )
        assertFalse actionItemGroups.isEmpty()
        assertNotNull( actionItemGroupById.toString() )
        assertEquals( expected, actionItemGroupById.title )
    }


    @Test
    void testActionItemGroupHashCode() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def hashCode = actionItemGroups.hashCode()
        assertNotNull hashCode
    }


    @Test
    void testActionItemGroupEquals() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        def actionItemGroupNewList = new ActionItemGroup()

        actionItemGroupNewList.id = actionItemGroup.id
        actionItemGroupNewList.title = actionItemGroup.title
        actionItemGroupNewList.folderId = actionItemGroup.folderId
        actionItemGroupNewList.description = actionItemGroup.description
        actionItemGroupNewList.status = actionItemGroup.status
        actionItemGroupNewList.userId = actionItemGroup.userId
        actionItemGroupNewList.activityDate = actionItemGroup.activityDate
        actionItemGroupNewList.version = actionItemGroup.version
        actionItemGroupNewList.dataOrigin = actionItemGroup.dataOrigin

        def result = actionItemGroupNewList.equals( actionItemGroup )
        assertTrue result

        result = actionItemGroups.equals( null )
        assertFalse result

        def actionItemsGroupListNull = new ActionItemGroup( null )
        result = actionItemGroups.equals( actionItemsGroupListNull )
        assertFalse result

    }

}