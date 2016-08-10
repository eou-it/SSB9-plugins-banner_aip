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
    void testUserActionItemROToString() {
        List<ActionItemReadOnly> actionItemReadOnlyList = ActionItemReadOnly.fetchActionItemRO()
        def folderId = actionItemReadOnlyList[0].folderId

        List<ActionItemReadOnly> actionItemsRO = ActionItemReadOnly.fetchActionItemROByFolder( folderId )
        assertNotNull( actionItemsRO.toString() )
        assertFalse actionItemsRO.isEmpty()
    }


    @Test
    void testUserActionItemROHashCode() {
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
    void testUserActionItemROEquals() {
        List<ActionItemReadOnly> actionItemReadOnlyList = ActionItemReadOnly.fetchActionItemRO()
        def folderId = actionItemReadOnlyList[0].folderId

        List<ActionItemReadOnly> actionItemsRO = ActionItemReadOnly.fetchActionItemROByFolder( folderId )
        
        def actionItemListRO = actionItemsRO[0]
        def actionItemRONewList = new ActionItemReadOnly()

        actionItemRONewList.id = actionItemListRO.id
        actionItemRONewList.name = actionItemListRO.name
        actionItemRONewList.folderId = actionItemListRO.folderId
        actionItemRONewList.folderName = actionItemListRO.folderName
        actionItemRONewList.description = actionItemListRO.description
        actionItemRONewList.status = actionItemListRO.status
        actionItemRONewList.activityDate = actionItemListRO.activityDate
        actionItemRONewList.userId = actionItemListRO.userId
        actionItemRONewList.creatorId = actionItemListRO.creatorId
        actionItemRONewList.createDate = actionItemListRO.createDate
        actionItemRONewList.version = actionItemListRO.version

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