/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.sql.SQLException

class ActionItemStatusCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemStatusCompositeService
    def springSecurityService
    def actionItemService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testFetchActionItemStatusPageSortService() {
        actionItemStatusCompositeService.statusSave([title: 'FStatus_A_INT_TEST'])
        actionItemStatusCompositeService.statusSave([title: 'FStatus_B_INT_TEST'])
        actionItemStatusCompositeService.statusSave([title: 'FStatus_C_INT_TEST'])
        actionItemStatusCompositeService.statusSave([title: 'FStatus_D_INT_TEST'])
        actionItemStatusCompositeService.statusSave([title: 'FStatus_E_INT_TEST'])
        actionItemStatusCompositeService.statusSave([title: 'FStatus_F_INT_TEST'])
        Map params1 = [filterName: "FStatus_%_INT_TEST", sortColumn: "actionItemStatus", sortAscending: true, max: 6, offset: 0]
        def actionItemStatusList1 = actionItemStatusCompositeService.listActionItemsPageSort(params1)
        def totalCount = actionItemStatusList1.result.size()
        def actualLength = actionItemStatusList1.length
        assertEquals(actualLength, totalCount)
    }


    @Test
    void testFetchActionItemCheckIfStatusRulePresent() {
        loginSSB('CSRSTU001', '111111')
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave([title: title]).status
        assert actionItemStatus.actionItemStatus == title
        def actionItem = actionItemService.create(new ActionItem(title: 'test integration',
                name: 'test integration',
                status: 'D',
                postedIndicator: 'N',
                folderId: CommunicationFolder.findByName('Student').id)
        )
        def actionItemId = actionItem.id
        def statusId = actionItemStatus.id
        sessionFactory.currentSession.createSQLQuery("INSERT INTO GCRAISR (GCRAISR_GCBACTM_ID, GCRAISR_SEQ_ORDER, GCRAISR_LABEL_TEXT, GCRAISR_GCVASTS_ID, GCRAISR_RESUBMIT_IND, GCRAISR_ACTIVITY_DATE, GCRAISR_USER_ID" +
                ") VALUES ($actionItemId, 1, 'TEST', $statusId, 'N', sysdate, 'BANNER' )").executeUpdate()
        Map params1 = [filterName: "TEST_TITLE", sortColumn: "actionItemStatus", sortAscending: true, max: 10, offset: 0]
        def actionItemStatusList1 = actionItemStatusCompositeService.listActionItemsPageSort(params1)
        assert actionItemStatusList1.result[0].deleteRestrictionReason == 'Delete not permitted. The Status Rule is associated to Action Item Content.'
    }


    @Test
    void testFetchActionItemCheckIfStatusRulePresent1() {
        loginSSB('CSRSTU001', '111111')
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave([title: title]).status
        assert actionItemStatus.actionItemStatus == title
        def actionItem = actionItemService.create(new ActionItem(title: 'test integration',
                name: 'test integration',
                status: 'D',
                postedIndicator: 'N',
                folderId: CommunicationFolder.findByName('Student').id)
        )
        def actionItemId = actionItem.id
        def statusId = actionItemStatus.id
        sessionFactory.currentSession.createSQLQuery("INSERT INTO GCRAISR (GCRAISR_GCBACTM_ID, GCRAISR_SEQ_ORDER, GCRAISR_LABEL_TEXT, GCRAISR_GCVASTS_ID, GCRAISR_RESUBMIT_IND, GCRAISR_ACTIVITY_DATE, GCRAISR_USER_ID) VALUES ($actionItemId, 1, 'TEST', $statusId, 'N', sysdate, 'BANNER' )").executeUpdate()
        def pidm = springSecurityService.getAuthentication()?.user.pidm
        def groupId = ActionItemGroup.findByName('Enrollment').id
        sessionFactory.currentSession.createSQLQuery("INSERT INTO GCRAACT (GCRAACT_GCBACTM_ID, GCRAACT_GCVASTS_ID, GCRAACT_PIDM, GCRAACT_GCBAGRP_ID, GCRAACT_DISPLAY_START_DATE, GCRAACT_DISPLAY_END_DATE, GCRAACT_ACTIVITY_DATE, GCRAACT_USER_ID) VALUES ($actionItemId, $statusId, $pidm, $groupId, sysdate, sysdate, sysdate, 'BANNER' )").executeUpdate()
        Map params1 = [filterName: "TEST_TITLE", sortColumn: "actionItemStatus", sortAscending: true, max: 10, offset: 0]
        def actionItemStatusList1 = actionItemStatusCompositeService.listActionItemsPageSort(params1)
        assert actionItemStatusList1.result[0].deleteRestrictionReason == 'Delete not permitted. The Status Rule is associated to Action Item Content and to assigned Action Items.'
    }


    @Test
    void testStatusSave() {
        loginSSB('CSRSTU001', '111111')
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave([title: title]).status
        assertEquals actionItemStatus.actionItemStatus, title
        assertEquals actionItemStatus.actionItemStatusActive, 'Y'
        assertEquals actionItemStatus.actionItemStatusBlockedProcess, 'N'
        assertEquals actionItemStatus.actionItemStatusSystemRequired, 'N'
        assertTrue(ActionItemStatus.fetchActionItemStatuses().any { it == actionItemStatus })
    }


    @Test
    void testStatusSaveCheckDuplicate() {
        try {
            loginSSB('CSRSTU001', '111111')
            ActionItemStatus actionItemStatusList1 = actionItemStatusCompositeService.statusSave([title: 'Completed'])
        } catch (ApplicationException e) {
            assertApplicationException(e, 'actionItemStatus.status.unique')
        }
    }


    @Test
    void testStatusSaveInvalidPidm() {
        try {
            loginSSB('INVALID', '111111')
            ActionItemStatus actionItemStatusList1 = actionItemStatusCompositeService.statusSave([title: 'Completed'])
        } catch (ApplicationException e) {
            assertApplicationException(e, 'user.id.not.valid')
        }
    }


    @Test
    void testRemoveStatusInvalidId() {
        try {
            ActionItemStatus actionItemStatusList1 = actionItemStatusCompositeService.removeStatus(-99)
        } catch (ApplicationException e) {
            assertApplicationException(e, 'action.item.status.not.in.system')
        }
    }


    @Test
    void testRemoveStatus() {
        loginSSB('CSRSTU001', '111111')
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave([title: title]).status
        assertTrue actionItemStatusCompositeService.removeStatus(actionItemStatus.id).success
    }


    @Test
    void testRemoveStatusActionItemStatusSystemRequired() {
        Map params1 = [filterName: "Completed", sortColumn: "actionItemStatus", sortAscending: true, max: 1, offset: 0]
        def actionItemStatus = actionItemStatusCompositeService.listActionItemsPageSort(params1).result[0]
        def res = actionItemStatusCompositeService.removeStatus(actionItemStatus.id)
        assert res.message == 'The record is system required and cannot be deleted.'
    }


    @Test
    void updateActionItemStatusRule() {
        Map rules = [
                status             : ActionItemStatus.findByActionItemStatus('Completed'),
                statusRuleLabelText: "sas",
                statusRuleSeqOrder : 0,
                reviewReqInd       : false
        ]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName('Personal Information').id]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        assertTrue data.success
        assert data.message == null
        assert data.rules.size() > 0
    }

    @Test
    void updateActionItemStatusRuleWithStatusID() {
        def status = [actionItemStatus: "Completed", actionItemStatusId: 5]
        Map rules = [
                status             : status,
                statusRuleLabelText: "sas",
                statusRuleSeqOrder : 0,
                reviewReqInd       : false,
                allowedAttachments : 5
        ]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName('Personal Information').id]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def data
        try {
            data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        } catch (Exception e) {
            e.printStackTrace()
        }
        assertTrue data.success
        assert data.message == null
        assert data.rules.size() > 0
    }

    @Test
    void updateActionItemStatusRuleWithoutStatus() {
        def status = [actionItemStatus: "Completed"]
        Map rules = [
                status             : status,
                statusRuleLabelText: "sas",
                statusRuleSeqOrder : 0,
                reviewReqInd       : false,
                allowedAttachments :  2
        ]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName('Personal Information').id]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def data
        try {
            data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        } catch (ApplicationException e) {
            assertApplicationException(e, 'actionItemStatusRule.statusId.nullable.error')
        }
        assertFalse data.success
    }

    @Test
    void updateActionItemStatusRuleWithoutReviewRequired() {
        Map rules = [
                status             : ActionItemStatus.findByActionItemStatus('Completed'),
                statusRuleLabelText: "sas",
                statusRuleSeqOrder : 0
        ]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName('Personal Information').id]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def data
        try {
            data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        } catch (SQLException e) {
            println("Error Messsage: " + e.getMessage())
            assertNotNull e.getMessage()
            assertFalse data.success
        }
    }

    @Test
    void createActionItemStatusRuleWithAttachments() {
        Map rules = [
                status             : ActionItemStatus.findByActionItemStatus('Completed'),
                statusRuleLabelText: "test Attachments",
                statusRuleSeqOrder : 0,
                reviewReqInd       : true,
                allowedAttachments : 3
        ]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName('Personal Information').id]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        assertTrue data.success
        assert data.message == null
        assert data.rules.size() > 0
        def rule = data.rules.find { it.labelText == "test Attachments" }
        assert rule.reviewReqInd == true
        assert rule.allowedAttachments == 3
    }

    @Test
    void createActionItemStatusRuleWithReviewEnabled() {
        Map rules = [
                status             : ActionItemStatus.findByActionItemStatus('Completed'),
                statusRuleLabelText: "test review",
                statusRuleSeqOrder : 0,
                reviewReqInd       : true,
                allowedAttachments : 3
        ]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName('Personal Information').id]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        assertTrue data.success
        assert data.message == null
        assert data.rules.size() > 0
        def rule = data.rules.find { it.labelText == "test review" }
        assert rule.reviewReqInd == true
        assert rule.allowedAttachments == 3
    }

    @Test
    void updateActionItemStatusRuleWithReviewEnabled() {
        Map rules = [
                status             : ActionItemStatus.findByActionItemStatus('Completed'),
                statusRuleLabelText: "test review",
                statusRuleSeqOrder : 0,
                reviewReqInd       : true,
                allowedAttachments  : 5
        ]
        def ruleList = [rules]
        def actionItemId = ActionItem.findByName('Personal Information').id
        Map params1 = [rules: ruleList, actionItemId: actionItemId]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'N'
        actionItemService.update(aim)
        def data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        assertTrue data.success
        assert data.message == null
        assert data.rules.size() > 0
        def rule = data.rules.find { it.labelText == "test review" }
        assert rule.reviewReqInd == true
        assert rule.allowedAttachments == 5

        //update reviewReqInd to false
        Map updateRules = [statusRuleId       : rule.id,
                           status             : ActionItemStatus.findByActionItemStatus('Completed'),
                           statusRuleLabelText: "test review",
                           statusRuleSeqOrder : 0,
                           reviewReqInd       : false,
                           allowedAttachments  : 0
        ]
        ruleList = [updateRules]
        params1 = [rules: ruleList, actionItemId: actionItemId]
        data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        assertTrue data.success
        assert data.message == null
        assert data.rules.size() > 0
        rule = data.rules.find { it.labelText == "test review" }
        assert rule.reviewReqInd == false
        assert rule.allowedAttachments == 0
    }

    @Test
    void updateActionItemStatusRuleWhenPosted() {
        Map rules = [
                status             : ActionItemStatus.findByActionItemStatus('Completed'),
                statusRuleLabelText: "test review",
                statusRuleSeqOrder : 0,
                reviewReqInd       : true,
                allowedAttachments : 1
        ]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName('Personal Information').id]
        ActionItem aim = ActionItem.findByName('Personal Information')
        aim.postedIndicator = 'Y'
        actionItemService.update(aim)
        def data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        assertFalse data.success
        assert data.errors == "Cannot be updated. Action Item is posted."
    }

    @Test
    void updateActionItemStatusRuleWhenAINotExists() {
        Map rules = [
                status             : ActionItemStatus.findByActionItemStatus('Completed'),
                statusRuleLabelText: "test review",
                statusRuleSeqOrder : 0,
                reviewReqInd       : true,
                allowedAttachments :  2
        ]
        def ruleList = [rules]
        Long id = 100
        Map params1 = [rules: ruleList, actionItemId: id]
        def data = actionItemStatusCompositeService.updateActionItemStatusRule(params1)
        assertFalse data.success
        assert data.errors == "Action Item not present"
    }
}
