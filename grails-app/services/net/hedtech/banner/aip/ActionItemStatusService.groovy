/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.service.ServiceBase
import net.hedtech.banner.i18n.MessageHelper
import org.springframework.security.core.context.SecurityContextHolder

class ActionItemStatusService extends ServiceBase {

    def listActionItemStatusById( Long statusId ) {
        return ActionItemStatus.fetchActionItemStatusById( statusId )
    }

    //simple return of all action items
    def listActionItemStatuses() {
        return ActionItemStatus.fetchActionItemStatuses()
    }


    def listActionItemStatusCount() {
        return ActionItemStatus.fetchActionItemStatusCount()
    }

    /**
     * Lists Action Item status
     * @param params
     * @return
     */
    def listActionItemsPageSort( Map params ) {
        def results = ActionItemStatus.fetchWithPagingAndSortParams(
                [params: [name: params?.filterName]],
                [sortColumn: params?.sortColumn, sortAscending: params?.sortAscending, max: params?.max, offset: params?.offset] )
        def resultCount = listActionItemStatusCount()
        results?.each {
            def person = PersonUtility.getPerson( it.actionItemStatusUserId )
            if (person) {
                it.putAt( 'actionItemStatusUserId', PersonUtility.getPerson( person.pidm as int )?.fullName )
            }
        }
        [
                result: results,
                length: resultCount[0],
                header: [
                        [name: "actionItemStatusId", title: "id", options: [visible: false, isSortable: true]],
                        [name: "actionItemStatus", title: MessageHelper.message( "aip.common.status" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemBlockedProcess", title: MessageHelper.message( "aip.common.block.process" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemSystemRequired", title: MessageHelper.message( "aip.common.system.required" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemStatusUserId", title: MessageHelper.message( "aip.common.last.updated.by" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0],
                        [name: "actionItemStatusActivityDate", title: MessageHelper.message( "aip.common.activity.date" ), options: [visible: true, isSortable: true, ascending: params.sortAscending], width: 0]
                ]
        ]
    }

    /**
     * Saved Action Item Status
     * @return
     */
    def statusSave( def title ) {
        def user = SecurityContextHolder?.context?.authentication?.principal
        if (!user.pidm) {
            throw new ApplicationException( ActionItemStatusService, new BusinessLogicValidationException( 'user.id.not.valid', [] ) )
        }
        def aipUser = PersonUtility.getPerson( user.pidm )
        ActionItemStatus status = new ActionItemStatus(
                actionItemStatus: title,
                actionItemStatusActive: 'Y',
                actionItemStatusBlockedProcess: 'N',
                actionItemStatusActivityDate: new Date(),
                actionItemStatusUserId: aipUser.bannerId,
                actionItemStatusSystemRequired: "N",
                actionItemStatusVersion: null,
                actionItemStatusDataOrigin: null
        )
        ActionItemStatus newStatus
        def success = false
        def message

        try {
            newStatus = create( status );
            success = true
        } catch (ApplicationException e) {
            throw new ApplicationException( ActionItemStatusService, new BusinessLogicValidationException( 'actionItemStatus.status.unique', [] ) )
        }
        [
                success: success,
                message: message,
                status : newStatus
        ]
    }

    /*
    def preCreate( domainModelOrMap ) {
        ActionItemStatus ais = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemStatus

        if (!ais.validate()) {
            def errorCodes = ais.errors.allErrors.codes[0]

            println errorCodes

            if (errorCodes.contains( 'actionItemStatus.unique' )) {
                throw new ApplicationException( ActionItemStatus, UNIQUE_STATUS_ERROR, 'actionItemStatus.status.unique.error' )
            }
        }
    }
    */
}
