/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import net.hedtech.banner.general.person.PersonUtility

class MonitorActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {



    Map paramsMap = [:]
    def criteriaMap = [:]
    def pagingAndSortParamsAsc = [sortColumn: "actionItemName", sortAscending: true, max: 50, offset: 0]
    def pagingAndSortParamsDesc = [sortColumn: "actionItemName", sortAscending: false, max: 50, offset: 0]


    ActionItem drugAndAlcoholPolicyActionItem;
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
    void testFetchActionItemsByExactPersonName() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Hank"

        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName

        assertEquals "CSRSTU004", result[0].spridenId
    }

    @Test
    void testFetchActionItemsByExactPersonNameSortDesc() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Hank"

        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsDesc)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName

        assertEquals "CSRSTU004", result[0].spridenId
    }


    @Test
    void testFetchActionItemsByExactPersonNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id;
        String personName = "Hank"

        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 1, result

    }

    @Test
    void testFetchActionItemsByBlankName() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = ""
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchActionItemsByNullName() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = null;
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchActionItemsByBlankNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = ""
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertEquals 13, result
    }

    @Test
    void testFetchActionItemsByNullNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = null;
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertEquals 13, result
    }

    @Test
    void testFetchActionItemsByNonExistingActionItemId() {
        Long actionItemId = 999L
        String personName = "Hank"
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNonExistingActionItemIdCount() {
        Long actionItemId = 999L
        String personName = "Hank"
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertEquals 0, result
    }

    @Test
    void testFetchActionItemsByNonExistingPerson() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNonExistingPersonCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertEquals 0, result
    }

    @Test
    void testFetchActionItemBySpridenId() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id

        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student

        def result = MonitorActionItemReadOnly.fetchByActionItemAndSpridenId(actionItemId, student.bannerId, pagingAndSortParamsAsc)
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "Cliff Starr", result[0].actionItemPersonName
        assertEquals "CSRSTU001", result[0].spridenId
    }

    @Test
    void testFetchActionItemBySpridenIdCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id

        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student

        def result = MonitorActionItemReadOnly.fetchByActionItemAndSpridenIdCount(actionItemId, student.bannerId)
        assertEquals 1, result

    }

    @Test
    void testFetchActionItemByNonExisitingSpridenId() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String spridenId = "CSRABCDEF"
        def result = MonitorActionItemReadOnly.fetchByActionItemAndSpridenId(actionItemId, spridenId, pagingAndSortParamsAsc)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemByNonExisitingSpridenIdCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String spridenId = "CSRABCDEF"
        def result = MonitorActionItemReadOnly.fetchByActionItemAndSpridenIdCount(actionItemId, spridenId)
        assertEquals 0, result
    }
    @Test
    void testFetchActionItemByNullSpridenIdCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String spridenId = null;
        def result = MonitorActionItemReadOnly.fetchByActionItemAndSpridenIdCount(actionItemId, spridenId)
        assertEquals 0, result
    }


    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = MonitorActionItemReadOnly.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyExistingCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id

        def result = MonitorActionItemReadOnly.fetchByActionItemIdCount(actionItemId)
        assertNotNull result
        assertEquals 13, result
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 3999L
        def result = MonitorActionItemReadOnly.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyNonExistingCount() {
        Long actionItemId = 3999L
        def result = MonitorActionItemReadOnly.fetchByActionItemIdCount(actionItemId)
        assertEquals 0, result
    }

    @Test
    void testFetchByPersonIdExisting() {
        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student
        def result = MonitorActionItemReadOnly.fetchByPersonId(student.bannerId, pagingAndSortParamsAsc)
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonIdExistingCount() {
        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student
        def result = MonitorActionItemReadOnly.fetchByPersonIdCount(student.bannerId)
        assertEquals 5, result

    }


    @Test
    void testFetchByPersonIdNonExisting() {
        String spridenId = "CSRQWERTY"
        def result = MonitorActionItemReadOnly.fetchByPersonId(spridenId, pagingAndSortParamsAsc)
        assertEquals 0, result.size()

    }

    @Test
    void testFetchByPersonIdNonExistingCount() {
        String spridenId = "CSRQWERTY"
        def result = MonitorActionItemReadOnly.fetchByPersonIdCount(spridenId)
        assertEquals 0, result

    }

    @Test
    void testFetchByPersonIdNullCount() {
        String spridenId = null
        def result = MonitorActionItemReadOnly.fetchByPersonIdCount(spridenId)
        assertEquals 0, result

    }

    @Test
    void testFetchByPersonNameExact() {
        String personName = "Cliff Starr"
        def result = MonitorActionItemReadOnly.fetchByPersonName(personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonNull() {
        String personName = null
        def result = MonitorActionItemReadOnly.fetchByPersonName(personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 50, result.size()

    }

    @Test
    void testFetchByPersonNameExactCount() {
        String personName = "Cliff Starr"
        def result = MonitorActionItemReadOnly.fetchByPersonNameCount(personName)
        assertEquals 5, result

    }


    @Test
    void testFetchByPersonNamePartial() {
        String personName = "Cliff"
        def result = MonitorActionItemReadOnly.fetchByPersonName(personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 6, result.size()

    }

    @Test
    void testFetchByPersonNamePartialCount() {
        String personName = "Cliff"
        def result = MonitorActionItemReadOnly.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 6, result

    }

    @Test
    void testFetchByPersonNameNonExisting() {
        String personName = "Osama Bin Ladden"
        def result = MonitorActionItemReadOnly.fetchByPersonName(personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 0, result.size()

    }

    @Test
    void testFetchByPersonNameNonExistingCount() {
        String personName = "Osama Bin Ladden"
        def result = MonitorActionItemReadOnly.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 0, result

    }
    @Test
    void testFetchByPersonNameNullCount() {
        String personName = null
        def result = MonitorActionItemReadOnly.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 143, result

    }
}

