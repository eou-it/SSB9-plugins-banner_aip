/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class GroupFolderReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def groupFolderReadOnlyService


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
    void testFetchGroupFolderService() {
        List<GroupFolderReadOnlyService> groupFolderList = groupFolderReadOnlyService.listActionItemGroups()
        assertFalse groupFolderList.isEmpty()
    }


    @Test
    void testFetchGroupFolderByIdService() {
        List<GroupFolderReadOnly> groupFolderList = groupFolderReadOnlyService.listActionItemGroups()
        def groupFolderId = groupFolderList[0].groupId
        def groupFolderTitle = groupFolderList[0].groupTitle
        def groupFolderById = groupFolderReadOnlyService.getActionItemGroupById( groupFolderId )
        assertFalse groupFolderById.isEmpty()
        assertEquals( groupFolderById.groupTitle, groupFolderTitle )
    }


    @Test
    void testFetchGroupFolderROPageSortService() {
        def paginationParams1 = [sortColumn: "groupTitle", sortAscending: true, max: 10, offset: 0]
        def groupFolderROList1 = groupFolderReadOnlyService.listGroupFolderPageSort( [name: "%"], paginationParams1 )
        def paginationParams2 = [sortColumn: "groupTitle", sortAscending: true, max: groupFolderROList1.length, offset: 10]
        def groupFolderROList2 = groupFolderReadOnlyService.listGroupFolderPageSort( [name: "%"], paginationParams2 )
        def totalCount = groupFolderROList1.result.size() + groupFolderROList2.result.size()
        def actualLength = groupFolderROList1.length
        assertEquals( actualLength, totalCount )
    }

}
