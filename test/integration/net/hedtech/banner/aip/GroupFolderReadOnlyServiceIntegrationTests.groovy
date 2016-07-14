/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class GroupFolderReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def groupFolderReadOnlyService

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
    void testFetchGroupFolderService( ) {
        List<GroupFolderReadOnlyService> groupFolderList = groupFolderReadOnlyService.listActionItemGroups()
        assertFalse groupFolderList.isEmpty(  )
    }

    @Test
    void testFetchGroupFolderByIdService() {
        List<GroupFolderReadOnly> groupFolderList = groupFolderReadOnlyService.listActionItemGroups()
        def groupFolderId = groupFolderList[0].groupId
        def groupFolderTitle = groupFolderList[0].groupTitle
        List<GroupFolderReadOnly> groupFolderListById = groupFolderReadOnlyService.getActionItemGroupById( groupFolderId )
        assertFalse groupFolderListById.isEmpty(  )
        assertEquals(groupFolderListById[0].groupTitle, groupFolderTitle)
        assertEquals( 1, groupFolderListById.size() )
    }

}