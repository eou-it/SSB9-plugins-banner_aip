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
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName)
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
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchActionItemsByNonExistingActionItemId() {
        Long actionItemId = 999L
        String personName = "Hank"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemsByNonExistingPerson() {
        Long actionItemId = 3L
        String personName = "QAWSEDRFTGYHUJUKLIOPIPOIPIPOI"
        def result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionItemId, personName)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchActionItemBySpridenId() {
        Long actionItemId = 3L
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionItemId, spridenId)
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
        def result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionItemId, spridenId)
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyExisting() {
        Long actionItemId = 3L
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId)
        assertNotNull result
        assertEquals 13, result.size()
    }

    @Test
    void testFetchByActionItemIdOnlyNonExisting() {
        Long actionItemId = 3999L
        def result = monitorActionItemReadOnlyService.fetchByActionItemId(actionItemId)
        assertNotNull result
        assertEquals 0, result.size()
    }

    @Test
    void testFetchByPersonIdExisting() {
        String spridenId = "CSRSTU001"
        def result = monitorActionItemReadOnlyService.fetchByPersonId(spridenId)
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonIdNonExisting() {
        String spridenId = "CSRQWERTY"
        def result = monitorActionItemReadOnlyService.fetchByPersonId(spridenId)
        assertEquals 0, result.size()

    }

    @Test
    void testFetchByPersonNameExact() {
        String personName = "Cliff Starr"
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName)
        assertNotNull result
        assertEquals 5, result.size()

    }

    @Test
    void testFetchByPersonNamePartial() {
        String personName = "Cliff"
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName)
        assertNotNull result
        assertEquals 6, result.size()

    }

    @Test
    void testFetchByPersonNameNonExisting() {
        String personName = "Osama Bin Ladden"
        def result = monitorActionItemReadOnlyService.fetchByPersonName(personName)
        assertNotNull result
        assertEquals 0, result.size()

    }


}
