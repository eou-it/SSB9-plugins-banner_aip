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
    void testGroupFolderSortNameAsc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "groupTitle", sortAscending: true, max: 10, offset: 0] )

        assertEquals( 10, results.size() )
    }


    @Test
    void testGroupFolderSortNameDesc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "groupTitle", sortAscending: false, max: 10, offset: 0] )

        assertEquals( 10, results.size() )
    }

    // sort by other than name is secondarily sorted by name
    @Test
    void testGroupFolderSortSecondaryAsc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "groupStatus", sortAscending: true, max: 50, offset: 0] )

        def foundActive = false
        def foundInactive = false
        def foundPending = false
        def activeAsFound = []
        def inactiveAsFound = []
        def pendingAsFound = []
        results.each { it ->
            if (it.groupStatus == 'active') {
                assertFalse foundInactive
                assertFalse foundPending
                foundActive = true
                activeAsFound.add( it.groupTitle )
            }
            if (it.groupStatus == 'inactive') {
                assertTrue( foundActive )
                assertFalse foundPending
                foundInactive = true
                inactiveAsFound.add( it.groupTitle )
            }
            if (it.groupStatus == 'pending') {
                assertTrue foundInactive
                assertTrue foundPending
                foundPending = true
                pendingAsFound.add( it.groupTitle )
            }
        }
        (0..9).each() { it ->
            assertEquals( activeAsFound[it], activeAsFound.sort( false )[it] )
        }
        assertEquals( inactiveAsFound[0], inactiveAsFound.sort( false )[0] )
        assertEquals( pendingAsFound[0], pendingAsFound.sort( false )[0] )
    }


    @Test
    void testGroupFolderSortSecondaryDesc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "groupStatus", sortAscending: false, max: 50, offset: 0] )
        def foundActive = false
        def foundInactive = false
        def foundPending = false
        def activeAsFound = []
        def inactiveAsFound = []
        def pendingAsFound = []
        results.each { it ->
            if (it.groupStatus == 'pending') {
                assertFalse foundInactive
                assertFalse foundPending
                foundPending = true
                pendingAsFound.add( it.groupTitle )
            }
            if (it.groupStatus == 'inactive') {
                assertFalse( foundActive )
                assertTrue foundPending
                foundInactive = true
                inactiveAsFound.add( it.groupTitle )
            }
            if (it.groupStatus == 'active') {
                assertTrue foundInactive
                assertTrue foundPending
                foundActive = true
                activeAsFound.add( it.groupTitle )
            }

        }
        (0..9).each() { it ->
            assertEquals( activeAsFound[it], activeAsFound.sort( false )[it] )
        }
        assertEquals( inactiveAsFound[0], inactiveAsFound.sort( false )[0] )
        assertEquals( pendingAsFound[0], pendingAsFound.sort( false )[0] )
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
        groupFolderNewList.folderDesc = groupFolderByIdList.folderDesc
        groupFolderNewList.folderName = groupFolderByIdList.folderName
        groupFolderNewList.folderId = groupFolderByIdList.folderId

        def result = groupFolderNewList.equals( groupFolderByIdList )
        assertTrue result

        result = groupFolderById.equals( null )
        assertFalse result

        def groupFolderListNull = new GroupFolderReadOnly( null )
        result = groupFolderByIdList.equals( groupFolderListNull )
        assertFalse result

    }
}