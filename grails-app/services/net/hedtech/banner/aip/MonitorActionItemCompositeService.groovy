/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Composite Service class for MonitorActionItemReadOnly domain
 */
class MonitorActionItemCompositeService extends ServiceBase {
    private static final def LOGGER = Logger.getLogger( this.class )
    def monitorActionItemReadOnlyService

    /**
     * List of  action item Names
     * @return
     */
    def getactionItemNames() {
        def actionItems = monitorActionItemReadOnlyService.listOfActionItemNames()
        def actionItemNamesList=[]
        actionItems.each {
            actionItemNamesList.add('id':it.getAt(0), 'name':it.getAt(1))
        }
        actionItemNamesList
    }
}
