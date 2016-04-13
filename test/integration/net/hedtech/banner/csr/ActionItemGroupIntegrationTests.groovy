/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemGroupIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchActionItemGroups( ) {
        List<ActionItemGroup> actionItemGroups = actionItemGroupService.listActionItemGroups()
        assertFalse actionItemGroups.isEmpty(  )
        println actionItemGroups
    }

    @Test
    void testFetchActionItemGroupById() {
        List<ActionItemGroup> actionItemGroups = actionItemGroupService.listActionItemGroups()
        def actionItemGroupId = actionItemGroups[0].id
        List<ActionItemGroup> actionItemGroup = actionItemGroupService.listActionItemGroupById( actionItemGroupId )
        assert 1 >= actionItemGroup.size()
        println actionItemGroup
    }

}