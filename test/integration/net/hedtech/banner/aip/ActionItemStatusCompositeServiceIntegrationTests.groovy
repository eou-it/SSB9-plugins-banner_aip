/*********************************************************************************
 Copyright 207 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

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
        Map params1 = [filterName: "%", sortColumn: "actionItemStatus", sortAscending: true, max: 10, offset: 0]
        def actionItemStatusList1 = actionItemStatusCompositeService.listActionItemsPageSort( params1 )
        def totalCount = actionItemStatusList1.result.size()
        def actualLength = actionItemStatusList1.length
        assertEquals( actualLength, totalCount )
    }


    @Test
    void testFetchActionItemCheckIfStatusRulePresent() {
        loginSSB( 'CSRSTU001', '111111' )
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave( title ).status
        assert actionItemStatus.actionItemStatus == title
        def actionItem = actionItemService.create( new ActionItem( title: 'test integration',
                                                                   name: 'test integration',
                                                                   status: 'D',
                                                                   postedIndicator: 'N',
                                                                   folderId: CommunicationFolder.findByName( 'Student' ).id )
        )
        def actionItemId = actionItem.id
        def statusId = actionItemStatus.id
        sessionFactory.currentSession.createSQLQuery( "INSERT INTO GCRAISR (GCRAISR_GCBACTM_ID, GCRAISR_SEQ_ORDER, GCRAISR_LABEL_TEXT, GCRAISR_GCVASTS_ID, GCRAISR_RESUBMIT_IND, GCRAISR_ACTIVITY_DATE, GCRAISR_USER_ID" +
                                                              ") VALUES ($actionItemId, 1, 'TEST', $statusId, 'N', sysdate, 'BANNER' )" ).executeUpdate()
        Map params1 = [filterName: "TEST_TITLE", sortColumn: "actionItemStatus", sortAscending: true, max: 10, offset: 0]
        def actionItemStatusList1 = actionItemStatusCompositeService.listActionItemsPageSort( params1 )
        assert actionItemStatusList1.result[0].deleteRestrictionReason == 'Delete not permitted. The Status Rule is associated to Action Item Content.'
    }


    @Test
    void testFetchActionItemCheckIfStatusRulePresent1() {
        loginSSB( 'CSRSTU001', '111111' )
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave( title ).status
        assert actionItemStatus.actionItemStatus == title
        def actionItem = actionItemService.create( new ActionItem( title: 'test integration',
                                                                   name: 'test integration',
                                                                   status: 'D',
                                                                   postedIndicator: 'N',
                                                                   folderId: CommunicationFolder.findByName( 'Student' ).id )
        )
        def actionItemId = actionItem.id
        def statusId = actionItemStatus.id
        sessionFactory.currentSession.createSQLQuery( "INSERT INTO GCRAISR (GCRAISR_GCBACTM_ID, GCRAISR_SEQ_ORDER, GCRAISR_LABEL_TEXT, GCRAISR_GCVASTS_ID, GCRAISR_RESUBMIT_IND, GCRAISR_ACTIVITY_DATE, GCRAISR_USER_ID) VALUES ($actionItemId, 1, 'TEST', $statusId, 'N', sysdate, 'BANNER' )" ).executeUpdate()
        def pidm = springSecurityService.getAuthentication()?.user.pidm
        def groupId = ActionItemGroup.findByName( 'Enrollment' ).id
        sessionFactory.currentSession.createSQLQuery( "INSERT INTO GCRAACT (GCRAACT_GCBACTM_ID, GCRAACT_GCVASTS_ID, GCRAACT_PIDM, GCRAACT_GCBAGRP_ID, GCRAACT_DISPLAY_START_DATE, GCRAACT_DISPLAY_END_DATE, GCRAACT_ACTIVITY_DATE, GCRAACT_USER_ID) VALUES ($actionItemId, $statusId, $pidm, $groupId, sysdate, sysdate, sysdate, 'BANNER' )" ).executeUpdate()
        Map params1 = [filterName: "TEST_TITLE", sortColumn: "actionItemStatus", sortAscending: true, max: 10, offset: 0]
        def actionItemStatusList1 = actionItemStatusCompositeService.listActionItemsPageSort( params1 )
        assert actionItemStatusList1.result[0].deleteRestrictionReason == 'Delete not permitted. The Status Rule is associated to Action Item Content and to assigned Action Items.'
    }


    @Test
    void testStatusSave() {
        loginSSB( 'CSRSTU001', '111111' )
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave( title ).status
        assertEquals actionItemStatus.actionItemStatus, title
        assertEquals actionItemStatus.actionItemStatusActive, 'Y'
        assertEquals actionItemStatus.actionItemStatusBlockedProcess, 'N'
        assertEquals actionItemStatus.actionItemStatusSystemRequired, 'N'
        assertTrue( ActionItemStatus.fetchActionItemStatuses().any {it == actionItemStatus} )
    }


    @Test
    void testStatusSaveCheckDuplicate() {
        try {
            loginSSB( 'CSRSTU001', '111111' )
            ActionItemStatus actionItemStatusList1 = actionItemStatusCompositeService.statusSave( 'Completed' )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'actionItemStatus.status.unique' )
        }
    }


    @Test
    void testStatusSaveInvalidPidm() {
        try {
            loginSSB( 'INVALID', '111111' )
            ActionItemStatus actionItemStatusList1 = actionItemStatusCompositeService.statusSave( 'Completed' )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'user.id.not.valid' )
        }
    }


    @Test
    void testRemoveStatusInvalidId() {
        try {
            ActionItemStatus actionItemStatusList1 = actionItemStatusCompositeService.removeStatus( -99 )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'action.item.status.not.in.system' )
        }
    }


    @Test
    void testRemoveStatus() {
        loginSSB( 'CSRSTU001', '111111' )
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave( title ).status
        assertTrue actionItemStatusCompositeService.removeStatus( actionItemStatus.id ).success
    }


    @Test
    void testRemoveStatusActionItemStatusSystemRequired() {
        Map params1 = [filterName: "Completed", sortColumn: "actionItemStatus", sortAscending: true, max: 1, offset: 0]
        def actionItemStatus = actionItemStatusCompositeService.listActionItemsPageSort( params1 ).result[0]
        def res = actionItemStatusCompositeService.removeStatus( actionItemStatus.id )
        assert res.message == 'The record is system required and cannot be deleted.'
    }


    @Test
    void updateActionItemStatusRule() {
        Map rules = [statusName         : "",
                     status             : ActionItemStatus.findByActionItemStatus( 'Completed' ),
                     statusRuleLabelText: "sas",
                     statusRuleSeqOrder : 0]
        def ruleList = [rules]
        Map params1 = [rules: ruleList, actionItemId: ActionItem.findByName( 'Personal Information' ).id]
        def data = actionItemStatusCompositeService.updateActionItemStatusRule( params1 )
        assertTrue data.success
        assert data.message == null
        assert data.rules.size() > 0
    }
}
