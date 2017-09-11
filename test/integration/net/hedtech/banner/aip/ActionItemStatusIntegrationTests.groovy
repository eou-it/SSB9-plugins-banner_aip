/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemStatusIntegrationTests extends BaseIntegrationTestCase {


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
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
        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "actionItemStatusBlockedProcess", sortAscending: true, max: 50, offset: 0] )

        def foundBlocked = false
        def foundNotBlocked = false
        def blockedAsFound = []
        def notBlockedAsFound = []
        results.each { it ->
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
        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: "%"]],
                [sortColumn: "actionItemStatusBlockedProcess", sortAscending: false, max: 50, offset: 0] )

        def foundBlocked = false
        def foundNotBlocked = false
        def blockedAsFound = []
        def notBlockedAsFound = []
        results.each { it ->
            if (it.actionItemStatusBlockedProcess == 'Y') {
                assertFalse foundNotBlocked
                foundBlocked = true
                blockedAsFound.add( it.actionItemStatus )
            }
            if (it.actionItemStatusBlockedProcess == 'N') {
                assertTrue foundBlocked
                foundNotBlocked = true
                notBlockedAsFound.add( it.actionItemStatus )
            }
        }
        assertEquals( blockedAsFound[0], blockedAsFound.sort( false )[0] )
        assertEquals( blockedAsFound[1], blockedAsFound.sort( false )[1] )
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
    void testActionItemStatusEquals() {

        List<ActionItemStatus> actionItemStatuses = ActionItemStatus.fetchActionItemStatuses()
        def actionItemStatusList = actionItemStatuses[0]
        def actionItemStatusNewList = new ActionItemStatus(
                actionItemStatus: actionItemStatusList.actionItemStatus,
                actionItemStatusActivityDate: actionItemStatusList.actionItemStatusActivityDate,
                actionItemStatusBlockedProcess: actionItemStatusList.actionItemStatusBlockedProcess,
                actionItemStatusSystemRequired: actionItemStatusList.actionItemStatusSystemRequired,
                actionItemStatusDataOrigin: actionItemStatusList.actionItemStatusDataOrigin,
                actionItemStatusUserId: actionItemStatusList.actionItemStatusUserId,
                actionItemStatusVersion: actionItemStatusList.actionItemStatusVersion,
                actionItemStatusActive: actionItemStatusList.actionItemStatusActive ,
                actionItemStatusDefault: actionItemStatusList.actionItemStatusDefault)
        long actionItemStatusListId = actionItemStatusList.id
        long actionItemStatusListVersion = actionItemStatusList.actionItemStatusVersion
        actionItemStatusNewList.id = actionItemStatusListId
        actionItemStatusNewList.actionItemStatusVersion = actionItemStatusListVersion

        def result = actionItemStatusList.equals( actionItemStatusList )
        assertTrue result

        result = actionItemStatusList.equals( actionItemStatusNewList )
        assertTrue result


        /* TODO: this test doesn't pass because of null object error.
        result = actionItemStatusList.equals( null )
        assertFalse result
        */


        def actionItemStatusListNull = new ActionItem( null )
        result = actionItemStatusList.equals( actionItemStatusListNull )
        assertFalse result

    }

    @Test
    void testNullStatusError() {
        List<ActionItemStatus> actionItemStatuses = ActionItemStatus.fetchActionItemStatuses()
        def actionItemStatusNew = new ActionItemStatus()

        actionItemStatusNew.actionItemStatus = null
        actionItemStatusNew.actionItemStatusActive = "Y"
        actionItemStatusNew.actionItemStatusSystemRequired = "Y"
        actionItemStatusNew.actionItemStatusActivityDate = new Date()
        actionItemStatusNew.actionItemStatusActive = "Y"
        actionItemStatusNew.actionItemStatusUserId = "GRAILS"

        //
        assertFalse actionItemStatusNew.validate()
        // TODO: verify something
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
        actionItemStatusNew.actionItemStatusActivityDate = new Date()
        actionItemStatusNew.actionItemStatusActive = "Y"
        actionItemStatusNew.actionItemStatusUserId = "GRAILS"

        //
        assertFalse actionItemStatusNew.validate()
        // TODO: verify something
        assertTrue( actionItemStatusNew.errors.allErrors.codes[0].contains( 'actionItemStatus.actionItemStatusBlockedProcess.nullable.error' ) )
    }

}
