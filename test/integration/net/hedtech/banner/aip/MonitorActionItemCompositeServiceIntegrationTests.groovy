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
    def pagingAndSortParams
    def filterData
    Map paramsMap = [:]
    def criteriaMap = [:]

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        pagingAndSortParams = [sortColumn: "actionItemName", sortDirection: "asc", max: 50, offset: 0]
        filterData = [params: paramsMap, criteria: criteriaMap]
    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void listActionItemNames() {
        def result = monitorActionItemCompositeService.getActionItemNames()
        assert result.size() > 0
    }

    //ActionID + personID test case
    @Test
    void fetchByActionItemIDAndPersonIdExisting() {
        Long actionId = 3L
        String personName = null
        String personId = "CSRSTU001"
        def pagingAndSortParams = [sortColumn: "actionItemName", sortDirection: "asc", max: 5, offset: 0]

        Map paramsMap = [:]
        def criteriaMap = [:]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 3, response.result[0].actionItemId
        assertEquals "Drug and Alcohol Policy", response.result[0].actionItemName
        assertEquals "CSRSTU001", response.result[0].spridenId
        assertEquals 1, response.length

    }

    @Test
    void fetchByActionItemIDAndPersonIdNonExistingPerson() {
        Long actionId = 3L
        String personName = null
        String personId = "CSRSTABCD"

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionId, personName, personId, filterData, pagingAndSortParams)
        assertEquals 0, response.result.size()
        assertEquals 0, response.length
    }

    @Test
    void fetchByActionItemIDAndPersonIdNonExistingActionID() {
        Long actionId = 9999L
        String personName = null
        String personId = "CSRSTU001"
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionId, personName, personId, filterData, pagingAndSortParams)
        assertEquals 0, response.result.size()
        assertEquals 0, response.length
    }

    //Action ID + Person Name Combination
    @Test
    void testFetchActionItemsByNameExisting() {
        Long actionItemId = 3L
        String personName = "Cliff Starr"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length

    }

    @Test
    void testFetchActionItemsByNameNonExistingName() {
        Long actionItemId = 3L
        String personName = "Osama Bin Ladden"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 0, response.result.size()
        assertEquals 0, response.length
    }

    @Test
    void testFetchActionItemsByNameNonExistingID() {
        Long actionItemId = 999L
        String personName = "Cliff Starr"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 0, response.result.size()
        assertEquals 0, response.length
    }

    @Test
    void testFetchActionItemsByNameExistingPartialName() {
        Long actionItemId = 3L
        String personName = "Cliff"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length
    }

    //Action Item only


    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = 3L
        String personName = null
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 13, response.result.size()
        assertEquals 13, response.length
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 399L
        String personName = null
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId ,filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 0, response.result.size()
        assertEquals 0, response.length
    }

    //person Id only
    @Test
    void testFetchByPersonIdExisting() {
        Long actionItemId = null
        String personName = null
        String personId = 'CSRSTU001'
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()
        assertEquals 5, response.length

    }

    @Test
    void testFetchByPersonIdNonExisting() {
        Long actionItemId = null
        String personName = null
        String personId = "CSRQWERTY"
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 0, response.result.size()
        assertEquals 0, response.length
    }

    //person name only

    @Test
    void testFetchByPersonNameExact() {
        Long actionItemId = null
        String personName = "Cliff Starr"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()
        assertEquals 5, response.length

    }

    @Test
    void testFetchByPersonNamePartial() {
        Long actionItemId = null
        String personName = "Cliff"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 6, response.result.size()
        assertEquals 6, response.length

    }

    @Test
    void testFetchByPersonNameNonExisting() {
        String personName = "Osama Bin Ladden"
        Long actionItemId = null
        String personId = null

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 0, response.result.size()
        assertEquals 0, response.length

    }

    @Test
    void testGetActionItem() {
        Long actionItemId = 3L
        String personName = "Cliff Starr"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length

        def actionItemDetails = monitorActionItemCompositeService.getActionItem(response.result[0].id)
        assertNotNull actionItemDetails
        assertEquals response.result[0].id, actionItemDetails.id
        assertEquals response.result[0].actionItemId, actionItemDetails.actionItemId
        assertEquals response.result[0].actionItemName, actionItemDetails.actionItemName
        assertEquals response.result[0].actionItemPersonName, actionItemDetails.actionItemPersonName
    }

}
