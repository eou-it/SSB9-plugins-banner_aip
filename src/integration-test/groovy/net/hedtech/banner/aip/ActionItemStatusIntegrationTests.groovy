/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemStatusIntegrationTests extends BaseIntegrationTestCase {


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
    void testCount() {
        def myCount = ActionItemStatus.fetchActionItemStatusCount('%')
        assert myCount > 0
    }


    @Test
    void testFetchActionItemStatusString() {
        List<ActionItemStatus> actionItemStatuses = ActionItemStatus.fetchActionItemStatuses()
        def actionItemStatusList = actionItemStatuses[0]
        assertNotNull( actionItemStatusList.toString() )
        assertFalse actionItemStatuses.isEmpty()
    }


    @Test
    void testFetchActionStatusItems() {
        List<ActionItemStatus> actionItemStatuses = ActionItemStatus.fetchActionItemStatuses()
        assertFalse actionItemStatuses.isEmpty()
    }

    // sort by other than name is secondarily sorted by name
    @Test
    void testActionItemStatusSortSecondaryAsc() {
        new ActionItemStatus(
                actionItemStatus: "A_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        new ActionItemStatus(
                actionItemStatus: "B_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        new ActionItemStatus(
                actionItemStatus: "C_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        new ActionItemStatus(
                actionItemStatus: "D_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: "%_INT_TEST"]],
                [sortColumn: "actionItemStatusBlockedProcess", sortAscending: true, max: 50, offset: 0] )

        def foundBlocked = false
        def foundNotBlocked = false
        def blockedAsFound = []
        def notBlockedAsFound = []
        results.each {it ->
            if (it.actionItemStatusBlockedProcess == 'N') {
                assertFalse foundBlocked
                foundNotBlocked = true
                notBlockedAsFound.add( it.actionItemStatus )
            }
            if (it.actionItemStatusBlockedProcess == 'Y') {
                assertTrue foundNotBlocked
                foundBlocked = true
                blockedAsFound.add( it.actionItemStatus )
            }
        }
        assertEquals( blockedAsFound[0], blockedAsFound.sort( false )[0] )
        assertEquals( blockedAsFound[1], blockedAsFound.sort( false )[1] )
        assertEquals( notBlockedAsFound[0], notBlockedAsFound.sort( false )[0] )
    }


    @Test
    void testActionItemStatusSortSecondaryDesc() {
        new ActionItemStatus(
                actionItemStatus: "A_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        new ActionItemStatus(
                actionItemStatus: "B_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        new ActionItemStatus(
                actionItemStatus: "C_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        new ActionItemStatus(
                actionItemStatus: "D_INT_TEST", actionItemStatusActive: 'Y', actionItemStatusBlockedProcess: 'N',
                actionItemStatusDefault: 'N',
                actionItemStatusSystemRequired: 'N'
        ).save( flash: true )
        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: "%_INT_TEST"]],
                [sortColumn: "actionItemStatusBlockedProcess", sortAscending: false, max: 50, offset: 0] )

        boolean foundNotBlocked = false
        def notBlockedAsFound = []
        results.each {it ->

            if (it.actionItemStatusBlockedProcess == 'N') {
                foundNotBlocked = true
                notBlockedAsFound.add( it.actionItemStatus )
            }
        }
        assert foundNotBlocked == true
        assertEquals( notBlockedAsFound[0], notBlockedAsFound.sort( false )[0] )
    }


    @Test
    void testActionStatusItemsHashCode() {

        List<ActionItemStatus> actionItemStatuses = ActionItemStatus.fetchActionItemStatuses()
        def actionItemStatusList = actionItemStatuses[0]

        def result = actionItemStatusList.hashCode()
        assertNotNull result

        def actionItemStatusListObj = new ActionItemStatus()
        assertNotNull actionItemStatusListObj
    }


    @Test
    void testNullStatusError() {
        List<ActionItemStatus> actionItemStatuses = ActionItemStatus.fetchActionItemStatuses()
        def actionItemStatusNew = new ActionItemStatus()

        actionItemStatusNew.actionItemStatus = null
        actionItemStatusNew.actionItemStatusActive = "Y"
        actionItemStatusNew.actionItemStatusSystemRequired = "Y"
        actionItemStatusNew.actionItemStatusActive = "Y"

        assertFalse actionItemStatusNew.validate()
        assertTrue( actionItemStatusNew.errors.allErrors.codes[0].contains( 'actionItemStatus.actionItemStatus.nullable.error' ) )
    }


    @Test
    void testEmptyBlockedIndError() {
        List<ActionItemStatus> actionItemStatuses = ActionItemStatus.fetchActionItemStatuses()
        def actionItemStatusNew = new ActionItemStatus()

        actionItemStatusNew.actionItemStatus = "New Status"
        actionItemStatusNew.actionItemStatusBlockedProcess = null
        actionItemStatusNew.actionItemStatusActive = "Y"
        actionItemStatusNew.actionItemStatusSystemRequired = "Y"
        actionItemStatusNew.actionItemStatusActive = "Y"

        assertFalse actionItemStatusNew.validate()
        assertTrue( actionItemStatusNew.errors.allErrors.codes[0].contains( 'actionItemStatus.actionItemStatusBlockedProcess.nullable.error' ) )
    }

}
