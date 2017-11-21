/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.service.ServiceBase

/**
 * Manages action item group post instances.
 */
class ActionItemPostService extends ServiceBase {

    /**
     * Validation before creation of Posting
     * @param dataMap
     */
    def  preCreateValidation( dataMap ) {
        if (!dataMap) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.insufficient.request', [] ) )
        }
        if (!dataMap.postingName) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.posting.job.name', [] ) )
        }
        if (isDuplicateJobName( dataMap.postingName )) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.job.name.already.defined', [] ) )
        }
        if (!dataMap.postingActionItemGroupId) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.group', [] ) )
        }
        if (!dataMap.actionItemIds) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.action.item', [] ) )
        }

        if (!dataMap.populationId) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.population.name', [] ) )
        }

        if (!dataMap.displayStartDate) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.display.start.date', [] ) )
        }

        if (!dataMap.displayEndDate) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.display.end.date', [] ) )
        }
        if (!dataMap.postNow && !dataMap.scheduledStartDate) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.schedule', [] ) )
        }
        // TODO: intent? ended up with duplicate case. Not sure if something got lost
        //if (dataMap.scheduledStartDate) {
        //    if (!dataMap.scheduledStartDate) {
        //        throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException(
        //                'preCreate.validation.no.schedule.start.date', [] ) )
        //    }
        //}

    }

    /**
     * Checks if posting name is already present
     * @param name
     * @return
     */
    def isDuplicateJobName( postingName ) {
        ActionItemPost.checkIfJobNameAlreadyExists( postingName )
    }
}
