/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemGroupCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemGroupCompositeService
    def actionItemService
    def actionItemGroupService


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
                group    : [
                        groupTitle : 'groupTitle',
                        groupName  : 'groupName',
                        folderId   : CommunicationFolder.findByName('Student').id,
                        groupDesc  : 'groupDesc',
                        postingInd : 'N',
                        groupStatus: 'Draft'
                ],
                edit     : false,
                duplicate: false
        ]
        def result = actionItemGroupCompositeService.createOrUpdateGroup(map)
        assert result.success == true
        assert result.message == null
        assert result.group.groupStatus == 'Draft'
        assert result.group.groupTitle == 'groupTitle'
    }


    @Test
    void testUpdateGroupSuccess() {
        ActionItemGroup existingGroup = ActionItemGroup.findByName('Enrollment')
        def exitingFolder = existingGroup.folderId
        Map map = [
                group    : [
                        groupId    : existingGroup.id,
                        groupTitle : 'groupTitle',
                        groupDesc  : 'groupDesc',
                        folderId   : CommunicationFolder.findByName('Student').id,
                        postingInd : 'N',
                        groupStatus: 'Draft'
                ],
                edit     : false,
                duplicate: false
        ]
        def result = actionItemGroupCompositeService.createOrUpdateGroup(map)
        assert result.success == true
        assert result.message == null
        assert result.group.groupName == 'Enrollment'
        assert result.group.groupStatus == 'Draft'
        assert result.group.groupTitle == 'groupTitle'
        assert result.group.groupDesc == 'groupDesc'
        assert (result.group.folderId as long) == CommunicationFolder.findByName('Student').id
        assert (exitingFolder as long) != CommunicationFolder.findByName('Student').id
    }


    @Test
    void testCreateGroupFailed() {
        Map map = [
                group    : [
                        groupTitle : 'groupTitle',
                        groupName  : null,
                        folderId   : CommunicationFolder.findByName('Student').id,
                        groupDesc  : 'groupDesc',
                        postingInd : 'N',
                        groupStatus: 'Draft'
                ],
                edit     : false,
                duplicate: false
        ]
        def result = actionItemGroupCompositeService.createOrUpdateGroup(map)
        assert result.success == false
        assert result.message == 'Save failed. The Name can not be null or empty.'
    }


    @Test
    void testCreateGroupFailedDuplicate() {
        Map map = [
                group    : [
                        groupTitle : 'groupTitle',
                        groupName  : 'groupName',
                        folderId   : -1,
                        groupDesc  : 'groupDesc',
                        postingInd : 'N',
                        groupStatus: 'Draft'
                ],
                edit     : false,
                duplicate: false
        ]
        def result = actionItemGroupCompositeService.createOrUpdateGroup(map)
        assert result.success == false
        assert result.message == 'Save failed. The Folder does not exist.'
    }


    @Test
    void updateGroupAssignment() {
        ActionItemGroupAssign actionItemGroupAssign = ActionItemGroupAssign.findByGroupId(ActionItemGroup.findByName('Enrollment').id)
        def list = ActionItemGroupAssign.findAllByGroupId(ActionItemGroup.findByName('Enrollment').id)
        def folderId = CommunicationFolder.findByName('Student').id
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.status = 'D'
        ai.name = 'Test Action Item. unique 98d7efh'
        ai.title = 'Test Action Item. unique 98d7efh'
        ai.description = 'this is some action item'
        ai.postedIndicator = 'N'
        ActionItem createdItem = actionItemService.create(ai)
        Map map = [groupId: actionItemGroupAssign.groupId, assignment: [[actionItemId: createdItem.id, seq: 10]]]
        actionItemGroupCompositeService.updateGroupAssignment(map)
        list = ActionItemGroupAssign.findAllByGroupId(ActionItemGroup.findByName('Enrollment').id)
        assert list.size() == 1
        actionItemGroupAssign = ActionItemGroupAssign.findByGroupId(ActionItemGroup.findByName('Enrollment').id)
        map = [groupId: actionItemGroupAssign.groupId, assignment: [[actionItemId: actionItemGroupAssign.actionItemId, seq: 10]]]
        actionItemGroupCompositeService.updateGroupAssignment(map)
        list = ActionItemGroupAssign.findAllByGroupId(ActionItemGroup.findByName('Enrollment').id)
        assert list.size() == 1
    }


    @Test
    void updateActionItemGroupAssignment() {
        ActionItemGroupAssign actionItemGroupAssign = ActionItemGroupAssign.findByGroupId(ActionItemGroup.findByName('Enrollment').id)
        def list = ActionItemGroupAssign.findAllByGroupId(ActionItemGroup.findByName('Enrollment').id)
        def folderId = CommunicationFolder.findByName('Student').id
        ActionItem ai = new ActionItem()
        ai.folderId = folderId
        ai.status = 'D'
        ai.name = 'Test Action Item. unique 98d7efh'
        ai.title = 'Test Action Item. unique 98d7efh'
        ai.description = 'this is some action item'
        ai.postedIndicator = 'N'
        ActionItem createdItem = actionItemService.create(ai)
        Map map = [groupId: actionItemGroupAssign.groupId, assignment: [[actionItemId: createdItem.id, seq: 10]]]
        def result = actionItemGroupCompositeService.updateActionItemGroupAssignment(map)
        assert result.success == true
    }


    @Test
    void deleteActionItemGroup() {
        Map map = [
                group    : [
                        groupTitle : 'groupTitle',
                        groupName  : 'groupName',
                        folderId   : CommunicationFolder.findByName('Student').id,
                        groupDesc  : 'groupDesc',
                        postingInd : 'N',
                        groupStatus: 'Draft'
                ],
                edit     : false,
                duplicate: false
        ]
        def result = actionItemGroupCompositeService.createOrUpdateGroup(map)
        assert result.success == true
        assert result.message == null
        assert result.group.groupStatus == 'Draft'
        assert result.group.groupTitle == 'groupTitle'
        result = actionItemGroupCompositeService.deleteGroup([groupId: result.group.groupId])
        assert result.message == 'Delete successful.'
        assert result.success == true
    }


    @Test
    void deleteActionItemGroupHaveAssignedItems() {
        def result = actionItemGroupCompositeService.deleteGroup([groupId: ActionItemGroup.findByName('Enrollment').id])
        assert result.message == 'The group is associated with a submitted Post Action Items job and cannot be deleted.'
        assert result.success == false
    }


    @Test
    void deleteActionItemGroupFailesCase() {
        def result = actionItemGroupCompositeService.deleteGroup([groupId: -99])
        assert result.message != null
        assert result.success == false
    }


    @Test
    void deleteActionItemGroupFailedCase() {
        Map map = [
                group    : [
                        groupTitle : 'groupTitle',
                        groupName  : 'groupName',
                        folderId   : CommunicationFolder.findByName('Student').id,
                        groupDesc  : 'groupDesc',
                        postingInd : 'N',
                        groupStatus: 'Draft'
                ],
                edit     : false,
                duplicate: false
        ]
        def result = actionItemGroupCompositeService.createOrUpdateGroup(map)
        assert result.success == true
        assert result.message == null
        assert result.group.groupStatus == 'Draft'
        assert result.group.groupTitle == 'groupTitle'
        ActionItemGroup group = ActionItemGroup.get(result.group.groupId)
        group.postingInd = 'Y'
        actionItemGroupService.update(group)
        result = actionItemGroupCompositeService.deleteGroup([groupId: result.group.groupId])
        assert result.message == 'The group is associated with a submitted Post Action Items job and cannot be deleted.'
        assert result.success == false
    }

}
