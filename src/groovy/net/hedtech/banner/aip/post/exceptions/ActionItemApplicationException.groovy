/* ****************************************************************************
Copyright 2015 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.aip.post.exceptions

import net.hedtech.banner.exceptions.ApplicationException


class ActionItemApplicationException extends ApplicationException {

    public ActionItemApplicationException( entityClassOrName, Throwable e)
    {
        super(entityClassOrName, e)
    }

    public ActionItemApplicationException( entityClassOrName, String msg, String defaultMessage = null ) {
        super(entityClassOrName, msg, defaultMessage = null)
    }

    public String getType() {
        friendlyName
    }
}
