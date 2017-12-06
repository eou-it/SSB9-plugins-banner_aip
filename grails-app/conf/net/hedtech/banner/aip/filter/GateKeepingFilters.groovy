/** *****************************************************************************
 © 2016 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */

package net.hedtech.banner.aip.filter

import net.hedtech.banner.aip.UserBlockedProcessReadOnly
import net.hedtech.banner.security.BannerUser
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.servlet.GrailsUrlPathHelper
import org.springframework.context.ApplicationContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.http.HttpSession


class GateKeepingFilters {
    private final Log log = LogFactory.getLog(this.getClass())
    public static final String BLOCKREGISTERFORCOURSES = '/ssb/term/termSelection?mode=registration'
    private static final String SLASH = "/"

    private static final String QUESTION_MARK = "?"


    def springSecurityService

    //def userBlockedProcessReadOnlyService

    def dependsOn = [net.hedtech.banner.security.AccessControlFilters.class]

    def filters = {
        actionItemFilter( controller: "selfServiceMenu|login|logout|error|dateConverter", invert: true ) {
            before = {
                //def servletCtx = ServletContextHolder.getServletContext()
                //ApplicationContext applicationContext = WebApplicationContextUtils.
                //        getRequiredWebApplicationContext( servletCtx )
                //userBlockedProcessReadOnlyService = applicationContext.getBean( 'userBlockedProcessReadOnlyService' )
                // FIXME: get urls from tables. Check and cache
                // only want to look at type 'document'? not stylesheet, script, gif, font, ? ?
                // at this point he getRequestURI returns the forwared dispatcher URL */aip/myplace.dispatch
                String path = getServletPath( request )
                log.info( "take a look at: " + request.getRequestURI() + " as user: " + userPidm)
                //if (!ApiUtils.isApiRequest() && !request.xhr) {
                if (isBlockingUrl( path )) { // checks path against list from DB
                    HttpSession session = request.getSession()
                    if (springSecurityService.isLoggedIn() && path != null) {
                        if (path.equals( BLOCKREGISTERFORCOURSES )) {
                            //if ('classRegistration'.equals( reqController ) && ! 'getTerms'.equals( reqAction )) {
                            // Test that we can get db items here with user info

                            // FIXME: pull in registration info (urls and session variable) from tables
                            // TODO: may need to look at session variable to see if student in Registration
                            log.info( "roleCode: " + session.getAttribute( 'selectedRole' )?.persona?.code)
                            if ('STUDENT'.equals( session.getAttribute( 'selectedRole' )?.persona?.code )) {
                                def isBlocked = false
                                try {
                                    isBlocked = UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId( userPidm, 12 )
                                    log.info( "isBlocked: " + isBlocked + " for: " + userPidm )
                                } catch (Throwable t) {
                                    log.info( "isBlocked: service call failed. Keep an eye on this as working. session aware proxy" )
                                    log.info( t )
                                }

                                if (isBlocked) {
                                    String uri = request.getScheme() + "://" +   // "http" + "://
                                            request.getServerName()
                                    //response.addHeader("Access-Control-Allow-Origin", "*");
                                    // FIXME: goto general app. Trying same port for dev environment
                                    // FIXME: make this configurable
                                    redirect( url: uri + ":8080/BannerGeneralSsb/ssb/aip/informedList" )
                                    //    redirect( url: uri + ":8090/StudentRegistrationSsb/ssb/registrationHistory/registrationHistory" )
                                    return false
                                }
                            }
                        }
                    }
                    return true
                }
            }
        }
    }
// who am I?
    private def getUserPidm() {
        def user = SecurityContextHolder?.context?.authentication?.principal
        if (user instanceof BannerUser) {
            //TODO:: extract necessary user information and return. (ex: remove pidm, etc)
            return user.pidm
        }
        return null
    }


    private getServletPath( request ) {
        GrailsUrlPathHelper urlPathHelper = new GrailsUrlPathHelper();
        String path = urlPathHelper.getOriginatingRequestUri( request );
        if (path != null) {
            path = path.substring( request.getContextPath().length() )
            if (SLASH.equals( path )) {
                path = null
            } else if (request?.getQueryString()) {
                path = path + QUESTION_MARK + request?.getQueryString()
            }
        }
        return path
    }

    // look a ThemeUtil for expiring cache pattern
    private boolean isBlockingUrl( String path ) {
        // compare to cached list, if exists (expiring?)
        // if not
        // call BlockedProcessReadOnlyService.getBlockedProcessUrlsAndActionItemIds()
        // create list
        // cache list
        return true
    }
}