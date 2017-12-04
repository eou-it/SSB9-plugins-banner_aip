/** *****************************************************************************
 © 2016 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */

package net.hedtech.banner.aip.filter

import net.hedtech.banner.security.BannerUser
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.GrailsUrlPathHelper
import org.springframework.security.core.context.SecurityContextHolder

import javax.servlet.http.HttpSession


class GateKeepingFilters {
    private final log = Logger.getLogger( GateKeepingFilters.class )
    public static final String BLOCKREGISTERFORCOURSES = '/ssb/term/termSelection?mode=registration'
    private static final String SLASH = "/"

    private static final String QUESTION_MARK = "?"


    def springSecurityService

    def userBlockedProcessReadOnlyService

    def dependsOn = [net.hedtech.banner.security.AccessControlFilters.class]

    def filters = {
        actionItemFilter( controller: "selfServiceMenu|login|logout|error|dateConverter", invert: true ) {
            before = {
                // FIXME: get urls from tables. Check and cache
                // only want to look at type 'document'? not stylesheet, script, gif, font, ? ?
                // at this point he getRequestURI returns the forwared dispatcher URL */aip/myplace.dispatch
                String path = getServletPath( request )
                println path
                //if (!ApiUtils.isApiRequest() && !request.xhr) {
                if (isBlockingUrl(path)) { // checks path against list from DB
                    println "take a look at: " + request.getRequestURI(  )
                    HttpSession session = request.getSession()
                    if (springSecurityService.isLoggedIn() && path != null) {
                        if (path.equals( BLOCKREGISTERFORCOURSES )) {
                            //if ('classRegistration'.equals( reqController ) && ! 'getTerms'.equals( reqAction )) {
                            // Test that we can get db items here with user info

                            // FIXME: pull in registration info (urls and session variable) from tables
                            // TODO: may need to look at session variable to see if student in Registration
                            //println "session parms: " + session.getAttributeNames() // do we add our reason to this?
                            println "roleCode: " + session.getAttribute( 'selectedRole' )?.persona?.code
                            if ('STUDENT'.equals( session.getAttribute( 'selectedRole' )?.persona?.code )) {
                                def isBlocked = false
                                try {
                                    isBlocked = userBlockedProcessReadOnlyService.getBlockedProcessesByPidmAndActionItemId( userPidm, 11 )
                                    println "isBlocked: " + isBlocked + " for: " + userPidm
                                    println isBlocked ? true : false
                                } catch (Throwable t) {
                                    println "isBlocked: crap. service call failed"
                                    println t
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