/*********************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
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
    def preCreateValidation( dataMap ) {
        if (!dataMap.postingJobName) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.posting.job.name', [] ) )
        }
        if (!dataMap.actionItemGroup) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.group', [] ) )
        }
        if (!dataMap.actionItems) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.action.item', [] ) )
        }

        if (!dataMap.populationListId) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.population.name', [] ) )
        }

        if (!dataMap.displayStartDate) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.display.start.date', [] ) )
        }

        if (!dataMap.displayEndDate) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.display.end.date', [] ) )
        }
        if (!dataMap.postNow && !dataMap.scheduled) {
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.schedule', [] ) )
        }
        if (dataMap.scheduled) {
            if (!dataMap.scheduleStartDate) {
                throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.validation.no.schedule.start.date', [] ) )
            }
        }
    }


    public List findRunning() {
        return ActionItemPost.findRunning()
    }

}
