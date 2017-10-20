/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.transaction.Transactional
import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.common.PostingStateEnum
import net.hedtech.banner.aip.post.grouppost.ActionItemPost
import net.hedtech.banner.aip.post.grouppost.ActionItemPostDetail
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
    def actionItemPostService
    def actionItemPostDetailService
    def actionItemService
    def actionItemGroupService
    def actionItemProcessingCommonService
    private static final def LOGGER = Logger.getLogger( this.class )


    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def addActionItemPosting( map ) {
        def user = springSecurityService.getAuthentication()?.user
        ActionItemPost savedActionItemPost
        def success = false
        actionItemPostService.preCreateValidation( map )
        ActionItemPost aip = new ActionItemPost(
                populationListId: map.populationListId,
                postingActionItemGroupId: map.actionItemGroup,
                postingName: map.postingJobName,
                postingDisplayStartDate: actionItemProcessingCommonService.convertToLocaleBasedDate( map.displayStartDate ),
                postingDisplayEndDate: actionItemProcessingCommonService.convertToLocaleBasedDate( map.displayEndDate ),
                postingScheduleDateTime: map.schedule ? actionItemProcessingCommonService.convertToLocaleBasedDate( map.scheduleStartDate ) : null,
                postingCreationDateTime: new Date(),
                populationRegenerateIndicator: false,
                postingDeleteIndicator: false,
                postingCreatorId: user.username,
                postingCurrentState: map.postNow ? ActionItemPostExecutionState.Queued : (map.scheduled ? ActionItemPostExecutionState.Scheduled : ActionItemPostExecutionState.New),
                lastModified: new Date(),
                lastModifiedBy: user.username )
        try {
            savedActionItemPost = actionItemPostService.create( aip )// Save base posting table
            map.actionItems.each {it ->
                ActionItemPostDetail actionItemPostDetail = new ActionItemPostDetail(
                        lastModifiedBy: user.username,
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
                ActionItemGroup actionItemGroup = actionItemGroupService.get( savedActionItemPost.postingActionItemGroupId )
                actionItemGroup.activityDate = new Date()
                actionItemGroup.userId = user.username
                actionItemGroup.postingInd = AIPConstants.YES_IND
                actionItemGroupService.update( actionItemGroup )
            }
            success = true
        } catch (ApplicationException e) {
            LOGGER.error( 'Error while save action item posting' + e )
            throw new ApplicationException( ActionItemPostService, new BusinessLogicValidationException( 'preCreate.error.while.creating.action.item.post', [] ) )
        }
        [
                success      : success,
                newActionItem: savedActionItemPost
        ]
    }

}

