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
        ai.title = 'Test Action Item'
        ai.userId = 'something'
        ai.description = 'this is some action item'
        ai.activityDate = new java.util.Date( System.currentTimeMillis() )
        ActionItem createdItem = actionItemService.create( ai )
        assertEquals( 'Test Action Item', createdItem.title )
        assertEquals( folderId, createdItem.folderId )
    }


    @Test
    void testCreateActionItemFailsFolder() {
        def folderId = 919278
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.active = 'Y'
        ai.title = 'Test Action Item'
        ai.userId = 'something'
        ai.description = 'this is some action item'
        ai.activityDate = new java.util.Date( System.currentTimeMillis() )
        try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail. Must have valid folder"
        } catch (ApplicationException e) {
            assertTrue( e.message.contains( 'Could not execute JDBC batch update' ) )
            assertTrue( e.message.contains( 'GENERAL.FK_GCBACTM_INV_GCRFLDR' ) )
        }
    }
}