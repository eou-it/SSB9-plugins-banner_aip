package net.hedtech.banner.csr

import org.apache.log4j.Logger
import net.hedtech.banner.general.person.PersonUtility

class CsrControllerUtils {
    static def log = Logger.getLogger(CsrControllerUtils)

    static def getPersonForCSR( def params, def loginPidm) {
        // If there is a bannerId in the params, then we are to get the profile of that person
        // Otherwise, we get the profile of the person logged in
        if (params.studentId) {
            return PersonUtility.getPerson( params.studentId )
        } else {
            return PersonUtility.getPerson( loginPidm )
        }
    }
}