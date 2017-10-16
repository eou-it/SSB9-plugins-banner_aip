/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.common.PostingStateEnum
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Propagation

/**
 * Class for ActionItemPostingCompositeService.
 */
class ActionItemPostingCompositeService {
    static transactional = true
    def springSecurityService
    def actionItemPostingService
    private static final def LOGGER = Logger.getLogger( this.class )


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def addActionItemPosting( map ) {
        def user = springSecurityService.getAuthentication()?.user
        println user
        ActionItemPost savedActionItemPost
        def success = false
        actionItemPostingService.preCreateValidation( map )
        ActionItemPost aip = new ActionItemPost(
                populationListId: map.populationListId,
                postingActionItemGroupId: map.actionItemGroup,
                postingName: map.postingJobName,
                postingDisplayStartDate: map.displayStartDate,
                postingDisplayEndDate: map.displayEndDate,
                postingScheduleDateTime: map.scheduleStartDate,
                postingCreationDateTime: new Date(),
                populationRegenerateIndicator: AIPConstants.NO_IND,
                postingCreatorId: user.userName,
                postingCurrentState: map.postedNow ? PostingStateEnum.QUEUED : (map.scheduled ? PostingStateEnum.SCHEDULED : ''),
                lastModified: new Date(),
                lastModifiedBy: user.userName )
        try {
            savedActionItemPost = actionItemPostingService.create( aip )
            success = true
        } catch (ApplicationException e) {
            LOGGER.error( 'Error while save action item posting' )
            throw new ApplicationException( ActionItemPostingService, new BusinessLogicValidationException( 'preCreate.error.while.creating.action.item.post', [] ) )
        }
        [
                success      : success,
                newActionItem: savedActionItemPost
        ]
    }

}

