/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import static org.junit.Assert.assertNotNull

class MonitorActionItemReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def monitorActionItemReadOnlyService

    Map paramsMap = [:]
    def criteriaMap = [:]
    def pagingAndSortParamsAsc = [sortColumn: "actionItemName", sortAscending: true, max: 50, offset: 0]
    def pagingAndSortParamsDesc = [sortColumn: "actionItemName", sortAscending: false, max: 50, offset: 0]
    ActionItem drugAndAlcoholPolicyActionItem;
    def filterData = [params: paramsMap, criteria: criteriaMap]

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        drugAndAlcoholPolicyActionItem = ActionItem.findByName("Drug and Alcohol Policy");
        assertNotNull drugAndAlcoholPolicyActionItem
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
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "CSRSTU004", result[0].spridenId
    }

    @Test
    void testFetchActionItemsByExactPersonNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 1, result

    }

    @Test
    void testFetchActionItemsByBlankName() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = ""
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchActionItemsByBlankNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = ""
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 13, result
    }

    @Test
    void testFetchActionItemsByNonExistingActionItemId() {
        Long actionItemId = 999L
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
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
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNonExistingPersonCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 0, result
    }

    @Test
    void testFetchActionItemBySpridenId() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionItemId, spridenId, pagingAndSortParamsAsc)
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "Cliff Starr", result[0].actionItemPersonName
        assertEquals "CSRSTU001", result[0].spridenId
    }

    @Test
    void testFetchActionItemBySpridenIdCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenIdCount(actionItemId, spridenId)
        assertEquals 1, result
    }

    @Test
    void testFetchActionItemByNonExisitingSpridenId() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String spridenId = "CSRABCDEF"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionItemId, spridenId, pagingAndSortParamsAsc)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemByNonExisitingSpridenIdCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String spridenId = "CSRABCDEF"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenIdCount(actionItemId, spridenId)
        assertEquals 0, result
    }

    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 13, result.size()
    }
    @Test
    void testFetchByActionItemIdOnlyExistingCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdCount(actionItemId)
        assertNotNull result
        assertEquals 13, result
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 3999L
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
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
        def result = monitorActionItemReadOnlyService.fetchByPersonId(spridenId, pagingAndSortParamsAsc)
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
        def result = monitorActionItemReadOnlyService.fetchByPersonId(spridenId, pagingAndSortParamsAsc)
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
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName, pagingAndSortParamsAsc)
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
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName, pagingAndSortParamsAsc)
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
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName, pagingAndSortParamsAsc)
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

    @Test
    void testFindById() {
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByPersonId(spridenId, pagingAndSortParamsAsc)
        assertTrue result.size() > 0

        MonitorActionItemReadOnly monitorActionItemReadOnly = monitorActionItemReadOnlyService.findById(result[0].id)

        assertNotNull monitorActionItemReadOnly
        assertEquals result[0].id, monitorActionItemReadOnly.id
        assertEquals result[0].actionItemId, monitorActionItemReadOnly.actionItemId
        assertEquals result[0].actionItemName, monitorActionItemReadOnly.actionItemName
        assertEquals result[0].actionItemPersonName, monitorActionItemReadOnly.actionItemPersonName
    }

}
