/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger


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
    def getActionItemNames() {
        def actionItems = monitorActionItemReadOnlyService.listOfActionItemNames()
        def actionItemNamesList=[]
        actionItems.each {
            actionItemNamesList.add('id':it.getAt(0), 'name':it.getAt(1))
        }
        actionItemNamesList
    }

    def searchMonitorActionItems(Long actionId, String personName, String personId) {
        LOGGER.debug("Action ID : {$actionId} -- PersonName :{$personName} -- PersonID :{$personId}")
        def result
        if (actionId && personName && !personId) {          // action id + person name combination
            result = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionId, personName)
        } else if (actionId && !personName && personId) {   //action id + person id combination
            result = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionId, personId)
        } else if (actionId && !personName && !personId) {  //only action item id combination
            result = monitorActionItemReadOnlyService.fetchByActionItemId(actionId)
        } else if (!actionId && personName && !personId) {  // search by person name
            result = monitorActionItemReadOnlyService.fetchByPersonName(personName)
        } else if (!actionId && !personName && personId) {  // search by person id only
            result = monitorActionItemReadOnlyService.fetchByPersonId(personId)
        }
        def resultMap = [];
        result.each { it ->
            resultMap.add([id                  : it.id,
                           actionItemId        : it.actionItemId,
                           actionItemName      : it.actionItemName,
                           actionItemGroupName : it.actionItemGroupName,
                           spridenId           : it.spridenId,
                           actionItemPersonName: it.actionItemPersonName,
                           status              : it.status,
                           responseDate        : it.responseDate,
                           displayStartDate    : it.displayStartDate,
                           displayEndDate      : it.displayEndDate,
                           currentResponseText : it.currentResponseText,
                           reviewIndicator     : it.reviewIndicator,
                           reviewState         : it.reviewState,
                           attachments         : it.attachments])

        }

        return resultMap
    }
}
