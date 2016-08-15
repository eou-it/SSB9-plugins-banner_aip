/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItemReadOnly
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemReadOnlyService

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
    void testFetchUserActionItemByROFolderNoResults() {

        def folderId = 0

        List<ActionItemReadOnly> actionItemsRO = actionItemReadOnlyService.listActionItemROByFolder( folderId )
        assertEquals( 0, actionItemsRO.size() )
    }


    @Test
    void testFetchUserActionItemByROFolderService() {
        List<ActionItemReadOnly> actionItemROList = actionItemReadOnlyService.listActionItemRO()
        def folderId = actionItemROList[0].folderId

        List<ActionItemReadOnly> actionItemsRO = actionItemReadOnlyService.listActionItemROByFolder(folderId)
        assert 0 < actionItemsRO.size()
    }


    @Test
    void testFetchUserActionItemROPageSortService() {
        Map params1 = [filterName:"%",sortColumn:"folderName", sortDirection:"desc", max:5, offset:0]

        Map params2 = [filterName:"%",sortColumn:"folderName", sortDirection:"desc", max:5, offset:5]

        def actionItemROList1 = actionItemReadOnlyService.listActionItemsPageSort(params1)
        def actionItemROList2 = actionItemReadOnlyService.listActionItemsPageSort(params2)

        println actionItemROList1
        println actionItemROList2

        assertEquals( 5, actionItemROList1.result.size() )
    }


}