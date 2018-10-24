/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for MonitorrActionItemReadOnly domain
 */
class MonitorActionItemReadOnlyService extends ServiceBase {

    /**
     * Lists of action items
     * @param
     * @return
     */
    def listOfActionItemNames( ) {
        return MonitorActionItemReadOnly.fetchActionItemNames()
    }

}
