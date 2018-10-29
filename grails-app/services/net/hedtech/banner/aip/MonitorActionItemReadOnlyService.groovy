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

    List<MonitorActionItemReadOnly> fetchByActionItemIdAndPersonName(Long actionItem, String personName) {
        return MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName(actionItem, personName)
    }
    /**
     * Lists of action items filtered by actionItem id and Person name
     * @param actionItem Long Action Item Id
     * @param spridenId Spriden Id of the person that is being searched
     * @return List < MonitorActionItemReadOnly >  list of actionItems
     */
        List<MonitorActionItemReadOnly> fetchByActionItemAndSpridenId(Long actionItem, String spridenId) {
        return MonitorActionItemReadOnly.fetchByActionItemAndSpridenId(actionItem, spridenId)
    }
    /**
     * Method to fetch Lists of action items filtered by actionItem id
     * @param actionItem Long Action Item Id
     * @param spridenId Spriden Id of the person that is being searched
     * @return List < MonitorActionItemReadOnly >  list of actionItems
     */

    List<MonitorActionItemReadOnly> fetchByActionItemId(Long actionItemId) {
        return MonitorActionItemReadOnly.fetchByActionItemId(actionItemId)
    }

    List<MonitorActionItemReadOnly> fetchByPersonName(String personName) {
        return MonitorActionItemReadOnly.fetchByPersonName(personName)
    }

    List<MonitorActionItemReadOnly> fetchByPersonId(String spridenId) {
        return MonitorActionItemReadOnly.fetchByPersonId(spridenId)
    }

}
