/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger

/**
 * Composite Service class for MonitorActionItemReadOnly domain
 */
class MonitorActionItemReadOnlyCompositeService extends ServiceBase {
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
