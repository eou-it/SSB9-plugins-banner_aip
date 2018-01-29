/* ****************************************************************************
Copyright 2018 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.aip.post.exceptions

import net.hedtech.banner.exceptions.ApplicationException

/**
 * Convenience methods for creating an application exceptions. Largely the factory takes care of stricter
 * type checking in order to take some of the confusion out of exception naming conventions and
 * to alleviate the developer assembling @@r1 in the calling code.
 */
class ActionItemExceptionFactory {

    /**
     * Creates an application exception.
     *
     * The string in messages.properties should be the package name of the service + "." + the service class name + "." + resourceId.
     *
     * @param service the service whose namespace will be used for specifying the resourceId
     * @param resourceId the sub key of the string resource
     * @param parameter0 the first parameterized value which will be passed as the {0} of the string value
     * @return
     */
    public static ApplicationException createApplicationException( Class service, String resourceId ) {
        return new ApplicationException( service, "@@r1:${resourceId}@@" )
    }
}
