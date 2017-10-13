/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemGroupAssignReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemGroupAssignReadOnlyService


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
    void testListBlockedProcessById() {
        ActionItemGroupAssignReadOnly domain = ActionItemGroupAssignReadOnly.findByGroupName( 'Enrollment' )
        def activeActionItems = actionItemGroupAssignReadOnlyService.fetchActiveActionItemByGroupId( domain.actionItemGroupId, [max: 10, offset: 0] )
        assertEquals( activeActionItems.size() > 0 )

    }

    @Test
    void fetchGroupLookup() {
        def paginationParams=[max: 10, offset: 0]
        def groupFolderList = actionItemGroupAssignReadOnlyService.fetchGroupLookup( '', paginationParams )
        assertFalse groupFolderList.isEmpty()
        groupFolderList = actionItemGroupAssignReadOnlyService.fetchGroupLookup( null,paginationParams)
        assertFalse groupFolderList.isEmpty()

        groupFolderList = actionItemGroupAssignReadOnlyService.fetchGroupLookup( 'AIPstudent', paginationParams )
        assertFalse groupFolderList.isEmpty()

        groupFolderList = actionItemGroupAssignReadOnlyService.fetchGroupLookup( 'student', paginationParams )
        assertFalse groupFolderList.isEmpty()
        groupFolderList = actionItemGroupAssignReadOnlyService.fetchGroupLookup( 'STUDENT', paginationParams )
        assertFalse groupFolderList.isEmpty()
    }
}
