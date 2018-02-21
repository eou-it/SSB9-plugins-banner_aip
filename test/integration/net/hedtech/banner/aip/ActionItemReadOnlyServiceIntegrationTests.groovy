/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.communication.folder.CommunicationFolder
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
        def folderId = CommunicationFolder.findByName( 'AIPstudent' ).id
        new ActionItem( name: 'A-INT-TEST1', title: 'A-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'B-INT-TEST1', title: 'B-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'C-INT-TEST1', title: 'C-INT-TEST1', folderId: folderId, status: 'A', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'D-INT-TEST1', title: 'D-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'E-INT-TEST1', title: 'E-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'F-INT-TEST1', title: 'F-INT-TEST1', folderId: folderId, status: 'A', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'G-INT-TEST1', title: 'G-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        def results = actionItemReadOnlyService.fetchWithPagingAndSortParams(
                [filterName: "%-INT-TEST1", sortColumn: "actionItemName", sortAscending: true, max: 7, offset: 0] )

        assertEquals( 7, results.size() )
        def asFound = []
        results.each {it ->
            asFound.add( it.actionItemName )
        }
        (0..4).each {it ->
            assertEquals( asFound[it], asFound.sort( false )[it] )
        }
    }
}
