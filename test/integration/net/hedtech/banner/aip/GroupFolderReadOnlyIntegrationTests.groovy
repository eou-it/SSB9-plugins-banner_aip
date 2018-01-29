/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class GroupFolderReadOnlyIntegrationTests extends BaseIntegrationTestCase {


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
    void testFetchGroupFolder() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        assertFalse groupFolderList.isEmpty()
    }





    @Test
    void testFetchGroupFolderById() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolderId = groupFolderList[0].groupId
        def groupFolderTitle = groupFolderList[0].groupTitle
        GroupFolderReadOnly groupFolderById = GroupFolderReadOnly.fetchGroupFolderById( groupFolderId )
        def groupFolderByIdSort = groupFolderById
        assertEquals( groupFolderById.groupTitle, groupFolderTitle )
        assertNotNull groupFolderByIdSort
    }


    @Test
    void testGroupFolderReadOnlyToString() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolderId = groupFolderList[0].groupId
        GroupFolderReadOnly groupFolderReadOnlyById = GroupFolderReadOnly.fetchGroupFolderById( groupFolderId )
        assertNotNull( groupFolderReadOnlyById.toString() )
        assertEquals(groupFolderList[0].folderName, groupFolderReadOnlyById.folderName)
    }


    @Test
    void testGroupFolderHashCode() {
        List<GroupFolderReadOnly> groupFolderList = GroupFolderReadOnly.fetchGroupFolders()
        def groupFolderId = groupFolderList[0].groupId
        GroupFolderReadOnly groupFolderById = GroupFolderReadOnly.fetchGroupFolderById( groupFolderId )

        def result = groupFolderById.hashCode()
        assertNotNull result

        def groupFolderObj = new GroupFolderReadOnly()
        result = groupFolderObj.hashCode()
        assertNotNull result
    }


    @Test
    void testGroupFolderSortNameAsc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [name: "%"],
                [sortColumn: "groupTitle", sortAscending: true, max: 10, offset: 0] )

        assertEquals( 10, results.size() )
    }


    @Test
    void testGroupFolderSortNameDesc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [name: "%"],
                [sortColumn: "groupTitle", sortAscending: false, max: 10, offset: 0] )

        assertEquals( 10, results.size() )
    }

    // sort by other than name is secondarily sorted by name
    @Test
    void testGroupFolderSortSecondaryAsc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [name: "%"],
                [sortColumn: "groupStatus", sortAscending: true, max: 50, offset: 0] )

        def foundActive = false
        def foundInactive = false
        def foundPending = false
        def activeAsFound = []
        def inactiveAsFound = []
        def pendingAsFound = []
        results.each {it ->
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
        (0..9).each() {it ->
            assertEquals( activeAsFound[it], activeAsFound.sort( false )[it] )
        }
        assertEquals( inactiveAsFound[0], inactiveAsFound.sort( false )[0] )
        assertEquals( pendingAsFound[0], pendingAsFound.sort( false )[0] )
    }


    @Test
    void testGroupFolderSortSecondaryDesc() {
        def results = GroupFolderReadOnly.fetchWithPagingAndSortParams(
                [name: "%"],
                [sortColumn: "groupStatus", sortAscending: false, max: 50, offset: 0] )
        def foundActive = false
        def foundInactive = false
        def foundPending = false
        def activeAsFound = []
        def inactiveAsFound = []
        def pendingAsFound = []
        results.each {it ->
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
        (0..9).each() {it ->
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
        GroupFolderReadOnly groupFolderById = GroupFolderReadOnly.fetchGroupFolderById( groupFolderId )
        def groupFolderNew = new GroupFolderReadOnly()

        groupFolderNew.groupId = groupFolderById.groupId
        groupFolderNew.groupTitle = groupFolderById.groupTitle
        groupFolderNew.groupName = groupFolderById.groupName
        groupFolderNew.groupDesc = groupFolderById.groupDesc
        groupFolderNew.groupStatus = groupFolderById.groupStatus
        groupFolderNew.groupUserId = groupFolderById.groupUserId
        groupFolderNew.groupActivityDate = groupFolderById.groupActivityDate
        groupFolderNew.groupVersion = groupFolderById.groupVersion
        groupFolderNew.folderDesc = groupFolderById.folderDesc
        groupFolderNew.folderName = groupFolderById.folderName
        groupFolderNew.folderId = groupFolderById.folderId
        groupFolderNew.postedInd = groupFolderById.postedInd


        def result = groupFolderNew.equals( groupFolderById )
        assertTrue result

        result = groupFolderById.equals( null )
        assertFalse result

        def groupFolderListNull = new GroupFolderReadOnly( null )
        result = groupFolderById.equals( groupFolderListNull )
        assertFalse result

    }
}
