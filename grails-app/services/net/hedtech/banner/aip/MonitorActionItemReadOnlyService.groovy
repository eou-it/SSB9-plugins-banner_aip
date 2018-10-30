/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger


/**
 * Service class for MonitorActionItemReadOnly domain
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

    /**
     * Lists of action items filtered by actionItem id and Person name
     * @param Long actionItem,String personName
     * @return List < MonitorActionItemReadOnly >
     */

    List<MonitorActionItemReadOnly> fetchByActionItemIdAndPersonName(Long actionItem, String personName,def filterData,def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItem, personName,filterData,pagingAndSortParams)
    }


    /**
     * Lists of action items filtered by actionItem id and Person name
     * @param actionItem Long Action Item Id
     * @param spridenId Spriden Id of the person that is being searched
     * @return List < MonitorActionItemReadOnly >  list of actionItems
     */
        List<MonitorActionItemReadOnly> fetchByActionItemAndSpridenId(Long actionItem, String spridenId,def filterData,def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemAndSpridenId(actionItem, spridenId, filterData, pagingAndSortParams)
    }


    /**
     * returns count of Lists of action items filtered by actionItem id and Person name
     * @param actionItem Long Action Item Id
     * @param spridenId Spriden Id of the person that is being searched
     * @return List < MonitorActionItemReadOnly >  list of actionItems
     */
    def fetchByActionItemAndSpridenIdCount(Long actionItem, String spridenId) {
        return MonitorActionItemReadOnly.fetchByActionItemAndSpridenIdCount(actionItem, spridenId)
    }


    /**
     * Method to fetch Lists of action items filtered by actionItem id
     * @param actionItem Long Action Item Id
     * @param spridenId Spriden Id of the person that is being searched
     * @return List < MonitorActionItemReadOnly >  list of actionItems
     */

    List<MonitorActionItemReadOnly> fetchByActionItemId(Long actionItemId,def filterData,def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByActionItemId(actionItemId,filterData,pagingAndSortParams)
    }

    def fetchByActionItemIdCount(Long actionItemId) {
        return MonitorActionItemReadOnly.fetchByActionItemIdCount(actionItemId)
    }

    List<MonitorActionItemReadOnly> fetchByPersonName(String personName,def filterData,def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByPersonName(personName)
    }

    def fetchByPersonNameCount(String personName) {
        return MonitorActionItemReadOnly.fetchByPersonNameCount(personName)
    }

    List<MonitorActionItemReadOnly> fetchByPersonId(String spridenId,def filterData,def pagingAndSortParams) {
        return MonitorActionItemReadOnly.fetchByPersonId(spridenId)
    }

    def fetchByPersonIdCount(String spridenId) {
        return MonitorActionItemReadOnly.fetchByPersonIdCount(spridenId)
    }

    def fetchByActionItemIdAndPersonNameCount(Long actionItem, String personName){
        return MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount(actionItem, personName)
    }

}
