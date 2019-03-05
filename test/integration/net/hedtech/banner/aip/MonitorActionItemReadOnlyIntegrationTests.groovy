/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test
import net.hedtech.banner.general.person.PersonUtility
import org.springframework.util.StopWatch

class MonitorActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {

    private static final log = Logger.getLogger(MonitorActionItemReadOnlyIntegrationTests.class)
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
    void testFetchActionItemsByPersonName() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Hank"
        StopWatch stopWatch = new StopWatch("testFetchActionItemsByPersonName")
        stopWatch.start("searchByPersonName")
        if (log.isDebugEnabled()) OverallUtility.turnSqlTraceOn("searchByPersonName")
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "CSRSTU004", result[0].spridenId
        stopWatch.stop()
        if (log.isDebugEnabled()) OverallUtility.turnSqlTraceOff("searchByPersonName")
        log.debug stopWatch.prettyPrint()
    }

    @Test
    void testFetchActionItemsByPersonNameSortDesc() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = "Hank"

        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName, pagingAndSortParamsDesc)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName

        assertEquals "CSRSTU004", result[0].spridenId
    }


    @Test
    void testFetchActionItemsByPersonNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id;
        String personName = "Hank"
        StopWatch stopWatch = new StopWatch("testFetchActionItemsByPersonNameCount")
        stopWatch.start("searchByPersonNameCount")
        if (log.isDebugEnabled()) OverallUtility.turnSqlTraceOn("searchByPersonNameCount")
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItemId, personName)
        assertNotNull result
        assertEquals 1, result
        stopWatch.stop()
        if (log.isDebugEnabled()) OverallUtility.turnSqlTraceOff("searchByPersonNameCount")
        log.debug stopWatch.prettyPrint()
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
    void testFetchActionItemsByBlankNameCount() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        String personName = ""
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
    void testFetchActionItemByPidm() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id

        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student

        def result = MonitorActionItemReadOnly.fetchByActionItemAndPidm(actionItemId, student.pidm, pagingAndSortParamsAsc)
        assertEquals 1, result.size()
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "CSRSTU001", result[0].spridenId
    }

    @Test
    void testFetchActionItemByNonExisitingPidm() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        Long pidm = 99999999L
        def result = MonitorActionItemReadOnly.fetchByActionItemAndPidm(actionItemId, pidm, pagingAndSortParamsAsc)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = drugAndAlcoholPolicyActionItem.id
        def result = MonitorActionItemReadOnly.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 3999L
        def result = MonitorActionItemReadOnly.fetchByActionItemId(actionItemId, pagingAndSortParamsAsc)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByPidmExisting() {
        def student = PersonUtility.getPerson("CSRSTU001")
        assertNotNull student
        def result = MonitorActionItemReadOnly.fetchByPidm(student.pidm, pagingAndSortParamsAsc)
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPidmNonExisting() {
        Long pidm = 9999999L
        def result = MonitorActionItemReadOnly.fetchByPidm(pidm, pagingAndSortParamsAsc)
        assertEquals 0, result.size()

    }

    //@Test
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

    //@Test
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

