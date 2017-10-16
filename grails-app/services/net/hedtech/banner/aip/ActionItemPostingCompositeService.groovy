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
    def actionItemPostDetailService
    def actionItemService
    def actionItemGroupService
    private static final def LOGGER = Logger.getLogger( this.class )


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def addActionItemPosting( map ) {
        def user = springSecurityService.getAuthentication()?.user
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
                postingCreatorId: user.username,
                postingCurrentState: map.postNow ? PostingStateEnum.QUEUED : (map.scheduled ? PostingStateEnum.SCHEDULED : ''),
                lastModified: new Date(),
                lastModifiedBy: user.oracleUserName )
        try {
            savedActionItemPost = actionItemPostingService.create( aip )// Save base posting table
            map.actionItems.each {it ->
                ActionItemPostDetail actionItemPostDetail = new ActionItemPostDetail(
                        lastModifiedBy: user.userName,
                        lastModified: new Date(),
                        actionItemPostId: savedActionItemPost.id,
                        actionItemId: it
                )
                actionItemPostDetailService.create( actionItemPostDetail ) // Save details
                if (map.postNow) {
                    ActionItem actionItem = actionItemService.get( it )
                    actionItem.activityDate = new Date()
                    actionItem.userId = user.username
                    actionItem.postedIndicator = AIPConstants.YES_IND
                    actionItemService.update( actionItem )
                }
            }
            if (map.postNow) {
                ActionItemGroup actionItemGroup = actionItemGroupService.get( it )
                actionItemGroup.activityDate = new Date()
                actionItemGroup.userId = user.username
                actionItemGroup.postingInd = AIPConstants.YES_IND
                actionItemGroupService.update( actionItemGroup )
            }
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

