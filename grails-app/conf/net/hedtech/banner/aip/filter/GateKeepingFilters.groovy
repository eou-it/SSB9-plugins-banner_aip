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

    def userActionItemReadOnlyService

    def filters = {
        actionItemFilter( controller: '*', action: '*' ) {
            before = {
                String uri = request.getScheme() + "://" +   // "http" + "://
                        request.getServerName() +       // "myhost"
                        (request.getServerPort().equals( 80 ) ? "" : ":" + request.getServerPort()) + // :port if not 80
                        //request.getServerPort() +
                        request.getRequestURI() +       // "/people"
                        (request.getQueryString() ? "?" + request.getQueryString() : "") // "lastname=Fox&age=30"
                def reqParams = request?.JSON ?: params
                def reqController = request?.JSON ?: controllerName
                def reqAction = request?.JSON ?: actionName

                println "request came from: " + uri
                println "controller: " + reqController
                println "action: " + reqAction
                println "params: " + reqParams
                if ('registration'.equals( reqParams['mode'] ) && 'term'.equals( reqController ) && 'termSelection'.equals( reqAction )) {
                    // Test that we can get db items here with user info
                    if (userActionItemReadOnlyService.listBlockingActionItemsByPidm( userPidm )) {
                        //redirect( controller: "aip", action: "actionItems" )
                        //redirect( url: "https://anotherdomain.com/aip/actionItems", params: [optional: 'something'] )
                        //redirect( url: uri + "/aip/actionItems", params: [optional: 'something'] )
                        redirect( url: uri + ":8080/BannerGeneralSsb/ssb/aip/blocked" )
                        return false
                    }
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