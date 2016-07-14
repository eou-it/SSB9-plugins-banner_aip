package net.hedtech.banner.aip

import net.hedtech.banner.aip.AipControllerUtils
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class AipControllerUtilsIntegrationTests extends BaseIntegrationTestCase {

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
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