/********************************************************************************
  Copyright 2018 Ellucian Company L.P. and its affiliates.
********************************************************************************/
package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.aip.AipControllerUtils
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class AipControllerUtilsIntegrationTests extends BaseIntegrationTestCase {

    @Before
    void setUp() {
        formContext = ['GUAGMNU','SELFSERVICE']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testGetPersonForAipParams() {
        def params = [studentId: "CSRSTU002"]
        def person = PersonUtility.getPerson( "CSRSTU002" )
        assertNotNull person
        def csrUser = AipControllerUtils.getPersonForAip( params, person.pidm )
        assertEquals( "Dallas", csrUser.firstName )
    }


    @Test
    void testGetPersonForAipUtil() {
        def params = [:]
        def person = PersonUtility.getPerson( "CSRSTU002" )
        assertNotNull person
        def csrUser = AipControllerUtils.getPersonForAip( params, person.pidm )
        assertEquals( "Dallas", csrUser.firstName )
    }
}
