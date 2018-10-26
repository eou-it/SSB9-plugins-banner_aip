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
    void testCreateInvalidMonitorActionItemReadOnly() {
        def monitorActionItemReadOnly = newInvalidForCreateMonitorActionItemReadOnly()
        shouldFail(UncategorizedSQLException) {
            monitorActionItemReadOnly.save(failOnError: true, flush: true)
        }
    }

    private def newInvalidForCreateMonitorActionItemReadOnly() {
        def monitorActionItemReadOnly = new MonitorActionItemReadOnly(
                actionItemId: i_failure_actionItemId,
                actionItemName: i_failure_actionItemName
        )
        return monitorActionItemReadOnly
    }
}
