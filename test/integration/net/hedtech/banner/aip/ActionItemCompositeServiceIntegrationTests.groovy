/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemCompositeService
    def actionItemService
    def actionItemContentService
    def actionItemStatusRuleService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        loginSSB( 'CSRSTU001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void addActionItem() {
        def result = actionItemCompositeService.addActionItem( [folderId: CommunicationFolder.findByName( 'Student' ).id, status: 'Draft', title: 'title', name: 'name', description: 'description'] )
        assert result.success == true
        assert result.newActionItem.description == 'description'
        assert result.message == null
    }


    @Test
    void addActionItemFailedFolderValidation() {
        def result = actionItemCompositeService.addActionItem( [folderId: null, status: 'Draft', title: 'title', name: 'name', description: 'description'] )
        assert result.success == false
        assert result.newActionItem == null
        assert result.message == 'Save failed. The Folder Id can not be null or empty.'
    }


    @Test
    void addActionItemFailedNoActionItemName() {
        def result = actionItemCompositeService.addActionItem( [folderId: CommunicationFolder.findByName( 'Student' ).id, status: 'Draft', title: 'title', name: null, description: 'description'] )
        assert result.success == false
        assert result.newActionItem == null
        assert result.message == 'Save failed. The Name can not be null or empty.'
    }


    @Test
    void deleteActionItem() {
        def result = actionItemCompositeService.addActionItem( [folderId: CommunicationFolder.findByName( 'Student' ).id, status: 'Draft', title: 'title', name: 'name', description: 'description'] )
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        result = actionItemCompositeService.deleteActionItem( ai.id )
        assert result.message == 'Delete successful'
        assert result.success == true
    }


    @Test
    void deleteActionItemFailedCase() {
        def result = actionItemCompositeService.addActionItem( [folderId: CommunicationFolder.findByName( 'Student' ).id, status: 'Draft', title: 'title', name: 'name', description: 'description'] )
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        ai.postedIndicator = 'Y'
        actionItemService.update( ai )
        result = actionItemCompositeService.deleteActionItem( ai.id )
        assert result.message == 'Cannot be deleted. Action item has been posted.'
        assert result.success == false
    }


    @Test
    void deleteActionItemWithActionItemContent() {
        def result = actionItemCompositeService.addActionItem( [folderId: CommunicationFolder.findByName( 'Student' ).id, status: 'Draft', title: 'title', name: 'name', description: 'description'] )
        assert result.success == true
        assert result.newActionItem.description == 'description'
        ActionItem ai = result.newActionItem
        actionItemContentService.create( new ActionItemContent(
                actionItemId: ai.id,
                actionItemTemplateId: (ActionItemTemplate.findAll()[0]).id,
                text: 'Text'
        ) )
        actionItemStatusRuleService.create( new ActionItemStatusRule(
                actionItemId: ai.id,
                seqOrder: 1,
                labelText: 'Text'
        ) )
        result = actionItemCompositeService.deleteActionItem( ai.id )
        assert result.success == true
    }


    @Test
    void getActionItemsListForSelect() {
        def result = actionItemCompositeService.getActionItemsListForSelect()
        assert result.size() > 0
        assertTrue data.find {it.actionItemName == 'Meet with Advisor'}.actionItemName == 'Meet with Advisor'
    }
}
