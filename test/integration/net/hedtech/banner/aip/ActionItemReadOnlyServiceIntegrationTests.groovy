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
    void testFetchActionItemByROFolderNoResults() {

        def folderId = 0

        List<ActionItemReadOnly> actionItemsRO = actionItemReadOnlyService.listActionItemROByFolder( folderId )
        assertEquals( 0, actionItemsRO.size() )
    }


    @Test
    void testFetchActionItemByROFolderService() {
        List<ActionItemReadOnly> actionItemROList = actionItemReadOnlyService.listActionItemRO()
        def folderId = actionItemROList[0].folderId

        List<ActionItemReadOnly> actionItemsRO = actionItemReadOnlyService.listActionItemROByFolder(folderId)
        assert 0 < actionItemsRO.size()
    }


    @Test
    void testFetchActionItemROPageSortService() {
        Map params1 = [filterName:"%",sortColumn:"actionItemName", sortAscending:true, max:10, offset:0]

        Map params2 = [filterName:"%",sortColumn:"actionItemName", sortAscending:true, max:10, offset:10]

        Map params3 = [filterName:"%",sortColumn:"actionItemName", sortAscending:true, max:10, offset:20]

        def actionItemROList1 = actionItemReadOnlyService.listActionItemsPageSort(params1)
        def actionItemROList2 = actionItemReadOnlyService.listActionItemsPageSort(params2)
        def actionItemROList3 = actionItemReadOnlyService.listActionItemsPageSort(params3)

        def totalCount = actionItemROList1.result.size() + actionItemROList2.result.size() + actionItemROList3.result.size()

        def actualLength = actionItemROList1.length

        assertEquals( actualLength, totalCount )
    }


}