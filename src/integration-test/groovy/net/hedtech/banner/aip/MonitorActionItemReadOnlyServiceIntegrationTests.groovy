/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.general.person.PersonUtility

import static org.junit.Assert.assertNotNull
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class MonitorActionItemReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def monitorActionItemReadOnlyService

    Map paramsMap = [:]
    def criteriaMap = [:]
    def pagingAndSortParamsAsc = [sortColumn: "actionItemName", sortAscending: true, max: 50, offset: 0]
    def pagingAndSortParamsDesc = [sortColumn: "actionItemName", sortAscending: false, max: 50, offset: 0]
    ActionItem drugAndAlcoholPolicyActionItem
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
    void testFetchActionItemsByExactPersonName() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "ARCHIBALDE, HANK"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "CSRSTU004", result[0].spridenId
    }

    @Test
    void testFetchActionItemsByExactPersonNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "ARCHIBALDE, HANK"
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
        assertEquals 12, result.size()
    }

    @Test
    void testFetchActionItemsByBlankNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = ""
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 12, result
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
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
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
        def pidm = PersonUtility.getPerson("CSRSTU001").pidm
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndPidm(actionItemId, pidm, pagingAndSortParamsAsc)
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "CLIFF", result[0].personSearchFirstName
        assertEquals "CSRSTU001", result[0].spridenId
    }


    @Test
    void testFetchActionItemByNonExisitingPidm() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        Long pidm = 99999999L
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndPidm(actionItemId, pidm, pagingAndSortParamsAsc)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 12, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 3999L
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByPersonIdExisting() {
        def pidm = PersonUtility.getPerson("CSRSTU001").pidm
        def result = monitorActionItemReadOnlyService.fetchByPidm(pidm, pagingAndSortParamsAsc)
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonIdNonExisting() {
        Long pidm = 99999999L
        def result = monitorActionItemReadOnlyService.fetchByPidm(pidm, pagingAndSortParamsAsc)
        assertEquals 0, result.size()

    }

    @Test
    void testFetchByPersonNameExact() {
        String personName = "Starr, Cliff"
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonNameExactCount() {
        String personName = "Starr, Cliff"
        def result = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 5, result

    }

    @Test
    void testFetchByPersonLastNameCount() {
        String personName = "Doll"
        def result = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 2, result

    }

    @Test
    void testFetchByPersonFirstNameCount() {
        String personName = "CORDELL"
        def result = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 2, result

    }

    @Test
    void testFetchByPersonMiddleNameCount() {
        String personName = "Bfghfh"
        def result = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        assertNotNull result
        assertEquals 1, result

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
        def pidm = PersonUtility.getPerson("CSRSTU001").pidm
        def result = monitorActionItemReadOnlyService.fetchByPidm(pidm, pagingAndSortParamsAsc)
        assertTrue result.size() > 0

        MonitorActionItemReadOnly monitorActionItemReadOnly = monitorActionItemReadOnlyService.findById(result[0].id)

        assertNotNull monitorActionItemReadOnly
        assertEquals result[0].id, monitorActionItemReadOnly.id
        assertEquals result[0].actionItemId, monitorActionItemReadOnly.actionItemId
        assertEquals result[0].actionItemName, monitorActionItemReadOnly.actionItemName
        assertEquals result[0].spridenId, monitorActionItemReadOnly.spridenId
    }

}
