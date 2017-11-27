/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemGroupCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemGroupCompositeService
    def actionItemService


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
    void testCreateGroupSuccess() {
        Map map = [
                groupTitle : 'groupTitle',
                groupName  : 'groupName',
                folderId   : CommunicationFolder.findByName( 'Student' ).id,
                groupDesc  : 'groupDesc',
                postingInd : 'N',
                groupStatus: 'Draft',
        ]
        def result = actionItemGroupCompositeService.createGroup( map )
        assert result.success == true
        assert result.message == null
        assert result.group.groupStatus == 'Draft'
        assert result.group.groupTitle == 'groupTitle'
    }


    @Test
    void testCreateGroupFailed() {
        Map map = [
                groupTitle : 'groupTitle',
                groupName  : null,
                folderId   : CommunicationFolder.findByName( 'Student' ).id,
                groupDesc  : 'groupDesc',
                postingInd : 'N',
                groupStatus: 'Draft',
        ]
        def result = actionItemGroupCompositeService.createGroup( map )
        assert result.success == false
        assert result.message == 'Save failed. The Name can not be null or empty.'
    }


    @Test
    void testCreateGroupFailedDuplicate() {
        Map map = [
                groupTitle : 'groupTitle',
                groupName  : 'groupName',
                folderId   : -1,
                groupDesc  : 'groupDesc',
                postingInd : 'N',
                groupStatus: 'Draft',
        ]
        def result = actionItemGroupCompositeService.createGroup( map )
        assert result.success == false
        assert result.message == 'Save failed. The Folder with Id -1 does not exist.'
    }


    @Test
    void updateActionItemGroupAssignment() {
        ActionItemGroupAssign actionItemGroupAssign = ActionItemGroupAssign.findByGroupId( ActionItemGroup.findByName( 'Enrollment' ).id )
        def list = ActionItemGroupAssign.findAllByGroupId( ActionItemGroup.findByName( 'Enrollment' ).id )
        def initialSize = list.size()
        def folderId = CommunicationFolder.findByName( 'Student' ).id
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.status = 'D'
        ai.name = 'Test Action Item. unique 98d7efh'
        ai.title = 'Test Action Item. unique 98d7efh'
        ai.description = 'this is some action item'
        ai.postedIndicator = 'N'
        ActionItem createdItem = actionItemService.create( ai )
        Map map = [groupId: actionItemGroupAssign.groupId, assignment: [[actionItemId: createdItem.id, seq: 10]]]
        actionItemGroupCompositeService.updateActionItemGroupAssignment( map )
        list = ActionItemGroupAssign.findAllByGroupId( ActionItemGroup.findByName( 'Enrollment' ).id )
        assert list.size() == 1
    }

}
