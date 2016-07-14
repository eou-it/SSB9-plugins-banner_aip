/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemGroupServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemGroupService


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
    void testFetchActionItemGroupsService() {
        List<ActionItemGroup> actionItemGroups = actionItemGroupService.listActionItemGroups()
        assertFalse actionItemGroups.isEmpty()
    }


    @Test
    void testFetchActionItemGroupByIdService() {
        List<ActionItemGroup> actionItemGroups = actionItemGroupService.listActionItemGroups()
        def actionItemGroupId = actionItemGroups[0].id
        def actionItemGroupTitle = actionItemGroups[0].title
        def actionItemGroup = actionItemGroupService.listActionItemGroupById( actionItemGroupId )
        assertEquals( actionItemGroupTitle, actionItemGroup.title )
    }

}