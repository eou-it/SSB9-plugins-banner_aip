/*********************************************************************************
 Copyright 207 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemStatusCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemStatusCompositeService


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
    void testStatusSave() {
        loginSSB( 'CSRSTU001', '111111' )
        def title = 'TEST_TITLE'
        ActionItemStatus actionItemStatus = actionItemStatusCompositeService.statusSave( title ).status
        assertEquals actionItemStatus.actionItemStatus, title
        assertEquals actionItemStatus.actionItemStatusActive, 'Y'
        assertEquals actionItemStatus.actionItemStatusBlockedProcess, 'N'
        assertEquals actionItemStatus.actionItemStatusSystemRequired, 'N'
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
        try {
            actionItemStatusCompositeService.removeStatus( actionItemStatus.id )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'action.item.status.cannot.be.deleted' )
        }
    }
}
