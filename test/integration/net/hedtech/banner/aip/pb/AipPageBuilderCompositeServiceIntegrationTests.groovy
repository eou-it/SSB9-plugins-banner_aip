/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
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
        String query = """ INSERT INTO PAGE ( CONSTANT_NAME, VERSION, ID, MODEL_VIEW ) VALUES ('TestAIPMasterTemplateSystemRequired', 0, -9999, :clob) """
        println query
        sessionFactory.currentSession.createSQLQuery( query ).setString( 'clob', '{ "name":"TestAIPMasterTemplateSystemRequired","type":"page" }' ).executeUpdate()
        def page = aipPageBuilderCompositeService.page( 'TestAIPMasterTemplateSystemRequired' )
        assert page.html != null
        assert page.pageName == 'TestAIPMasterTemplateSystemRequired'
        assert page.script != null
        assert page.compiled != null
    }


    @Test
    void pageScript() {
        String query = """ INSERT INTO PAGE ( CONSTANT_NAME, VERSION, ID, MODEL_VIEW ) VALUES ('TestAIPMasterTemplateSystemRequired', 0, -9999, '{ \t"name": "TestAIPMasterTemplateSystemRequired", \t"type": "page" }') """
        println query
        sessionFactory.currentSession.createSQLQuery( query ).executeUpdate()
        String pageScript = aipPageBuilderCompositeService.pageScript( 'TestAIPMasterTemplateSystemRequired' )
        assert pageScript != null
        assert pageScript.contains( 'TestAIPMasterTemplateSystemRequired' )
    }

}
