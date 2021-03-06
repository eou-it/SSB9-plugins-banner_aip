/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail

@Integration
@Transactional
class ActionItemServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemService

    def communicationFolderService


    @Before
    void setUp() {
        formContext = ['GUAGMNU','SELFSERVICE']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchActionItemsService() {
        List<ActionItem> actionItems = actionItemService.listActionItems()
        assertFalse actionItems.isEmpty()
    }


    @Test
    void checkActionItemPosted() {
        ActionItem actionItem = ActionItem.findByName( 'Drug and Alcohol Policy' )
        actionItem.postedIndicator = 'Y'
        actionItem = actionItem.save( flush: true )
        assert actionItem.postedIndicator == 'Y'
        assert actionItemService.checkActionItemPosted( actionItem.id )
    }


    @Test
    void testCreateActionItem() {
        def folderId = communicationFolderService.list()[0].id
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.status = 'D'
        ai.name = 'Test Action Item. unique 98d7efh '+new Date().toString().hashCode().toString()
        ai.title = 'Test Action Item. unique 98d7efh'
        ai.description = 'this is some action item'
        ai.postedIndicator = 'N'
        ActionItem createdItem = actionItemService.create( ai )
        assertEquals( 'Test Action Item. unique 98d7efh', createdItem.title )
        assertEquals( folderId, createdItem.folderId )
        def verifyActionItem = actionItemService.getActionItemById( createdItem.id )
        assertNotNull( verifyActionItem )
        assertEquals( 'Test Action Item. unique 98d7efh', verifyActionItem.title )
        assertEquals( folderId, verifyActionItem.folderId )
    }


    @Test
    void testCreateActionItemFailsBadFolder() {
        def folderId = 919278
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.status = 'P'
        ai.name = 'Test Action Item'
        ai.title = 'Test Action Item'
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        // fails due to no folder matching id
        try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail because folder does not exist."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:FolderDoesNotExist@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.folder.validation.error' ) )

        }
    }


    @Test
    void testCreateActionItemFailsNoTitle() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = 'Pending'
        ai.title = null
        ai.description = 'this is some action item'
        // fails due to no folder matching id
        try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail because folder does not exist."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:TitleCanNotBeNullError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.title.nullable.error' ) )
        }
    }


    @Test
    void testCreateActionItemFailsNoStatus() {
        ActionItem existingAI = actionItemService.list()[6]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = null
        ai.name = ' a title ds8f4h3'+new Date().toString()
        ai.title = ' a title ds8f4h3'
        ai.description = 'this is some action item'
        // fails due to no folder matching id
        try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail because folder does not exist."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:StatusCanNotBeNullError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.status.nullable.error' ) )
        }
    }


    @Test
    void testCreateActionItemFailsNoFolderId() {
        ActionItem existingAI = actionItemService.list()[7]
        assertNotNull existingAI
        ActionItem ai = new ActionItem()
        ai.folderId = null
        ai.name ='Test Action Item'
        ai.status = 'A'
        ai.title = ' a title ds8f4h3 2'
        ai.description = 'this is some action item'
        // fails due to no folder matching id
       /* try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail because folder does not exist."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( ActionItemService.NO_FOLDER_ERROR ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.folderId.nullable.error' ) )
        }*/

        def excption = shouldFail(ApplicationException){
            actionItemService.create( ai )
        }
        assertEquals ActionItemService.NO_FOLDER_ERROR,excption.wrappedException.getMessage()
        assertEquals  'actionItem.folderId.nullable.error',excption.defaultMessage


    }


    @Test
    void testCreateActionItemFailsMaxSize() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = 'Ginormous Status 123456789012345678901234567890'
        ai.name = ' a title ds8f4h3'
        ai.title = ' a title ds8f4h3'
        ai.description = 'this is some action item'
        // fails due to no folder matching id
        try {
            actionItemService.create( ai )
            Assert.fail "Expected to fail because folder does not exist."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( ActionItemService.MAX_SIZE_ERROR ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'max.size' ) )
        }
    }


    @Test
    void testCreateActionItemFailsFolderTitleCombo() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = 'P'
        ai.name = existingAI.name
        ai.title = existingAI.title
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        try {
            actionItemService.create( ai )
            Assert.fail "Expected duplicate title in folder to fail because of name unique constraint."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:UniqueNameInFolderError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.name.unique.error' ) )
        }
    }


    @Test
    void testCreateActionItemFailWithDomainAsMap() {
        ActionItem existingAI = actionItemService.list()[7]
        Map ai = [
                folderId       : existingAI.folderId,
                status         : 'P',
                name           : existingAI.name,
                title          : existingAI.title,
                postedIndicator: 'N',
                description    : 'this is some action item']
        try {
            actionItemService.create( [domainModel: ai] )
            Assert.fail "Expected duplicate title in folder to fail because of name unique constraint."
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:UniqueNameInFolderError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.name.unique.error' ) )
        }
    }


    @Test
    void testCreateActionItemFailMaxSizeForName() {
        ActionItem existingAI = actionItemService.list()[7]
        String largeString = 'Large Text Large Text Large Text Large TextLarge Text  Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text '
        Map ai = [
                folderId       : existingAI.folderId,
                status         : 'P',
                name           : existingAI.name + largeString,
                title          : existingAI.title,
                postedIndicator: 'N',
                description    : 'this is some action item']
        try {
            actionItemService.create( [domainModel: ai] )
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:MaxSizeError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.max.size.error' ) )
        }
    }


    @Test
    void testValidateUpdateActionItemSuccess() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = 'P'
        ai.name = existingAI.name
        ai.title = existingAI.title
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        actionItemService.validateUpdate( ai, existingAI.folderId )
    }


    @Test
    void testValidateUpdateActionItemFailNullName() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = 'P'
        ai.name = null
        ai.title = existingAI.title
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        try {
            actionItemService.validateUpdate( ai, existingAI.folderId )
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:NameCanNotBeNullError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.name.nullable.error' ) )
        }
    }


    @Test
    void testValidateUpdateActionItemFailNullTitle() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = 'P'
        ai.name = existingAI.name
        ai.title = null
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        try {
            actionItemService.validateUpdate( ai, existingAI.folderId )
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:TitleCanNotBeNullError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.title.nullable.error' ) )
        }
    }


    @Test
    void testValidateUpdateActionItemFailNullFolder() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = null
        ai.status = 'P'
        ai.name = existingAI.name
        ai.title = existingAI.title
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        try {
            actionItemService.validateUpdate( ai, existingAI.folderId )
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:FolderCanNotBeNullError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.folderId.nullable.error' ) )
        }
    }


    @Test
    void testValidateUpdateActionItemFailNullStatus() {
        ActionItem existingAI = actionItemService.list()[7]
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = null
        ai.name = existingAI.name
        ai.title = existingAI.title
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        try {
            actionItemService.validateUpdate( ai, existingAI.folderId )
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:StatusCanNotBeNullError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.status.nullable.error' ) )
        }
    }


    @Test
    void testValidateUpdateActionItemFailExceedNameSize() {
        ActionItem existingAI = actionItemService.list()[7]
        String largeString = 'Large Text Large Text Large Text Large TextLarge Text  Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text Large Text '
        ActionItem ai = new ActionItem()
        ai.folderId = existingAI.folderId
        ai.status = 'D' + largeString
        ai.name = existingAI.name
        ai.title = existingAI.title
        ai.postedIndicator = 'N'
        ai.description = 'this is some action item'
        try {
            actionItemService.validateUpdate( ai, existingAI.folderId )
        } catch (ApplicationException e) {
            assertTrue( e.getMessage().toString().contains( "@@r1:MaxSizeError@@" ) )
            assertTrue( e.getDefaultMessage().toString().contains( 'actionItem.max.size.error' ) )
        }
    }
}
