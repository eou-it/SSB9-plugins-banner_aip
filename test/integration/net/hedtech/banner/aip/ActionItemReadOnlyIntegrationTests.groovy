/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItemReadOnly
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchUserActionItemROByFolder() {
        //def actionItemPidm = PersonUtility.getPerson( "CSRSTU018" ).pidm

        List<ActionItemReadOnly> actionItemReadOnlyList = ActionItemReadOnly.fetchActionItemRO()
        def folderId = actionItemReadOnlyList[0].folderId

        List<ActionItemReadOnly> actionItems = ActionItemReadOnly.fetchActionItemROByFolder( folderId )
        //assertEquals( 10, actionItems.size() )

        println actionItems

        assert 0 < actionItems.size()
    }

    @Test
    void testFetchUserActionItemROByFolderNoResults() {
        def folderId = 0

        List<ActionItemReadOnly> actionItems = ActionItemReadOnly.fetchActionItemROByFolder( folderId )
        assertEquals( 0, actionItems.size() )
        //println actionItems
    }


    @Test
    void testActionItemROToString() {
        List<ActionItemReadOnly> actionItemReadOnlyList = ActionItemReadOnly.fetchActionItemRO()
        def folderId = actionItemReadOnlyList[0].folderId

        List<ActionItemReadOnly> actionItemsRO = ActionItemReadOnly.fetchActionItemROByFolder( folderId )
        assertNotNull( actionItemsRO.toString() )
        assertFalse actionItemsRO.isEmpty()
    }

    @Test
    void testActionItemSortNameAsc() {
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "actionItemName", sortAscending: true, max: 10, offset: 0])

        assertEquals( 10, results.size() )
    }

    @Test
    void testActionItemSortNameDesc() {
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "actionItemName", sortAscending: false, max: 10, offset: 0] )

        assertEquals( 10, results.size() )
    }

    @Test
    void testActionItemROHashCode() {
        List<ActionItemReadOnly> actionItemReadOnlyList = ActionItemReadOnly.fetchActionItemRO()
        def folderId = actionItemReadOnlyList[0].folderId

        List<ActionItemReadOnly> actionItemsRO = ActionItemReadOnly.fetchActionItemROByFolder( folderId )

        def result = actionItemsRO.hashCode()
        assertNotNull result

        def actionItemROObj = new ActionItemReadOnly()
        result = actionItemROObj.hashCode()
        assertNotNull result
    }

    @Test
    void testActionItemROEquals() {
        List<ActionItemReadOnly> actionItemReadOnlyList = ActionItemReadOnly.fetchActionItemRO()
        def folderId = actionItemReadOnlyList[0].folderId

        List<ActionItemReadOnly> actionItemsRO = ActionItemReadOnly.fetchActionItemROByFolder( folderId )

        def actionItemListRO = actionItemsRO[0]
        def actionItemRONewList = new ActionItemReadOnly()

        actionItemRONewList.actionItemId = actionItemListRO.actionItemId
        actionItemRONewList.actionItemName = actionItemListRO.actionItemName
        actionItemRONewList.folderId = actionItemListRO.folderId
        actionItemRONewList.folderName = actionItemListRO.folderName
        actionItemRONewList.folderDesc = actionItemListRO.folderDesc
        actionItemRONewList.actionItemDesc = actionItemListRO.actionItemDesc
        actionItemRONewList.actionItemStatus = actionItemListRO.actionItemStatus
        actionItemRONewList.actionItemActivityDate = actionItemListRO.actionItemActivityDate
        actionItemRONewList.actionItemUserId = actionItemListRO.actionItemUserId
        actionItemRONewList.actionItemCreatorId = actionItemListRO.actionItemCreatorId
        actionItemRONewList.actionItemCreateDate = actionItemListRO.actionItemCreateDate
        actionItemRONewList.actionItemVersion = actionItemListRO.actionItemVersion
        actionItemRONewList.actionItemTemplateId = actionItemListRO.actionItemTemplateId
        actionItemRONewList.actionItemTemplateName = actionItemListRO.actionItemTemplateName
        actionItemRONewList.actionItemContentId = actionItemListRO.actionItemContentId
        actionItemRONewList.actionItemContent = actionItemListRO.actionItemContent
        actionItemRONewList.actionItemPageName = actionItemListRO.actionItemPageName

        def result = actionItemRONewList.equals( actionItemListRO )
        assertTrue result

        //todo: figure out why null test doesn't work for actionItemListRO
        //result = actionItemListRO.equals( null )
       // println actionItemListRO
        //assertFalse result

        def actionItemListNull = new ActionItemReadOnly( null )
        result = actionItemListRO.equals( actionItemListNull )
        assertFalse result
    }
}