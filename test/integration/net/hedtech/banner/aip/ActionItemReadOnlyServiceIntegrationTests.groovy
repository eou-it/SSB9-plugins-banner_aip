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
        Map params = [filterName:"%",sortColumn:"folderName", sortDirection:"desc", max:20, offset:0]
        def actionItemROList = actionItemReadOnlyService.listActionItemsPageSort(params)

        println actionItemROList

        //assertEquals( 20, actionItemROList.result.size() )
    }


}