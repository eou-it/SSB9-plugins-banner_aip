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
                        ":" +                           // ":"
                        request.getServerPort() +       // "8080"
                        request.getRequestURI() +       // "/people"
                        (request.getQueryString() ? "?" + request.getQueryString() : "") // "lastname=Fox&age=30"
                println uri
                def reqParams = request?.JSON ?: params
                def reqController = request?.JSON ?: controllerName
                def reqAction = request?.JSON ?: actionName

                println "request came from: " + uri
                println "controller: " + reqController
                println "action: " + reqAction
                println "params: " + reqParams
                println reqParams['type']

                if ('testRedirect'.equals( reqParams['type'] ) || 'testRedirect'.equals( reqAction )) {
                    // Test that we can get db items here with user info
                    def myItems = userActionItemReadOnlyService.listActionItemByPidm( userPidm )
                    println "Verify can do actionItemLookup: " + myItems
                    //redirect( controller: "aip", action: "actionItems" )
                    //redirect( url: "https://anotherdomain.com/aip/actionItems", params: [optional: 'something'] )
                    redirect( url: uri + "/aip/actionItems", params: [optional: 'something'] )
                    return false
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