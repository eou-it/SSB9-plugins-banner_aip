import grails.util.GrailsWebUtil
import grails.util.Holders
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.context.ApplicationContext
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

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

    def filterInterceptor

    def grailsApplication

    def grailsWebRequest

    def conn


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
        logout()
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
            'aip'                       : ['SELFSERVICE'],
            'aipgroup'                  : ['SELFSERVICE'],
            'aipadmin'                  : ['SELFSERVICE']
    ]


    @Test
    void testFilterRedirectsController() {
        def person = PersonUtility.getPerson( "CSRSTU002" )
        assertNotNull person
        def auth = selfServiceBannerAuthenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken( person.bannerId, '111111' ) )
        SecurityContextHolder.getContext().setAuthentication( auth )

        def result = request( [type: 'testRedirect'], "aip", "getActionGroupDescription" )
        println result
        assert !result
        println "CRR: test response: " + response.redirectedUrl

        assertTrue response.redirectedUrl.endsWith( '/aip/actionItems' )
    }


    def getResponse() {
        grailsWebRequest.currentResponse
    }


    def request( Map params, controllerName, actionName ) {
        grailsApplication.config.formControllerMap = formControllerMap
        grailsWebRequest = GrailsWebUtil.bindMockWebRequest( grailsApplication.mainContext )
        grailsWebRequest.params.putAll( params )
        grailsWebRequest.controllerName = controllerName
        grailsWebRequest.actionName = actionName
        filterInterceptor.preHandle( grailsWebRequest.request, grailsWebRequest.response, null )
    }

}
