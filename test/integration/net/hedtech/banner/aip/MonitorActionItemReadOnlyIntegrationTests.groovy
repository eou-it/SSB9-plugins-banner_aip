/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.validation.ValidationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class MonitorActionItemReadOnlyIntegrationTests extends BaseIntegrationTestCase {

    def i_failure_actionItemId = "111"
    def i_failure_actionItemName = "Test"

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
    void testFetchActionItemNames() {
        List<MonitorActionItemReadOnly> monitorActionItems = MonitorActionItemReadOnly.fetchActionItemNames()
        assert monitorActionItems.size() > 0
    }

    @Test
    void testFetchActionItemsByExactPersonName() {
        Long actionItemId = 3L
        String personName = "Hank"
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName)
        assertNotNull result
        assertEquals 1, result.size()
        assertEquals 3, result[0].actionItemId
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "Hank Archibalde", result[0].actionItemPersonName
        assertEquals "CSRSTU004", result[0].spridenId
    }

    @Test
    void testFetchActionItemsByBlankName() {
        Long actionItemId = 3L
        String personName = ""
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName)
        assertNotNull result
        assertEquals 13, result.size()
    }

    private def newInvalidForCreateMonitorActionItemReadOnly() {
        def monitorActionItemReadOnly = new MonitorActionItemReadOnly(
                actionItemId: i_failure_actionItemId,
                actionItemName: i_failure_actionItemName
        )
        return monitorActionItemReadOnly
    }

    @Test
    void testFetchActionItemsByNonExistingActionItemId() {
        Long actionItemId = 999L
        String personName = "Hank"
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNonExistingPerson() {
        Long actionItemId = 3L
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItemId, personName)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemBySpridenId() {
        Long actionItemId = 3L
        String spridenId = "CSRSTU001"
        def result = MonitorActionItemReadOnly.fetchByActionItemAndSpridenId(actionItemId, spridenId)
        assertEquals 1, result.size()
        assertEquals 3, result[0].actionItemId
        assertEquals "Drug and Alcohol Policy", result[0].actionItemName
        assertEquals "Cliff Starr", result[0].actionItemPersonName
        assertEquals "CSRSTU001", result[0].spridenId
    }

    @Test
    void testFetchActionItemByNonExisitingSpridenId() {
        Long actionItemId = 3L
        String spridenId = "CSRABCDEF"
        def result = MonitorActionItemReadOnly.fetchByActionItemAndSpridenId(actionItemId, spridenId)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = 3L
        def result = MonitorActionItemReadOnly.fetchByActionItemId(actionItemId)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 3999L
        def result = MonitorActionItemReadOnly.fetchByActionItemId(actionItemId)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByPersonIdExisting() {
        String spridenId = "CSRSTU001"
        def result = MonitorActionItemReadOnly.fetchByPersonId(spridenId)
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonIdNonExisting() {
        String spridenId = "CSRQWERTY"
        def result = MonitorActionItemReadOnly.fetchByPersonId(spridenId)
        assertEquals 0, result.size()

    }

    @Test
    void testFetchByPersonNameExact() {
        String personName = "Cliff Starr"
        def result = MonitorActionItemReadOnly.fetchByPersonName(personName)
        assertNotNull result
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonNamePartial() {
        String personName = "Cliff"
        def result = MonitorActionItemReadOnly.fetchByPersonName(personName)
        assertNotNull result
        assertEquals 6, result.size()

    }

    @Test
    void testFetchByPersonNameNonExisting() {
        String personName = "Osama Bin Ladden"
        def result = MonitorActionItemReadOnly.fetchByPersonName(personName)
        assertNotNull result
        assertEquals 0, result.size()

    }

}
