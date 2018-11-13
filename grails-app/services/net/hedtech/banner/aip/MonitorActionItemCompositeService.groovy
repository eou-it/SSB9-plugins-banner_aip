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
    private static final def LOGGER = Logger.getLogger(this.class)
    def monitorActionItemReadOnlyService

    /**
     * get Action Item
     */
    def getActionItem(Long userActionItemId) {
        def userActionItemDetails = monitorActionItemReadOnlyService.findById(userActionItemId)
        def result =
        [id                  : userActionItemDetails.id,
          actionItemId        : userActionItemDetails.actionItemId,
          actionItemName      : userActionItemDetails.actionItemName,
          actionItemGroupName : userActionItemDetails.actionItemGroupName,
          spridenId           : userActionItemDetails.spridenId,
          actionItemPersonName: userActionItemDetails.actionItemPersonName,
          status              : userActionItemDetails.status,
          responseDate        : userActionItemDetails.responseDate,
          displayStartDate    : userActionItemDetails.displayStartDate,
          displayEndDate      : userActionItemDetails.displayEndDate,
          currentResponseText : userActionItemDetails.currentResponseText,
          reviewIndicator     : userActionItemDetails.reviewIndicator,
          reviewState         : userActionItemDetails.reviewState,
          attachments         : userActionItemDetails.attachments]

        return result
    }

    /**
     * List of  action item Names
     * @return
     */
    def getActionItemNames() {
        def actionItems = monitorActionItemReadOnlyService.listOfActionItemNames()
        def actionItemNamesList = []
        actionItems.each {
            actionItemNamesList.add('id': it.getAt(0), 'name': it.getAt(1))
        }
        actionItemNamesList
    }

    def searchMonitorActionItems(Long actionId, String personName, String personId,
                                 def filterData, def pagingAndSortParams) {
        LOGGER.debug("Action ID : {$actionId} -- PersonName :{$personName} -- PersonID :{$personId}-- ${filterData} -- ${pagingAndSortParams}")
        def qryresult
        def count
        if (actionId && personName && !personId) {          // action id + person name combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionId, personName, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionId, personName)
        } else if (actionId && !personName && personId) {   //action id + person id combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionId, personId, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenIdCount(actionId, personId)
        } else if (actionId && !personName && !personId) {  //only action item id combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemId(actionId, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByActionItemIdCount(actionId)
        } else if (!actionId && personName && !personId) {  // search by person name
            qryresult = monitorActionItemReadOnlyService.fetchByPersonName(personName, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        } else if (!actionId && !personName && personId) {  // search by person id only
            qryresult = monitorActionItemReadOnlyService.fetchByPersonId(personId, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByPersonIdCount(personId)
        }

        def result = [];

        qryresult.each { it ->
            result.add([id                  : it.id,
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

        def resultMap = [result: result,
                         length: count];

        return resultMap
    }
}
