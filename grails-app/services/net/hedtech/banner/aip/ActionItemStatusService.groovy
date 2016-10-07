/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.service.ServiceBase

class ActionItemStatusService extends ServiceBase {

    def preferredNameService

    def listActionItemStatusById( Long statusId) {
        return ActionItemStatus.fetchActionItemStatusById( statusId )
    }

    //simple return of all action items
    def listActionItemStatuses() {
        return ActionItemStatus.fetchActionItemStatuses()
    }


    def listActionItemStatusCount() {
        return ActionItemStatus.fetchActionItemStatusCount()
    }

    def listActionItemsPageSort( Map params ) {

        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending: params?.sortAscending, max: params?.max, offset: params?.offset] )

        def resultCount = listActionItemStatusCount()

        def displayName
        def person
        def personParams

        /*todo: temporary name replace until we implement a decorator for preferred name*/
        results?.each {
            person = PersonUtility.getPerson( it.actionItemStatusUserId )
            if (person) {
                personParams = [pidm:person.pidm, usage:'DEFAULT']
                displayName = preferredNameService.getPreferredName(personParams);
                it.putAt('actionItemStatusUserId', displayName)
            }
        }

        def resultMap = [
                result: results,
                length: resultCount[0]
        ]

        return resultMap

    }
}