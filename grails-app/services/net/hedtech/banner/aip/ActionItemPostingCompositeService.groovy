/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
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
                /**
                 * GCBAPST_SURROGATE_ID
                 GCBAPST_POPLIST_ID
                 GCBAPST_GCBAGRP_ID
                 GCBAPST_NAME
                 GCBAPST_DELETED
                 GCBAPST_SCHEDULE_TYPE
                 GCBAPST_DISPLAY_START_DATE
                 GCBAPST_DISPLAY_END_DATE
                 GCBAPST_SCHEDULED_DATETIME
                 GCBAPST_CREATOR_ID
                 GCBAPST_CREATION_DATETIME
                 GCBAPST_RECALC_ON_POST
                 GCBAPST_CURRENT_STATE
                 GCBAPST_JOB_ID
                 GCBAPST_POPCALC_ID
                 GCBAPST_ERROR_CODE
                 GCBAPST_ERROR_TEXT
                 GCBAPST_GROUP_ID
                 GCBAPST_PARAMETER_VALUES
                 GCBAPST_ACTIVITY_DATE
                 GCBAPST_USER_ID
                 GCBAPST_VERSION
                 GCBAPST_DATA_ORIGIN
                 GCBAPST_VPDI_CODE
                 */
                populationListId: map.populationListId,
                postingActionItemGroupId: map.postingActionItemGroupId,
                postingName: map.postingName
        )
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

