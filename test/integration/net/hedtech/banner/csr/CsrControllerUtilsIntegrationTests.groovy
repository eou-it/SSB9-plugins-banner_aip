package net.hedtech.banner.csr
import org.junit.Before
import org.junit.Test
import org.junit.After
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase

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
        def person = PersonUtility.getPerson("ADVA000032")
        assertNotNull person
        def csrUser = CsrControllerUtils.getPersonForCSR(params, person.pidm)
        println csrUser
        assertNull csrUser
    }
}
