/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class ActionItemServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemService

    def communicationFolderService


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
    void testFetchActionItemsService() {
        List<ActionItem> actionItems = actionItemService.listActionItems()
        assertFalse actionItems.isEmpty()
    }


    @Test
    void testCreateActionItem() {
        def folderId = communicationFolderService.list()[0].id
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.active = 'Y'
        ai.title = 'Test Action Item. unique 98d7efh'
        ai.userId = 'something'
        ai.description = 'this is some action item'
        ai.activityDate = new java.util.Date( System.currentTimeMillis() )
        ActionItem createdItem = actionItemService.create( ai )
        assertEquals( 'Test Action Item. unique 98d7efh', createdItem.title )
        assertEquals( folderId, createdItem.folderId )
        def verifyActionItem = actionItemService.getActionItemById( createdItem.id )
        println verifyActionItem
        assertNotNull( verifyActionItem )
        assertEquals( 'Test Action Item. unique 98d7efh', verifyActionItem.title )
        assertEquals( folderId, verifyActionItem.folderId )
    }


    @Test
    void testCreateActionItemFailsBadFolder() {
        def folderId = 919278
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.active = 'Y'
        ai.title = 'Test Action Item'
        ai.userId = 'something'
        ai.description = 'this is some action item'
        ai.activityDate = new java.util.Date( System.currentTimeMillis() )
        // fails due to no folder matching id
        try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail because folder does not exist."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:FolderDoesNotExist@@" ) )

        }
    }


    @Test
    void testCreateActionItemFailsNoTitle() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.active = 'Y'
        ai.title = null
        ai.userId = 'something'
        ai.description = 'this is some action item'
        ai.activityDate = new java.util.Date( System.currentTimeMillis() )
        // fails due to no folder matching id
        try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail because folder does not exist."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:TitleCanNotBeNullError@@" ) )

        }
    }


    @Test
    void testCreateActionItemFailsFolderTitleCombo() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.active = 'Y'
        ai.title = existingAI.title
        ai.userId = 'something'
        ai.description = 'this is some action item'
        ai.activityDate = new java.util.Date( System.currentTimeMillis() )
        try {
            actionItemService.create( ai )
            Assert.fail "Expected duplicate title in folder to fail because of name unique constraint."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:UniqueTitleInFolderError@@" ) )

        }
    }
}