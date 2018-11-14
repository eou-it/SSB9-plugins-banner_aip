/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import static org.junit.Assert.*


class MonitorActionItemCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def monitorActionItemCompositeService
    def userActionItemReadOnlyCompositeService
    def configUserPreferenceService

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
    void listActionItemNames() {
        def result = monitorActionItemCompositeService.getactionItemNames()
        assert result.size() > 0
    }

    //ActionID + personID test case
    @Test
    void fetchByActionItemIDAndPersonIdExisting() {
        Long actionId = 3L
        String personName = null
        String personId = "CSRSTU001"

        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionId, personName, personId)
        assert result.size() > 0
        assertEquals 1, result.size()
        assertEquals 3, result[0].actionItemId
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "Cliff Starr", result[0].actionItemPersonName
        assertEquals "CSRSTU001", result[0].spriden_id

    }

    @Test
    void fetchByActionItemIDAndPersonIdNonExistingPerson() {
        Long actionId = 3L
        String personName = null
        String personId = "CSRSTABCD"

        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionId, personName, personId)
        assertEquals 0, result.size()


    }

    @Test
    void fetchByActionItemIDAndPersonIdNonExistingActionID() {
        Long actionId = 9999L
        String personName = null
        String personId = "CSRSTU001"
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionId, personName, personId)
        assertEquals 0, result.size()
    }

    //Action ID + Person Name Combination
    @Test
    void testFetchActionItemsByNameExisting() {
        Long actionItemId = 3L
        String personName = "Cliff Starr"
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertNotNull result
        assertEquals 1, result.size()
    }

    @Test
    void testFetchActionItemsByNameNonExistingName() {
        Long actionItemId = 3L
        String personName = "Osama Bin Ladden"
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNameNonExistingID() {
        Long actionItemId = 999L
        String personName = "Cliff Starr"
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNameExistingPartialName() {
        Long actionItemId = 3L
        String personName = "Cliff"
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertNotNull result
        assertEquals 1, result.size()
    }

    //Action Item only


    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = 3L
        String personName = null
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assert result.size() > 0
        assertEquals 13, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 399L
        String personName = null
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertEquals 0, result.size()
    }

    //person Id only
    @Test
    void testFetchByPersonIdExisting() {
        Long actionItemId = null
        String personName = null
        String personId = 'CSRSTU001'
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assert result.size() > 0
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonIdNonExisting() {
        Long actionItemId = null
        String personName = null
        String personId = "CSRQWERTY"
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertEquals 0, result.size()

    }

    //person name only

    @Test
    void testFetchByPersonNameExact() {
        Long actionItemId = null
        String personName = "Cliff Starr"
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertNotNull result
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonNamePartial() {
        Long actionItemId = null
        String personName = "Cliff"
        String personId = null
        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertNotNull result
        assertEquals 6, result.size()

    }

    @Test
    void testFetchByPersonNameNonExisting() {
        String personName = "Osama Bin Ladden"
        Long actionItemId = null
        String personId = null

        def result = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId)
        assertNotNull result
        assertEquals 0, result.size()

    }

    @Test
    void testReviewStateNameInSearchResult() {
        loginSSB( 'CSRSTU004', '111111' )
        def map = [locale:'en-US']
        def statusMap = configUserPreferenceService.saveLocale(map)
        assert statusMap.status == 'success'
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        def group = result.groups.find { it.title == 'Enrollment' }
        def item = group.items.find { it.name == 'Policy Handbook' }
        Long actionItemId = item.id
        assertNotNull actionItemId
        String personId = 'CSRSTU004'
        def filterParam = 'id'
        def paginationParams =[
                max:10,
                offset:0,
                sortAscending:true,
                sortColumn:'id'
        ]
        def results = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, null , personId, filterParam, paginationParams)
        assert results.size() > 0
        assert results[0].reviewState == "Review needed"
        assertEquals 13, result.size()
    }


}
