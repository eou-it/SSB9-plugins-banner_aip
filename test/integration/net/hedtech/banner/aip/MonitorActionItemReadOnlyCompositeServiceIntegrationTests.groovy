/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class MonitorActionItemReadOnlyCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def monitorActionItemReadOnlyCompositeService

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
        loginSSB( 'CSRSTU004', '111111' )
        def result =monitorActionItemReadOnlyCompositeService.getactionItemNames()
        assert result.size() > 0
    }

}
