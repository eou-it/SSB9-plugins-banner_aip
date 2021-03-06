/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

@Integration
@Rollback
class MonitorActionItemCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def monitorActionItemCompositeService
    def userActionItemReadOnlyCompositeService
    def configUserPreferenceService
    def pagingAndSortParams = [sortColumn: "actionItemName", sortAscending: true, max: 50, offset: 0];
    def paramsMap = [:]
    def criteriaMap = [:]
    def filterData = [params: paramsMap, criteria: criteriaMap];
    ActionItem drugAndAlcoholPolicyActionItem
    ActionItem policyHandBookActionItem

    @Before
    void setUp() {
        formContext = ['SELFSERVICE']
        super.setUp()
        drugAndAlcoholPolicyActionItem = ActionItem.findByName("Drug and Alcohol Policy")
        assertNotNull drugAndAlcoholPolicyActionItem
        policyHandBookActionItem = ActionItem.findByName("Policy Handbook")
        assertNotNull policyHandBookActionItem
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
        Long actionId = drugAndAlcoholPolicyActionItem.id
        String personName = null
        String personId = "CSRSTU001"
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals "Drug and Alcohol Policy", response.result[0].actionItemName
        assertEquals "CSRSTU001", response.result[0].spridenId
        assertEquals 1, response.length

    }

    @Test
    void fetchByActionItemIDAndPersonIdNonExistingPerson() {
        Long actionId = drugAndAlcoholPolicyActionItem.id
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
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Cliff"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length

    }

    @Test
    void testFetchActionItemsByNameNonExistingName() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
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
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
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
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = null
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 12, response.result.size()
        assertEquals 12, response.length
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 399L
        String personName = null
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
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
        String personName = "Starr, Cliff"
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

    //Status,Response,Group, Person Name and Person ID,Action Item Name
    @Test
    void testFilterByStatus() {
        Long actionItemId = policyHandBookActionItem.id
        String personId = null
        String personName = null
        String searchparam = null;
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        //checking seed data
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()

        //Filtering by status
        searchparam = "pending"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 3, response.result.size()
        assertEquals 3, response.length

        searchparam = "completed"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 2, response.result.size()
        assertEquals 2, response.length
    }

    @Test
    void testFilterByPersonName() {
        Long actionItemId = policyHandBookActionItem.id
        String personId = null
        String personName = null
        String searchparam = "";
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        //checking seed data
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()

        searchparam = "hank"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length

        searchparam = "a"//for names containing a
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()
        assertEquals 5, response.length

        searchparam = ""
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals response.result.size(), 5
        assertEquals 5, response.length
    }

    @Test
    void testFilterByPersonId() {
        Long actionItemId = policyHandBookActionItem.id
        String personId = null
        String personName = null
        String searchparam = "";
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        //checking seed data
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()

        searchparam = "CSRSTU012"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length

        searchparam = "CSRSTU01"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 3, response.result.size()
        assertEquals 3, response.length
    }

    @Test
    void testFilterByActionItemName() {
        Long actionItemId = null
        String personId = "CSRSTU001"
        String personName = null
        String searchparam = "";
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()

        searchparam = "drug"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length

        searchparam = "Information"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length
    }

    @Test
    void testFilterByGroup() {
        Long actionItemId = null
        String personId = "CSRSTU001"
        String personName = null
        String searchparam = "";
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 5, response.result.size()

        searchparam = "Enrollment"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 3, response.result.size()
        assertEquals 3, response.length

        searchparam = "Students"
        paramsMap = [searchString: searchparam]
        filterData = [params: paramsMap, criteria: criteriaMap]
        response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length
    }

    @Test
    void testGetActionItem() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Cliff"
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

    @Test
    void testUpdateActionItemReview() {
        loginSSB('AIPADM001', '111111')
        Long actionItemId = policyHandBookActionItem.id
        String personName = null
        String personId = "CSRSTU004"
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertTrue response.result.size() > 0
        assertTrue response.length > 0
        def actionItemDetails = monitorActionItemCompositeService.getActionItem(response.result[0].id)
        def requestMap = [
                userActionItemId  : actionItemDetails.id,
                reviewStateCode   : 20,
                displayEndDate    : actionItemDetails.displayEndDate,
                responseId        : actionItemDetails.responseId,
                externalCommentInd: true,
                reviewComments    : 'test comments',
                contactInfo       : 'admin office'
        ]

        def result = monitorActionItemCompositeService.updateActionItemReview(requestMap)
        assertEquals true, result.success
    }

    @Test
    void testUpdateActionItemReviewInvalidDate() {
        loginSSB('AIPADM001', '111111')
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Cliff"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length
        def actionItemDetails = monitorActionItemCompositeService.getActionItem(response.result[0].id)
        def requestMap = [
                userActionItemId  : actionItemDetails.id,
                reviewStateCode   : 20,
                displayEndDate    : new Date(),
                responseId        : actionItemDetails.responseId,
                externalCommentInd: true,
                reviewComments    : 'test comments',
                contactInfo       : 'admin office'
        ]

        def result = monitorActionItemCompositeService.updateActionItemReview(requestMap)
        assertEquals false, result.success
    }

    @Test
    void testUpdateActionItemReviewInvalidUserActionItemId() {
        loginSSB('AIPADM001', '111111')
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Cliff"
        String personId = null
        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
        assertEquals 1, response.length
        def actionItemDetails = monitorActionItemCompositeService.getActionItem(response.result[0].id)
        def requestMap = [
                userActionItemId  : 12345,
                reviewStateCode   : 20,
                displayEndDate    : new Date(),
                responseId        : actionItemDetails.responseId,
                externalCommentInd: true,
                reviewComments    : 'test comments',
                contactInfo       : 'admin office'
        ]

        def result = monitorActionItemCompositeService.updateActionItemReview(requestMap)
        assertEquals false, result.success
    }

    @Test
    void testReviewStateNameInSearchResult() {
        loginSSB('CSRSTU004', '111111')
        def map = [locale: 'en-US']
        def statusMap = configUserPreferenceService.saveLocale(map)
        assert statusMap.status == 'success'

        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        def group = result.groups.find { it.title == 'Enrollment' }
        def item = group.items.find { it.name == 'Policy Handbook' }
        Long actionItemId = item.id
        assertNotNull actionItemId

        String personId = 'CSRSTU004'
        def output = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, null, personId, filterData, pagingAndSortParams)
        def list = output.result
        assert output.length > 0
        assert list.size() > 0
        assert list[0].reviewStateCode == "Review needed"
    }

    @Test
    void testGetReviewStatusList() {
        loginSSB('CSRSTU004', '111111')


        def map = [locale: 'en-US']
        def statusMap = configUserPreferenceService.saveLocale(map)
        assertEquals 'success', statusMap.status
        def output = monitorActionItemCompositeService.getReviewStatusList()
        assertNotNull output
    }

    @Test
    void testPersonNameSortingAsc() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personId = null
        String personName = null
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortAscending: true, max: 10, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 'Brady, Jesus B', response.result[0].actionItemPersonName
    }

    @Test
    void testPersonNameSortingDesc() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personId = null
        String personName = null
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortAscending: false, max: 10, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 'Worthington, Dennis', response.result[0].actionItemPersonName
    }

    @Test
    void testSearchWithFirstName() {
        Long actionItemId = null
        String personId = null
        String personName = ", WILLIAM"
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortDirection: "asc", max: 25, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 11, response.result.size()
    }

    @Test
    void testSearchWithLastName() {
        Long actionItemId = null
        String personId = null
        String personName = "SHERMAN,"
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortDirection: "asc", max: 10, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
    }

    @Test
    void testPersonNameSearchCaseInsensitive() {
        Long actionItemId = null
        String personId = null
        String personName = "hope, william f"
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortDirection: "asc", max: 10, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 10, response.result.size()
    }

    @Test
    void testSearchWithFirstAndLastName() {
        Long actionItemId = null
        String personId = null
        String personName = 'SHERMAN, WILLIAM'
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortDirection: "asc", max: 10, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
    }

    @Test
    void testSearchWithOnlyMiddleName() {
        Long actionItemId = null
        String personId = null
        String personName = " BFGHFH"
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortDirection: "asc", max: 10, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 1, response.result.size()
    }

    @Test
    void testSearchWithFullName() {
        Long actionItemId = null
        String personId = null
        String personName = 'Hope, William F'
        String searchparam = ""
        pagingAndSortParams = [sortColumn: "actionItemPersonName", sortDirection: "asc", max: 10, offset: 0];
        def paramsMap = [searchString: searchparam]
        def filterData = [params: paramsMap, criteria: criteriaMap]

        def response = monitorActionItemCompositeService.searchMonitorActionItems(actionItemId, personName, personId, filterData, pagingAndSortParams)
        assertNotNull response
        assertEquals 10, response.result.size()
    }
}
