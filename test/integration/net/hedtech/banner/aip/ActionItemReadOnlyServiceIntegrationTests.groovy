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
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testGetActionItemROById() {
        ActionItemReadOnly aiReadOnly = actionItemReadOnlyService.listActionItemRO()[3]
        assertNotNull( aiReadOnly )
        ActionItemReadOnly actionItemRO = actionItemReadOnlyService.getActionItemROById( aiReadOnly.actionItemId )
        assertEquals( aiReadOnly.actionItemId, actionItemRO.actionItemId )
        assertEquals( aiReadOnly.actionItemDesc, actionItemRO.actionItemDesc )
        assertEquals( aiReadOnly.folderDesc, actionItemRO.folderDesc )
        assertEquals( aiReadOnly.folderName, actionItemRO.folderName )
    }


    @Test
    void testCompositeDate() {
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        ActionItemContent myActionItemDetail = ActionItemContent.fetchActionItemContentById( actionItemId )
        assertEquals( actionItemId, myActionItemDetail.actionItemId )

        // update takes care of updating date
        myActionItemDetail.lastModified = new java.util.Date()
        actionItemContentService.update( myActionItemDetail )

        ActionItemReadOnly updatedAIRO = actionItemReadOnlyService.getActionItemROById( actionItemId )
        assertEquals( myActionItemDetail.lastModified, updatedAIRO.actionItemCompositeDate )
    }


    @Test
    void testActionItemSortNameAsc() {
        def results = actionItemReadOnlyService.fetchWithPagingAndSortParams(
                [filterName: "%", sortColumn: "actionItemName", sortAscending: true, max: 20, offset: 0] )

        assertEquals( 20, results.size() )
        def asFound = []
        results.each {it ->
            asFound.add( it.actionItemName )
        }
        (0..4).each {it ->
            assertEquals( asFound[it], asFound.sort( false )[it] )
        }
    }
}
