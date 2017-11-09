/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemReadOnlyService
    def actionItemContentService

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
    void testGetActionItemROById() {
        ActionItemReadOnly aiReadOnly = actionItemReadOnlyService.listActionItemRO(  )[3]
        assertNotNull( aiReadOnly )
        ActionItemReadOnly actionItemRO = actionItemReadOnlyService.getActionItemROById( aiReadOnly.actionItemId )
        assertEquals( aiReadOnly.actionItemId, actionItemRO.actionItemId )
        assertEquals( aiReadOnly.actionItemDesc, actionItemRO.actionItemDesc )
        assertEquals( aiReadOnly.folderDesc, actionItemRO.folderDesc )
        assertEquals( aiReadOnly.folderName, actionItemRO.folderName )
    }

    @Test
    void testFetchActionItemROPageSortService() {
        Map params1 = [filterName:"%",sortColumn:"actionItemName", sortAscending:true, max:10, offset:0]

        Map params2 = [filterName:"%",sortColumn:"actionItemName", sortAscending:true, max:10, offset:10]

        Map params3 = [filterName:"%",sortColumn:"actionItemName", sortAscending:true, max:20, offset:20]

        def actionItemROList1 = actionItemReadOnlyService.listActionItemsPageSort(params1)
        def actionItemROList2 = actionItemReadOnlyService.listActionItemsPageSort(params2)
        def actionItemROList3 = actionItemReadOnlyService.listActionItemsPageSort(params3)

        def totalCount = actionItemROList1.result.size() + actionItemROList2.result.size() + actionItemROList3.result.size()

        def actualLength = actionItemROList1.length

        assertEquals( actualLength, totalCount )
    }


    @Test
    void testCompositeDate() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        ActionItemContent myActionItemDetail = ActionItemContent.fetchActionItemContentById( actionItemId )
        assertEquals( actionItemId, myActionItemDetail.actionItemId )

        // update takes care of updating date
        myActionItemDetail.lastModified = new java.util.Date()
        actionItemContentService.update( myActionItemDetail)

        ActionItemReadOnly updatedAIRO = actionItemReadOnlyService.getActionItemROById( actionItemId )
        assertEquals( myActionItemDetail.lastModified, updatedAIRO.actionItemCompositeDate )
    }
}
