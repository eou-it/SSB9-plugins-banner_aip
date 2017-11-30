/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.pb

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class AipPageBuilderCompositeServiceIntegrationTests extends BaseIntegrationTestCase {
    def aipPageBuilderCompositeService


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
    void page() {
        def page = aipPageBuilderCompositeService.page( 'AIPMasterTemplateSystemRequired' )
        assert page.html != null
        assert page.pageName == 'AIPMasterTemplateSystemRequired'
        assert page.script != null
        assert page.compiled != null
    }


    @Test
    void pageScript() {
        String pageScript = aipPageBuilderCompositeService.pageScript( 'AIPMasterTemplateSystemRequired' )
        assert pageScript != null
        assert pageScript.contains( 'AIPMasterTemplateSystemRequired' )
    }

}
