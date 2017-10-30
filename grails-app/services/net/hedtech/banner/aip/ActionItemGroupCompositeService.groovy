/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.MessageUtility
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException

import java.text.MessageFormat

/**
 * Composite Service for Action Item Group
 */

class ActionItemGroupCompositeService {

    def actionItemGroupService
    def groupFolderReadOnlyService

    /**
     * Creates a new group
     * @param map
     * @return
     */
    def createGroup( map ) {
        def success = false
        def message
        def group = new ActionItemGroup(
                title: map.groupTitle,
                name: map.groupName,
                folderId: map.folderId,
                description: map.groupDesc,
                postingInd: 'N',
                status: map.groupStatus ? (AIPConstants.STATUS_MAP.get( map.groupStatus )) : null,
                )
        def groupNew
        try {
            groupNew = actionItemGroupService.create( group )
            success = true
        } catch (ApplicationException e) {
            if (ActionItemGroupService.FOLDER_VALIDATION_ERROR.equals( e.getMessage() )) {
                message = MessageUtility.message( e.getDefaultMessage(), MessageFormat.format( "{0,number,#}", map.folderId ) )
            } else {
                message = MessageUtility.message( e.getDefaultMessage() )
            }
        }
        [
                success: success,
                message: message,
                group  : groupFolderReadOnlyService.getActionItemGroupById( groupNew.id.toInteger() )
        ]


    }
}
