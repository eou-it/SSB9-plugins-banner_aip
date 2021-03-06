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
    List<MonitorActionItemReadOnly> fetchByActionItemAndPidm(Long actionItemId, Long pidm, def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemAndPidm(actionItemId, pidm, pagingAndSortParams)
    }

    /**
     * Lists of action items filtered by actionItem id
     * @param actionItemId  Action Item Id of action item to be searched
     * @return List of actionItems
     */
    List<MonitorActionItemReadOnly> fetchByActionItemId(Long actionItemId,  def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemId(actionItemId, pagingAndSortParams)
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
     * @param Pidm of the person to be searched
     * @param pagingAndSortParams pagaination and sorting parameter
     * @return List of action items
     * */
    List<MonitorActionItemReadOnly> fetchByPidm(Long pidm,  def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByPidm(pidm, pagingAndSortParams)
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
