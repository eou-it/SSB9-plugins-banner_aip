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


    @Test
    void testFetchGroupFolderROPageSortService() {
        Map params1 = [filterName:"%",sortColumn:"groupTitle", sortAscending:true, max:10, offset:0]
        Map params2 = [filterName:"%",sortColumn:"groupTitle", sortAscending:true, max:10, offset:10]
       // Map params3 = [filterName:"%",sortColumn:"groupTitle", sortAscending:true, max:10, offset:20]

        def groupFolderROList1 = groupFolderReadOnlyService.listGroupFolderPageSort(params1)
        def groupFolderROList2 = groupFolderReadOnlyService.listGroupFolderPageSort(params2)
        //def groupFolderROList3 = groupFolderReadOnlyService.listGroupFolderPageSort(params3)

        def totalCount = groupFolderROList1.result.size() + groupFolderROList2.result.size()

        def actualLength = groupFolderROList1.length

        assertEquals( actualLength, totalCount )
    }

}