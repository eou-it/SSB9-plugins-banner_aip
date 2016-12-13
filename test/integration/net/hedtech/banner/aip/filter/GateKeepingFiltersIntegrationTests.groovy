package net.hedtech.banner.aip.filter

import grails.util.GrailsWebUtil
import grails.util.Holders
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

/** *****************************************************************************
 © 2016 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */

/**
 * GateKeepingFiltersIntegrationTests.
 *
 * Date: 8/26/2016
 * Time: 4:44 PM
 */
class GateKeepingFiltersIntegrationTests extends BaseIntegrationTestCase {

    public static final String BLOCKREGISTERFORCOURSES = '/ssb/term/termSelection?mode=registration'
    public static final String BLOCKPLANAHEAD = '/what is this'
    public static final String UNBLOCKEDURI = '/somethingrandom'

    def filterInterceptor

    def grailsApplication

    def grailsWebRequest

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        logout()
        super.tearDown()
    }

    def formControllerMap = [
            'banner'                    : ['SCACRSE'],
            'mainpage'                  : ['SELFSERVICE'],
            'menu'                      : ['SELFSERVICE'],
            'selfservicemenu'           : ['SELFSERVICE-EMPLOYEE', 'SELFSERVICE'],
            'survey'                    : ['SELFSERVICE'],
            'useragreement'             : ['SELFSERVICE'],
            'securityqa'                : ['SELFSERVICE'],
            'general'                   : ['SELFSERVICE'],
            'updateaccount'             : ['SELFSERVICE'],
            'accountlisting'            : ['SELFSERVICE'],
            'directdepositconfiguration': ['SELFSERVICE'],
            '/ssb/registration/**'      : ['IS_AUTHENTICATED_ANONYMOUSLY'],
            '/ssb/registration/registerPostSignIn/**': ['ROLE_SELFSERVICE-STUDENT_BAN_DEFAULT_M',
                                                        'ROLE_SELFSERVICE_BAN_DEFAULT_M',
                                                        'ROLE_SELFSERVICE-REGISTRAR_BAN_DEFAULT_M',
                                                        'ROLE_SELFSERVICE-FACULTY_BAN_DEFAULT_M'],
            '/ssb/classRegistration/**' : ['ROLE_SELFSERVICE-STUDENT_BAN_DEFAULT_M',
                                          'ROLE_SELFSERVICE_BAN_DEFAULT_M',
                                          'ROLE_SELFSERVICE-REGISTRAR_BAN_DEFAULT_M',
                                          'ROLE_SELFSERVICE-FACULTY_BAN_DEFAULT_M'],
            'aip'                       : ['SELFSERVICE'],
            'aipgroup'                  : ['SELFSERVICE'],
            'aipadmin'                  : ['SELFSERVICE']
    ]


    @Test
    void testFilterAPI() { // do not evaluate apis
        def person = PersonUtility.getPerson( "CSRSTU018" ) // user has blocking
        assertNotNull person
        loginSSB( person.bannerId, '111111' )

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.getSession( true )
        request.characterEncoding = 'UTF-8'
        request.setRequestURI( BLOCKREGISTERFORCOURSES )
        request.addHeader( 'X-Requested-With', "XMLHttpRequest" )

        def result = doRequest( request )
        assert result

        assertNull(  response.redirectedUrl )
    }


    @Test
    void testFilterRedirectsController() {
        def person = PersonUtility.getPerson( "CSRSTU018" ) // user had blocking
        assertNotNull person
        loginSSB( person.bannerId, '111111' )

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.getSession( true )
        request.characterEncoding = 'UTF-8'
        request.setRequestURI( BLOCKREGISTERFORCOURSES )

        // mock persona? might need for registration student selected
        //request.session.setAttribute('selectedRole', persona)

        def result = doRequest( request )
        assert !result

        assertTrue response.redirectedUrl.endsWith( 'aip/informedList' )
    }


    @Test
    void testFilterNoRedirect() {
        def person = PersonUtility.getPerson( "CSRSTU013" ) // user has no blocking AIs
        assertNotNull person
        loginSSB( person.bannerId, '111111' )

        MockHttpServletRequest request = new MockHttpServletRequest()
        request.getSession( true )
        request.characterEncoding = 'UTF-8'
        request.setRequestURI( UNBLOCKEDURI )

        def result = doRequest( request )
        assert result

        assertNull(  response.redirectedUrl )
    }


    def getResponse() {
        grailsWebRequest.currentResponse
    }


    def doRequest( MockHttpServletRequest mockRequest ) {
        grailsApplication.config.formControllerMap = formControllerMap
        grailsWebRequest = GrailsWebUtil.bindMockWebRequest(
                Holders.getGrailsApplication().mainContext, mockRequest, new MockHttpServletResponse() )

        filterInterceptor.preHandle( grailsWebRequest.request, grailsWebRequest.response, null )
    }


    static class PersonaRule {
        def persona = GateKeepingFiltersIntegrationTests.Persona
    }


    static class Persona {
        String code = 'STUDENT'
    }


}
