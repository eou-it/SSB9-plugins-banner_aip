import grails.util.GrailsWebUtil
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

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
        def person = PersonUtility.getPerson( "CSRSTU018" ) // user had blocking
        assertNotNull person
        //loginSSB( person.bannerId, '111111' )

        def result = request( person, [mode: 'registration'], "term", "termSelection" )
        println result
        assert !result
        println "CRR: test response: " + response.redirectedUrl

        assertTrue response.redirectedUrl.endsWith( '/aip/list' )
    }


    @Test
    void testFilterNoRedirect() {
        def person = PersonUtility.getPerson( "CSRSTU013" ) // user has no blocking AIs
        assertNotNull person
        //loginSSB( person.bannerId, '111111' )

        def result = request( person, [mode: 'registration'], "term", "termSelection" )
        println result
        assert result

        assertNull(  response.redirectedUrl )
    }


    def getResponse() {
        grailsWebRequest.currentResponse
    }


    def request( def person, Map params, controllerName, actionName ) {
        grailsApplication.config.formControllerMap = formControllerMap

        grailsWebRequest = GrailsWebUtil.bindMockWebRequest( grailsApplication.mainContext )
        grailsWebRequest.params.putAll( params )
        grailsWebRequest.controllerName = controllerName
        grailsWebRequest.actionName = actionName

        loginSSB( person.bannerId, '111111' )

        filterInterceptor.preHandle( grailsWebRequest.request, grailsWebRequest.response, null )
    }

}
