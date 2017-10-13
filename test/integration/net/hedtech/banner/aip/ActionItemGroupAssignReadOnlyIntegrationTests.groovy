/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemGroupAssignReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void fetchGroupLookup() {
        def paginationParams = [max: 10, offset: 0]
        def groupFolderList = ActionItemGroupAssignReadOnly.fetchGroupLookup( '', paginationParams )
        assertFalse groupFolderList.isEmpty()
        groupFolderList = ActionItemGroupAssignReadOnly.fetchGroupLookup( null, paginationParams )
        assertFalse groupFolderList.isEmpty()

        groupFolderList = ActionItemGroupAssignReadOnly.fetchGroupLookup( 'AIPstudent', paginationParams )
        assertFalse groupFolderList.isEmpty()

        groupFolderList = ActionItemGroupAssignReadOnly.fetchGroupLookup( 'student', paginationParams )
        assertFalse groupFolderList.isEmpty()
        groupFolderList = ActionItemGroupAssignReadOnly.fetchGroupLookup( 'STUDENT', paginationParams )
        assertFalse groupFolderList.isEmpty()
    }
}
