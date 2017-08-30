/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.bcm

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Test class for BannerCommManagementResourceAccessService
 */
class BannerCommManagementResourceAccessServiceIntegrationTests extends BaseIntegrationTestCase {
    def bannerCommManagementResourceAccessService


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
    void getBCMLocation() {
        def result = bannerCommManagementResourceAccessService.getBCMLocation()
        assertNotNull( result )
    }
}
