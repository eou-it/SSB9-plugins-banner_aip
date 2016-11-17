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
import org.springframework.security.core.context.SecurityContextHolder


class GateKeepingFilters {
    private final log = Logger.getLogger( GateKeepingFilters.class )

//    ApplicationContext ctx = Holders.grailsApplication.mainContext
//    UserActionItemReadOnlyService userActionItemReadOnlyService = (UserActionItemReadOnlyService) ctx.getBean("userActionItemReadOnlyService")

    def userActionItemReadOnlyService

    def dependsOn = [net.hedtech.banner.security.AccessControlFilters.class]

    def filters = {
        actionItemFilter( controller: '*', action: '*' ) {
            before = {
                String uri = request.getScheme() + "://" +   // "http" + "://
                        request.getServerName() //+       // "myhost"
                //(request.getServerPort().equals( 80 ) ? "" : ":" + request.getServerPort()) // + // :port if not 80
                //request.getServerPort() +
                //request.getRequestURI() +       // "/people"
                //(request.getQueryString() ? "?" + request.getQueryString() : "") // "lastname=Fox&age=30"
                def reqParams = request?.JSON ?: params
                def reqController = request?.JSON ?: controllerName
                def reqAction = request?.JSON ?: actionName

                log.debug "controller: " + reqController
                log.debug "action: " + reqAction
                log.debug "params: " + reqParams
                log.debug "origin: " + request.getHeader("Origin");
                //if ('registration'.equals( reqParams['mode'] ) && 'term'.equals( reqController ) && !'search'.equals( reqAction )) {
                if ('classRegistration'.equals( reqController ) && ! 'getTerms'.equals( reqAction )) {
                    // Test that we can get db items here with user info
                    log.debug "session parms: " + session.getAttributeNames(  ) // do we add our reason to this?
                    Enumeration keys = session.getAttributeNames();
                    while (keys.hasMoreElements())
                    {
                      String key = (String)keys.nextElement();
                      //log.debug(key + ": " + session.getAttribute(key));
                    }
                    log.debug "roleCode: " + session.getAttribute( 'selectedRole' )?.persona?.code
                    //if ('STUDENT'.equals( session.getAttribute( 'selectedRole' )?.persona?.code )) {
                    def isBlocked = false
                    try {
                        isBlocked = userActionItemReadOnlyService.listBlockingActionItemsByPidm( userPidm )
                        log.debug "isBlocked: "
                        log.debug isBlocked ? true : false
                    } catch (Throwable t) {
                        log.debug "isBlocked: crap"
                        log.debug t
                    }

                    //if (isBlocked) {
                    //response.addHeader("Access-Control-Allow-Origin", "*");
                    // FIXME: goto general app. Trying same port for dev environment
                    log.debug "do redirect"
                    redirect( url: uri + ":8080/BannerGeneralSsb/ssb/aip/informedList" )
                    //    redirect( url: uri + ":8090/StudentRegistrationSsb/ssb/registrationHistory/registrationHistory" )
                    return false
                    //}
                    //}
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
}