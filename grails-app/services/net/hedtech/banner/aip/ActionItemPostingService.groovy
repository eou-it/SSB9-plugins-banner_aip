/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Posting
 */
class ActionItemPostingService extends ServiceBase {

    /**
     * Validation before creation of Posting
     * @param dataMap
     */
    def preCreateValidation( dataMap ) {
        if (!dataMap.postingJobName) {
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.posting.job.name', [] ) )
        }
        if (!dataMap.actionItemGroup) {
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.group', [] ) )
        }
        if (!dataMap.actionItem) {
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.action.item', [] ) )
        }

        if (!dataMap.populationListId) {
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.population.name', [] ) )
        }

        if (!dataMap.displayStartDate) {
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.display.start.date', [] ) )
        }

        if (!dataMap.displayEndDate) {
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.display.end.date', [] ) )
        }
        if (!dataMap.postNow && !dataMap.schedule) {
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.schedule', [] ) )
        }
        if (dataMap.schedule) {
            if (!dataMap.scheduleStartDate) {
                throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.validation.no.schedule.start.date', [] ) )
            }
        }
    }
}
