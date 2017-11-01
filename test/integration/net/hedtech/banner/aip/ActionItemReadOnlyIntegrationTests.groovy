/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import groovy.sql.Sql
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void testActionItemSortNameAsc() {
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "%"],
                [sortColumn: "actionItemName", sortAscending: true, max: 20, offset: 0] )

        assertEquals( 20, results.size() )
        def asFound = []
        results.each {it ->
            asFound.add( it.actionItemName )
        }
        (0..4).each {it ->
            assertEquals( asFound[it], asFound.sort( false )[it] )
        }
    }


    @Test
    void testActionItemSortNameDesc() {
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "%"],
                [sortColumn: "actionItemName", sortAscending: false, max: 20, offset: 0] )

        assertEquals( 20, results.size() )
        def asFound = []
        results.each {it ->
            asFound.add( it.actionItemName )
        }
        (0..4).each {it ->
            assertEquals( asFound[it], asFound.sort( false ).reverse( false )[it] )
        }
    }

    // sort by other than name is secondarily sorted by name
    @Test
    void testActionItemSortSecondaryAsc() {
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "%"],
                [sortColumn: "actionItemStatus", sortAscending: true, max: 50, offset: 0] )

        def foundActive = false
        def foundPending = false
        def foundInactive = false
        def activeAsFound = []
        def pendingAsFound = []
        def inactiveAsFound = []
        results.each {it ->
            if (it.actionItemStatus == 'Active') {
                assertFalse foundPending
                assertFalse foundInactive
                foundActive = true
                activeAsFound.add( it.actionItemName )
            }
            if (it.actionItemStatus == 'Inactive') {
                assertTrue foundActive
                assertFalse foundPending
                foundInactive = true
                inactiveAsFound.add( it.actionItemName )
            }
            if (it.actionItemStatus == 'Pending') {
                assertTrue foundActive
                assertTrue foundInactive
                foundPending = true
                pendingAsFound.add( it.actionItemName )
            }
        }
        (0..4).each {it ->
            assertEquals( activeAsFound[it], activeAsFound.sort( false )[it] )
            assertEquals( pendingAsFound[it], pendingAsFound.sort( false )[it] )
            assertEquals( inactiveAsFound[it], inactiveAsFound.sort( false )[it] )
        }
    }


    @Test
    void testActionItemSortSecondaryDesc() {
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "Draft"],
                [sortColumn: "actionItemStatus", sortAscending: false, max: 10, offset: 0] )

        results.each {it ->
            assertTrue 'Draft' == it.actionItemStatus

        }
        results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "Action"],
                [sortColumn: "actionItemStatus", sortAscending: false, max: 10, offset: 0] )

        results.each {it ->
            assertTrue 'Action' == it.actionItemStatus

        }
        results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "Inaction"],
                [sortColumn: "actionItemStatus", sortAscending: false, max: 10, offset: 0] )

        results.each {it ->
            assertTrue 'Inaction' == it.actionItemStatus

        }
    }

    // ActionItem most recent
    @Test
    void testActionItemROCompositeDateGCBACTMGreater() {
        def someRandomItemName = 'Policy Handbook'
        setBackActionItemActivityDate( 1, someRandomItemName )
        List<ActionItem> ais = ActionItem.findAllByTitle( someRandomItemName )
        ais.each {
            setBackActionItemDetailActivityDate( 4, it.id )
        }
        ActionItemReadOnly testTheDates = ActionItemReadOnly.findAllByActionItemName( someRandomItemName )[0]
        assertEquals( testTheDates.actionItemCompositeDate, testTheDates.actionItemActivityDate )
        assertTrue( testTheDates.actionItemCompositeDate > testTheDates.actionItemContentDate )
        assertEquals( testTheDates.actionItemLastUserId, testTheDates.actionItemUserId )
        assertNotEquals( testTheDates.actionItemLastUserId, testTheDates.actionItemContentUserId )
    }

    // Detail most recent
    @Test
    void testActionItemROCompositeDateGCRACTNGreater() {
        def someRandomItemName = 'Policy Handbook'
        setBackActionItemActivityDate( 4, someRandomItemName )
        List<ActionItem> ais = ActionItem.findAllByTitle( someRandomItemName )
        ais.each {
            setBackActionItemDetailActivityDate( 1, it.id )
        }
        ActionItemReadOnly testTheDates = ActionItemReadOnly.findAllByActionItemName( someRandomItemName )[0]
        assertEquals( testTheDates.actionItemCompositeDate, testTheDates.actionItemContentDate )
        assertTrue( testTheDates.actionItemCompositeDate > testTheDates.actionItemActivityDate )
        assertNotEquals( testTheDates.actionItemLastUserId, testTheDates.actionItemUserId )
        assertEquals( testTheDates.actionItemLastUserId, testTheDates.actionItemContentUserId )
    }


    private void setBackActionItemActivityDate( def daysBack, def actionItemId ) {
        def sql
        try {
            def updateSql = """update gcbactm set gcbactm_activity_date = (SYSDATE - ?), gcbactm_user_id = 'jack' where gcbactm_name = ?"""
            sql = new Sql( sessionFactory.getCurrentSession().connection() )
            sql.executeUpdate( updateSql, [daysBack, actionItemId] )
        } finally {
            sql?.close()
        }
    }


    private void setBackActionItemDetailActivityDate( def daysBack, def actionItemId ) {
        def sql
        try {
            def updateSql = """update gcracnt set GCRACNT_ACTIVITY_DATE = (SYSDATE - ?), gcracnt_user_id = 'jill' where gcracnt_gcbactm_id = ?"""
            sql = new Sql( sessionFactory.getCurrentSession().connection() )
            sql.executeUpdate( updateSql, [daysBack, actionItemId] )
        } finally {
            sql?.close()
        }
    }
}
