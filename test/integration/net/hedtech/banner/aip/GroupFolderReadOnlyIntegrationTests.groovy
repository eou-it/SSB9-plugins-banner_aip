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
    void testFetchGroupFolder() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        assertFalse groupFolderList.isEmpty()
        //println groupFolderList
    }


    @Test
    void testFetchGroupFolderById() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolderId = groupFolderList[0].groupId
        def groupFolderTitle = groupFolderList[0].groupTitle
        List<GroupFolderReadOnly> groupFolderListById = GroupFolderReadOnly.fetchGroupFoldersById( groupFolderId )
        def groupFolderByIdSort = groupFolderListById
        assertFalse groupFolderListById.isEmpty()
        assertEquals( groupFolderListById[0].groupTitle, groupFolderTitle )
        assertEquals( 1, groupFolderListById.size() )
        assertNotNull groupFolderByIdSort
    }


    @Test
    void testGroupFolderReadOnlyToString() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolderId = groupFolderList[0].groupId
        List<GroupFolderReadOnly> groupFolderReadOnlyById = GroupFolderReadOnly.fetchGroupFoldersById( groupFolderId )
        assertNotNull( groupFolderReadOnlyById.toString() )
        assertFalse groupFolderReadOnlyById.isEmpty()
    }


    @Test
    void testGroupFolderHashCode() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolderId = groupFolderList[0].groupId
        List<GroupFolderReadOnly> groupFolderListById = GroupFolderReadOnly.fetchGroupFoldersById( groupFolderId )

        def result = groupFolderListById.hashCode()
        assertNotNull result

        def groupFolderObj = new GroupFolderReadOnly()
        result = groupFolderObj.hashCode()
        assertNotNull result
    }


    @Test
    void testGroupFolderEquals() {

        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolder = groupFolderList[0]
        def groupFolderId = groupFolder.groupId
        List<GroupFolderReadOnly> groupFolderById = GroupFolderReadOnly.fetchGroupFoldersById( groupFolderId )
        def groupFolderByIdList = groupFolderById[0]
        def groupFolderNewList = new GroupFolderReadOnly()

        groupFolderNewList.groupId = groupFolderByIdList.groupId
        groupFolderNewList.groupTitle = groupFolderByIdList.groupTitle
        groupFolderNewList.groupDesc = groupFolderByIdList.groupDesc
        groupFolderNewList.groupStatus = groupFolderByIdList.groupStatus
        groupFolderNewList.groupUserId = groupFolderByIdList.groupUserId
        groupFolderNewList.groupActivityDate = groupFolderByIdList.groupActivityDate
        groupFolderNewList.groupVersion = groupFolderByIdList.groupVersion
        groupFolderNewList.groupVpdiCode = groupFolderByIdList.groupVpdiCode
        groupFolderNewList.folderDesc = groupFolderByIdList.folderDesc
        groupFolderNewList.folderName = groupFolderByIdList.folderName

        def result = groupFolderNewList.equals( groupFolderByIdList )
        assertTrue result

        result = groupFolderById.equals( null )
        assertFalse result

        def groupFolderListNull = new GroupFolderReadOnly( null )
        result = groupFolderByIdList.equals( groupFolderListNull )
        assertFalse result

    }


}