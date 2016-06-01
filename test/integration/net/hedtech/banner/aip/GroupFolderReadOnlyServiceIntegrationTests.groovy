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
        println groupFolderList
    }

    @Test
    void testFetchGroupFolderByIdService() {
        List<GroupFolderReadOnly> groupFolderList = groupFolderReadOnlyService.listActionItemGroups()
        def groupFolderId = groupFolderList[0].groupId
        List<GroupFolderReadOnly> groupFolderListById = groupFolderReadOnlyService.listActionItemGroupById( groupFolderId )
        assertFalse groupFolderListById.isEmpty(  )
        assert 1 <= groupFolderListById.size()
        println groupFolderListById
    }

}