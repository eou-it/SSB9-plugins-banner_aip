/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import groovy.sql.Sql
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
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
        def folderId = CommunicationFolder.findByName( 'AIPstudent' ).id
        new ActionItem( name: 'A-INT-TEST1', title: 'A-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'B-INT-TEST1', title: 'B-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'C-INT-TEST1', title: 'C-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'D-INT-TEST1', title: 'D-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'E-INT-TEST1', title: 'E-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'F-INT-TEST1', title: 'F-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'G-INT-TEST1', title: 'G-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "%-INT-TEST1"],
                [sortColumn: "actionItemName", sortAscending: true, max: 7, offset: 0] )

        assertEquals( 7, results.size() )
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
        def folderId = CommunicationFolder.findByName( 'AIPstudent' ).id
        new ActionItem( name: 'A-INT-TEST1', title: 'A-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'B-INT-TEST1', title: 'B-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'C-INT-TEST1', title: 'C-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'D-INT-TEST1', title: 'D-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'E-INT-TEST1', title: 'E-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'F-INT-TEST1', title: 'F-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'G-INT-TEST1', title: 'G-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "%-INT-TEST1"],
                [sortColumn: "actionItemName", sortAscending: false, max: 7, offset: 0] )

        assertEquals( 7, results.size() )
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
        def folderId = CommunicationFolder.findByName( 'AIPstudent' ).id
        new ActionItem( name: 'A-INT-TEST1', title: 'A-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'B-INT-TEST1', title: 'B-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'C-INT-TEST1', title: 'C-INT-TEST1', folderId: folderId, status: 'A', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'D-INT-TEST1', title: 'D-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'E-INT-TEST1', title: 'E-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'F-INT-TEST1', title: 'F-INT-TEST1', folderId: folderId, status: 'A', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'G-INT-TEST1', title: 'G-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: "%-INT-TEST1"],
                [sortColumn: "actionItemStatus", sortAscending: true, max: 50, offset: 0] )

        def foundActive = false
        def foundDraft = false
        def foundInactive = false
        def activeAsFound = []
        def draftAsFound = []
        def inactiveAsFound = []
        results.each {ActionItemReadOnly it ->
            if (it.actionItemStatus == 'A') {
                foundActive = true
                activeAsFound.add( it.actionItemName )
            }
            if (it.actionItemStatus == 'I') {
                foundInactive = true
                inactiveAsFound.add( it.actionItemName )
            }
            if (it.actionItemStatus == 'D') {
                foundDraft = true
                draftAsFound.add( it.actionItemName )
            }
        }
        (0..4).each {it ->
            assertEquals( activeAsFound[it], activeAsFound.sort( false )[it] )
            assertEquals( draftAsFound[it], draftAsFound.sort( false )[it] )
            assertEquals( inactiveAsFound[it], inactiveAsFound.sort( false )[it] )
        }
    }


    @Test
    void testActionItemSortSecondaryDesc() {

        def folderId = CommunicationFolder.findByName( 'AIPstudent' ).id
        new ActionItem( name: 'A-INT-TEST1', title: 'A-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'B-INT-TEST1', title: 'B-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'C-INT-TEST1', title: 'C-INT-TEST1', folderId: folderId, status: 'A', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'D-INT-TEST1', title: 'D-INT-TEST1', folderId: folderId, status: 'D', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'E-INT-TEST1', title: 'E-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'F-INT-TEST1', title: 'F-INT-TEST1', folderId: folderId, status: 'A', postedIndicator: 'N' ).save( flush: true )
        new ActionItem( name: 'G-INT-TEST1', title: 'G-INT-TEST1', folderId: folderId, status: 'I', postedIndicator: 'N' ).save( flush: true )

        def results = ActionItemReadOnly.fetchWithPagingAndSortParams(
                [params: [name: "%-INT-TEST1"]],
                [sortColumn: "actionItemStatus", sortAscending: false, max: 50, offset: 0] )

        def foundActive = false
        def foundDraft = false
        def foundInactive = false
        def activeAsFound = []
        def draftAsFound = []
        def inactiveAsFound = []
        results.each {ActionItemReadOnly it ->
            if (it.actionItemStatus == 'D') {
                foundDraft = true
                draftAsFound.add( it.actionItemName )
            }
            if (it.actionItemStatus == 'I') {
                foundInactive = true
                inactiveAsFound.add( it.actionItemName )
            }
            if (it.actionItemStatus == 'A') {
                foundActive = true
                activeAsFound.add( it.actionItemName )
            }

        }
        (0..4).each {it ->
            assertEquals( draftAsFound[it], draftAsFound.sort( false )[it] )
            assertEquals( inactiveAsFound[it], inactiveAsFound.sort( false )[it] )
            assertEquals( activeAsFound[it], activeAsFound.sort( false )[it] )
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

            def updateSql = """update gcbactm set gcbactm_activity_date = (SYSDATE - ?), gcbactm_user_id = 'jack' where gcbactm_name = ?"""
            sql = new Sql( sessionFactory.getCurrentSession().connection() )
            sql.executeUpdate( updateSql, [daysBack, actionItemId] )

    }

//TODO : Sivaram reloook
    private void setBackActionItemDetailActivityDate( def daysBack, def actionItemId ) {
        def sql

            def updateSql = """update gcracnt set GCRACNT_ACTIVITY_DATE = (SYSDATE - ?), gcracnt_user_id = 'jill' where gcracnt_gcbactm_id = ?"""
            sql = new Sql( sessionFactory.getCurrentSession().connection() )
            sql.executeUpdate( updateSql, [daysBack, actionItemId] )

    }
}
