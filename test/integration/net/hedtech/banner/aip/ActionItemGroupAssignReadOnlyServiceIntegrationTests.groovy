/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemGroupAssignReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemGroupAssignReadOnlyService


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
    void testListBlockedProcessById() {
        ActionItemGroupAssignReadOnly domain = ActionItemGroupAssignReadOnly.findByGroupName( 'Enrollment' )
        def activeActionItems = actionItemGroupAssignReadOnlyService.fetchActiveActionItemByGroupId( domain.actionItemGroupId )
        assert activeActionItems.size() > 0

    }


    @Test
    void getAssignedActionItemsInGroup() {
        ActionItemGroupAssignReadOnly domain = ActionItemGroupAssignReadOnly.findByGroupName( 'Enrollment' )
        def activeActionItems = actionItemGroupAssignReadOnlyService.getAssignedActionItemsInGroup( domain.actionItemGroupId )
        assert activeActionItems.size() > 0

    }


    @Test
    void fetchGroupLookup() {
        def groupFolderList = actionItemGroupAssignReadOnlyService.fetchGroupLookup()
        assertFalse groupFolderList.isEmpty()
    }
}
