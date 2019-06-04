/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip


import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import grails.util.GrailsWebMockUtil
import grails.web.servlet.context.GrailsWebApplicationContext
import net.hedtech.banner.aip.block.process.ActionItemBlockedProcess
import net.hedtech.banner.aip.block.process.BlockingProcess
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.grails.plugins.testing.GrailsMockHttpServletRequest
import org.grails.plugins.testing.GrailsMockHttpServletResponse
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemCompositeService
    def actionItemService
    def actionItemContentService
    def actionItemStatusRuleService
    def actionItemBlockedProcessService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        webAppCtx = new GrailsWebApplicationContext()
        mockRequest()
        loginSSB('CSRSTU001', '111111')
    }

    GrailsWebRequest mockRequest() {
        GrailsMockHttpServletRequest mockRequest = new GrailsMockHttpServletRequest();
        GrailsMockHttpServletResponse mockResponse = new GrailsMockHttpServletResponse();
        GrailsWebMockUtil.bindMockWebRequest(webAppCtx, mockRequest, mockResponse)
    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void addActionItem() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        assert result.message == null
    }


    @Test
    void addActionItemEmptyMap() {
        def result = actionItemCompositeService.addActionItem([:])
        assertFalse result.success
        assertNull result.newActionItem
        assertNotNull result.message
    }


    @Test
    void addActionItemInvalidFolderId() {
        def result = actionItemCompositeService.addActionItem([folderId: '123', status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assertFalse result.success
        assertNotNull result.message
        assertNull result.newActionItem
    }


    @Test
    void updateActionItem() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        assert result.message == null

        Map editParam = [actionItemId: result.newActionItem.id,
                         folderId    : CommunicationFolder.findByName('Student').id,
                         status      : 'Active',
                         title       : 'title',
                         name        : 'name',
                         description : 'description1']
        result = actionItemCompositeService.editActionItem(editParam)
        assert result.success == true
        assert result.updatedActionItem.description == 'description1'
        assert result.updatedActionItem.name == 'name'
        assert result.updatedActionItem.status == 'A'
        assert result.message == null
    }


    @Test
    void updateActionItemDupicateFolder() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        assert result.message == null
        ActionItem actionItem1 = result.newActionItem

        result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Registration').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        assert result.message == null
        Map editParam = [actionItemId: actionItem1.id,
                         folderId    : CommunicationFolder.findByName('Registration').id,
                         status      : 'Active',
                         title       : 'title',
                         name        : 'name',
                         description : 'description1']
        result = actionItemCompositeService.editActionItem(editParam)
        assert result.success == false
        assert result.message == 'Save failed. The Action Item Name and Folder must be unique.'
    }


    @Test
    void updateActionItemNameChange() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        assert result.message == null

        Map editParam = [actionItemId: result.newActionItem.id,
                         folderId    : CommunicationFolder.findByName('Student').id,
                         status      : 'Complete',
                         title       : 'title',
                         name        : 'name2',
                         description : 'description1']
        result = actionItemCompositeService.editActionItem(editParam)
        assert result.success == false
        assert result.message == 'Action Item name cannot be updated.'
    }


    @Test
    void updateActionItemFolderValidation() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        assert result.message == null

        Map editParam = [actionItemId: result.newActionItem.id,
                         folderId    : null,
                         status      : 'Complete',
                         title       : 'title',
                         name        : 'name',
                         description : 'description1']
        result = actionItemCompositeService.editActionItem(editParam)
        assert result.success == false
        assert result.message == 'Save failed. The Folder Id can not be null or empty.'
    }


    @Test
    void addActionItemFailedFolderValidation() {
        def result = actionItemCompositeService.addActionItem([folderId: null, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == false
        assert result.newActionItem == null
        assert result.message == 'Save failed. The Folder Id can not be null or empty.'
    }


    @Test
    void addActionItemFailedNoActionItemName() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: null, description: 'description'])
        assert result.success == false
        assert result.newActionItem == null
        assert result.message == 'Save failed. The Name can not be null or empty.'
    }


    @Test
    void deleteActionItem() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        result = actionItemCompositeService.deleteActionItem(ai.id)
        assert result.message == 'The action item with its corresponding records have been deleted'
        assert result.success == true
    }


    @Test
    void deleteActionItemFailedCase() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        ai.postedIndicator = 'Y'
        actionItemService.update(ai)
        result = actionItemCompositeService.deleteActionItem(ai.id)
        assert result.message == 'The action item cannot be deleted because it has been posted to users.'
        assert result.success == false
    }


    @Test
    void deleteActionItemWithActionItemContent() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        actionItemContentService.create(new ActionItemContent(
                actionItemId: ai.id,
                actionItemTemplateId: (ActionItemTemplate.findAll()[0]).id,
                text: 'Text'
        ))
        actionItemStatusRuleService.create(new ActionItemStatusRule(
                actionItemId: ai.id,
                seqOrder: 1,
                labelText: 'Text',
                reviewReqInd: false,
                allowedAttachments: 0
        ))
        result = actionItemCompositeService.deleteActionItem(ai.id)
        assert result.success == true
    }

    @Test
    void deleteActionItemWithBlockedProcess() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        ActionItemBlockedProcess savedActionItemBlockedProcess = actionItemBlockedProcessService.create(new ActionItemBlockedProcess(
                blockActionItemId: ai.id,
                blockedProcessId: BlockingProcess.findByProcessName('Prepare for Registration').id
        ))
        assertNotNull(savedActionItemBlockedProcess.blockActionItemId)
        assertEquals(savedActionItemBlockedProcess.blockActionItemId, ai.id)

        ActionItemContent savedActionItemContent = actionItemContentService.create(new ActionItemContent(
                actionItemId: ai.id,
                actionItemTemplateId: (ActionItemTemplate.findAll()[0]).id,
                text: 'Text'
        ))
        assertNotNull(savedActionItemContent.actionItemId)
        assertEquals(savedActionItemContent.actionItemId, ai.id)

        ActionItemStatusRule savedActionItemStatusRule = actionItemStatusRuleService.create(new ActionItemStatusRule(
                actionItemId: ai.id,
                seqOrder: 1,
                labelText: 'Text',
                reviewReqInd: false,
                allowedAttachments: 0
        ))
        assertNotNull(savedActionItemStatusRule.id)
        assertEquals(savedActionItemStatusRule.actionItemId, ai.id)
        assertEquals(savedActionItemStatusRule.seqOrder, 1)
        assertEquals(savedActionItemStatusRule.labelText, 'Text')

        result = actionItemCompositeService.deleteActionItem(ai.id)
        assert result.success == true
        assertNull(ActionItem.findById(ai.id))
        assertNull(ActionItemBlockedProcess.findByBlockActionItemId(ai.id))
        assertNull(ActionItemContent.findByActionItemId(ai.id))
        assertNull(ActionItemStatusRule.findByActionItemId(ai.id))

    }

    @Test
    void getActionItemsListForSelect() {
        def result = actionItemCompositeService.getActionItemsListForSelect()
        assert result.size() > 0
        assertTrue result.find { it.actionItemName == 'Meet with Advisor' }.actionItemName == 'Meet with Advisor'
    }


    @Test
    void updateActionItemDetailWithTemplate() {
        def map = [templateId          : ActionItemTemplate.findByTitle('Master Template').id,
                   actionItemId        : ActionItem.findByName('All staff: Prepare for winter snow').id,
                   actionItemDetailText: 'text']
        ActionItem aim = ActionItem.findByName('All staff: Prepare for winter snow')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def result = actionItemCompositeService.updateActionItemDetailWithTemplate(map)
        assert result.success == true
        assert result.errors == []
        assert result.actionItem.actionItemName == 'All staff: Prepare for winter snow'

    }


    @Test
    void validateEditActionItemContent() {
        def result = actionItemCompositeService.validateEditActionItemContent(-999)
        assert result.editable == false
        assert result.message == 'Action Item not present'
    }


    @Test
    void validateEditActionItemContentMarkedPosted() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        ai.postedIndicator = 'Y'
        actionItemService.update(ai)
        result = actionItemCompositeService.validateEditActionItemContent(ai.id)
        assert result.editable == false
        assert result.message == 'Cannot be updated. Action Item is posted.'
    }


    @Test
    void validateEditActionItemContentPostingNo() {
        def result = actionItemCompositeService.addActionItem([folderId: CommunicationFolder.findByName('Student').id, status: 'Draft', title: 'title', name: 'name', description: 'description'])
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        actionItemService.update(ai)
        result = actionItemCompositeService.validateEditActionItemContent(ai.id)
        assert result.editable == true
        assert result.message == null
    }


}
