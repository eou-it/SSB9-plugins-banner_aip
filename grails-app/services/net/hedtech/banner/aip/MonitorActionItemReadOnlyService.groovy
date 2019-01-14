/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase


/**
 * Service class for MonitorActionItemReadOnly domain
 */
class MonitorActionItemReadOnlyService extends ServiceBase {

    /**
     * Lists of action items
     * @param
     * @return
     */
    def listOfActionItemNames() {
        return MonitorActionItemReadOnly.fetchActionItemNames()
    }

    /**
     * Lists of action items filtered by actionItem id and Person name
     * @param actionItem ID of action item that is being searched
     * @param personName name of the person to be searched
     * @param pagingAndSortParams
     * @return List of action items
     */
    List<MonitorActionItemReadOnly> fetchByActionItemIdAndPersonName(Long actionItem, String personName, def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItem, personName, pagingAndSortParams)
    }

    /**
     * Lists of action items filtered by actionItem id and Person name
     * @param actionItem  Action Item Id of action item to be searched
     * @param spridenId Spriden Id of the person to be searched
     * @return List < MonitorActionItemReadOnly >  list of actionItems
     */
    List<MonitorActionItemReadOnly> fetchByActionItemAndSpridenId(Long actionItemId, String spridenId, def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemAndSpridenId(actionItemId, spridenId, pagingAndSortParams)
    }

    /**
     * Count of lists of action items filtered by actionItem id and Person name
     * @param actionItemId  Action Item Id of action item to be searched
     * @param spridenId Spriden Id of the person that is being searched
     * @return Count of action items
     */
    def fetchByActionItemAndSpridenIdCount(Long actionItemId, String spridenId) {
        return MonitorActionItemReadOnly.fetchByActionItemAndSpridenIdCount(actionItemId, spridenId)
    }

    /**
     * Lists of action items filtered by actionItem id
     * @param actionItemId  Action Item Id of action item to be searched
     * @param spridenId Spriden Id of the person being searched
     * @return List of actionItems
     */
    List<MonitorActionItemReadOnly> fetchByActionItemId(Long actionItemId,  def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemId(actionItemId, pagingAndSortParams)
    }

    /**
     * Lists of action items filtered by action item id
     * @param actionItem  Action Item Id of action item to be searched
     * @return count of action items
     * */
    def fetchByActionItemIdCount(Long actionItemId) {
        return MonitorActionItemReadOnly.fetchByActionItemIdCount(actionItemId)
    }

    /**
     * Lists of action items filtered by person name
     * @param personName name of the person to be searched
     * @param pagingAndSortParams
     * @return List of action items
     * */
    List<MonitorActionItemReadOnly> fetchByPersonName(String personName,  def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByPersonName(personName, pagingAndSortParams)
    }

    /**
     * Count of action items filtered by person Name
     * @param personName name of the person to be searched
     * @return count of action items
     * */
    def fetchByPersonNameCount(String personName) {
        return MonitorActionItemReadOnly.fetchByPersonNameCount(personName)
    }


    /**
     * Lists of action items filtered by person id
     * @param PersonID of the person to be searched
     * @param pagingAndSortParams pagaination and sorting parameter
     * @return List of action items
     * */
    List<MonitorActionItemReadOnly> fetchByPersonId(String spridenId,  def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByPersonId(spridenId, pagingAndSortParams)
    }

    /**
     * Lists of action items filtered by person id
     * @param personID spriden id of the person
     * @return count of action items
     * */
    def fetchByPersonIdCount(String spridenId) {
        return MonitorActionItemReadOnly.fetchByPersonIdCount(spridenId)
    }

    /**
     * Lists of action items filtered by actionItem id and Person name
     * @param actionItem  Action Item Id of action item to be searched
     * @param personName name of the person
     * @return count of action items
     * */
    def fetchByActionItemIdAndPersonNameCount(Long actionItem, String personName) {
        return MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItem, personName)
    }

    /**
     * Get ActionItem by Id
     * @param Id of the ActionItem to be searched
     * @return MonitorActionItemReadOnly object
     */
    def findById(Long id) {
        return MonitorActionItemReadOnly.findById(id)
    }


}
