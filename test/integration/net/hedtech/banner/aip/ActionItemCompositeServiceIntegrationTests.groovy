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
}
