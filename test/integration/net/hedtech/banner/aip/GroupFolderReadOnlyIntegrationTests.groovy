/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class GroupFolderReadOnlyIntegrationTests extends BaseIntegrationTestCase {


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
    void testFetchGroupFolder( ) {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        assertFalse groupFolderList.isEmpty(  )
        println groupFolderList
    }

    @Test
    void testFetchGroupFolderById() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolderId = groupFolderList[0].groupId
        List<GroupFolderReadOnly> groupFolderListById = GroupFolderReadOnly.fetchGroupFoldersById( groupFolderId )
        assertFalse groupFolderListById.isEmpty(  )
        assert 1 == groupFolderListById.size()
        println groupFolderListById
    }

}