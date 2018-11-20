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
    private static final String WILDCARD = ".*"
    def monitorActionItemReadOnlyService

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

    /**
     * Performs search on monitor action items
     * @param actionId Id of the action item
     * @param personName Name of the person to be serached
     * @param personId banner id of the person to be searched
     * @param filterData filter data object
     * @param pagingAndSortParams pagination and sorting params object
     * @return filtered list
     * */
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

        def resultMap = [result: filterResults(result, filterData.params.searchString),
                         length: count];

        return resultMap
    }

    /**
    *Filters the list of results based on the search string
    * @param result List of result
    * @param searchParam search string to be searched.
    * @return filtered list
    * */
    private def filterResults(def result, def searchParam) {
        def filteredResult
        String regexPattern
        regexPattern = searchParam ? WILDCARD + searchParam.toString().toUpperCase() + WILDCARD : WILDCARD + WILDCARD;
        filteredResult = result.findAll { it ->
                    it.actionItemName.toString().toUpperCase().matches(regexPattern) ||
                    it.status.toString().toUpperCase().matches(regexPattern) ||
                    it.currentResponseText.toString().toUpperCase().matches(regexPattern) ||
                    it.actionItemGroupName.toString().toUpperCase().matches(regexPattern) ||
                    it.actionItemPersonName.toString().toUpperCase().matches(regexPattern) ||
                    it.spridenId.toString().toUpperCase().matches(regexPattern)
            // TODO: below line to be uncommented once the  columns is modified  in db scripts
            //|| it.reviewState.toString().toUpperCase().matches(regexPattern)
        }
        filteredResult
    }
}
