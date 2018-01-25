/********************************************************************************
  Copyright 2016-2018 Ellucian Company L.P. and its affiliates.
********************************************************************************/
package net.hedtech.banner.aip

import org.apache.log4j.Logger
import net.hedtech.banner.general.person.PersonUtility

class AipControllerUtils {
    static def log = Logger.getLogger(AipControllerUtils)

    static def getPersonForAip( def params, def loginPidm) {
        // If there is a bannerId in the params, then we are to get the profile of that person
        // Otherwise, we get the profile of the person logged in
        if (params.studentId) {
            return PersonUtility.getPerson( params.studentId )
        } else {
            return PersonUtility.getPerson( loginPidm )
        }
    }
}