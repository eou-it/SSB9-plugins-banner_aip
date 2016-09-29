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

import javax.servlet.http.HttpSession


class GateKeepingFilters {
    private final log = Logger.getLogger( GateKeepingFilters.class )

    def userActionItemReadOnlyService
    def springSecurityService

    def filters = {
        actionItemFilter( controller: '*', action: '*' ) {
            before = {
                HttpSession session = request.getSession()
                println "CRR: session: " + session

                println "CRR isLogged in: " + springSecurityService.isLoggedIn()
                String uri = request.getScheme() + "://" +   // "http" + "://
                        request.getServerName()// +       // "myhost"
                //":" +                           // ":"
                //request.getServerPort() +       // "8080"
                //request.getRequestURI() +       // "/people"
                //(request.getQueryString() ? "?" + request.getQueryString() : "") // "lastname=Fox&age=30"
                println uri
                def reqParams = request?.JSON ?: params
                def reqController = request?.JSON ?: controllerName
                def reqAction = request?.JSON ?: actionName

                println "request came from: " + uri
                println "controller: " + reqController
                println "action: " + reqAction
                println "params: " + reqParams
                println reqParams['mode']
                println "crr:sch: " + SecurityContextHolder

                if ('registration'.equals( reqParams['mode'] ) && 'term'.equals( reqController ) && 'termSelection'.equals( reqAction )) {
                    // Test that we can get db items here with user info
                    println "Verify can do actionItemLookup: pidm: " + userPidm
                    if (userActionItemReadOnlyService.listBlockingActionItemsByPidm( userPidm )) {
                        //redirect( controller: "aip", action: "actionItems" )
                        //redirect( url: "https://anotherdomain.com/aip/actionItems", params: [optional: 'something'] )
                        //redirect( url: uri + "/aip/actionItems", params: [optional: 'something'] )
                        redirect( url: uri + ":8080/BannerGeneralSsb/ssb/aip/list" )
                        return false
                    }
                }
            }
        }
    }

    // who am I?
    private def getUserPidm() {
        def user = SecurityContextHolder?.context?.authentication?.principal
        println user
        if (user instanceof BannerUser) {
            //TODO:: extract necessary user information and return. (ex: remove pidm, etc)
            return user.pidm
        }
        return null
    }
}