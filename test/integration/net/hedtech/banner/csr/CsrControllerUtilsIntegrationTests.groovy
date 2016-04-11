package net.hedtech.banner.csr

import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class CsrControllerUtilsIntegrationTests extends BaseIntegrationTestCase {

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
    void testGetPersonForCSR() {
        def params = [:]
        def person = PersonUtility.getPerson("CSRSTU002")
        assertNotNull person
        def csrUser = CsrControllerUtils.getPersonForCSR( params, person.pidm )
        assertEquals( "Dallas", csrUser.firstName )
    }
}
