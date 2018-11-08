/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class MonitorActionItemReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def monitorActionItemReadOnlyService

    Map paramsMap = [:]
    def criteriaMap = [:]
    def pagingAndSortParams = [sortColumn: "actionItemName", sortDirection: "asc", max: 50, offset: 0]
    def filterData = [params: paramsMap, criteria: criteriaMap]

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
    void testFetchUserActionItemNamesExist() {
        List<MonitorActionItemReadOnly> monitorActionItems = monitorActionItemReadOnlyService.listOfActionItemNames()
        assert monitorActionItems.size() > 0
    }

    @Test
    void testFetchActionItemsByExactPersonName() {
        Long actionItemId = 3L
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals 3, result[0].actionItemId
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "CSRSTU004", result[0].spridenId
    }

    @Test
    void testFetchActionItemsByExactPersonNameCount() {
        Long actionItemId = 3L
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 1, result

    }

    @Test
    void testFetchActionItemsByBlankName() {
        Long actionItemId = 3L
        String personName = ""
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchActionItemsByBlankNameCount() {
        Long actionItemId = 3L
        String personName = ""
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 13, result
    }

    @Test
    void testFetchActionItemsByNonExistingActionItemId() {
        Long actionItemId = 999L
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 0, result.size()
    }
    @Test
    void testFetchActionItemsByNonExistingActionItemIdCount() {
        Long actionItemId = 999L
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId,personName)
        assertNotNull result
        assertEquals 0, result
    }

    @Test
    void testFetchActionItemsByNonExistingPerson() {
        Long actionItemId = 3L
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNonExistingPersonCount() {
        Long actionItemId = 3L
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 0, result
    }

    @Test
    void testFetchActionItemBySpridenId() {
        Long actionItemId = 3L
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionItemId, spridenId, filterData, pagingAndSortParams)
        assertEquals 1, result.size()
        assertEquals 3, result[0].actionItemId
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "Cliff Starr", result[0].actionItemPersonName
        assertEquals "CSRSTU001", result[0].spridenId
    }

    @Test
    void testFetchActionItemBySpridenIdCount() {
        Long actionItemId = 3L
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenIdCount(actionItemId, spridenId)
        assertEquals 1, result
    }

    @Test
    void testFetchActionItemByNonExisitingSpridenId() {
        Long actionItemId = 3L
        String spridenId = "CSRABCDEF"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionItemId, spridenId, filterData, pagingAndSortParams)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemByNonExisitingSpridenIdCount() {
        Long actionItemId = 3L
        String spridenId = "CSRABCDEF"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenIdCount(actionItemId, spridenId)
        assertEquals 0, result
    }

    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = 3L
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 13, result.size()
    }
    @Test
    void testFetchByActionItemIdOnlyExistingCount() {
        Long actionItemId = 3L
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdCount(actionItemId)
        assertNotNull result
        assertEquals 13, result
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 3999L
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 0, result.size()
    }
    @Test
    void testFetchByActionItemIdOnlyNonExistingCount() {
        Long actionItemId = 3999L
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdCount(actionItemId)
        assertNotNull result
        assertEquals 0, result
    }


    @Test
    void testFetchByPersonIdExisting() {
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByPersonId(spridenId, filterData, pagingAndSortParams)
        assertEquals 5, result.size()

    }
    @Test
    void testFetchByPersonIdExistingCount() {
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByPersonIdCount(spridenId)
        assertEquals 5, result

    }

    @Test
    void testFetchByPersonIdNonExisting() {
        String spridenId = "CSRQWERTY"
        def result = monitorActionItemReadOnlyService.fetchByPersonId(spridenId, filterData, pagingAndSortParams)
        assertEquals 0, result.size()

    }

    @Test
    void testFetchByPersonIdNonExistingCount() {
        String spridenId = "CSRQWERTY"
        def result = monitorActionItemReadOnlyService.fetchByPersonIdCount(spridenId)
        assertEquals 0, result

    }

    @Test
    void testFetchByPersonNameExact() {
        String personName = "Cliff Starr"
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 5, result.size()

    }


    @Test
    void testFetchByPersonNameExactCount() {
        String personName = "Cliff Starr"
        def result = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 5, result

    }


    @Test
    void testFetchByPersonNamePartial() {
        String personName = "Cliff"
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 6, result.size()

    }
    @Test
    void testFetchByPersonNamePartialCount() {
        String personName = "Cliff"
        def result = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 6, result

    }


    @Test
    void testFetchByPersonNameNonExisting() {
        String personName = "Osama Bin Ladden"
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName, filterData, pagingAndSortParams)
        assertNotNull result
        assertEquals 0, result.size()

    }

    @Test
    void testFetchByPersonNameNonExistingCount() {
        String personName = "Osama Bin Ladden"
        def result = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 0, result

    }

}
