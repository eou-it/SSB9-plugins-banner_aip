/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class MonitorActionItemCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def monitorActionItemCompositeService

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void listActionItemNames() {
        def result =monitorActionItemCompositeService.getactionItemNames()
        assert result.size() > 0
    }

}
