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
        List<MonitorActionItemReadOnly> monitorActionItems = monitorActionItemReadOnlyService.listOfActionItemNames( )
        assert monitorActionItems.size() > 0
    }
}
