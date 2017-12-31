/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Group
 */
class ActionItemGroupService extends ServiceBase {

    boolean transactional = true

    static final String UNIQUE_NAME_ERROR = '@@r1:UniqueNameInFolderError@@'

    static final String NO_TITLE_ERROR = '@@r1:TitleCanNotBeNullError@@'
    static final String NO_NAME_ERROR = '@@r1:NameCanNotBeNullError@@'

    static final String FOLDER_VALIDATION_ERROR = '@@r1:FolderDoesNotExist@@'

    static final String NO_FOLDER_ERROR = '@@r1:FolderCanNotBeNullError@@'

    static final String NO_STATUS_ERROR = '@@r1:StatusCanNotBeNullError@@'

    static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'

    static final String OTHER_VALIDATION_ERROR = '@@r1:ValidationError@@'

    /**
     * Lists All Action Item Groups
     * @return
     */
    def listActionItemGroups() {
        ActionItemGroup.fetchActionItemGroups()
    }

    /**
     * Fetch Action Item Group for specified group Id
     * @param actionItemGroupId
     * @return
     */
    def getActionItemGroupById( Long actionItemGroupId ) {
        ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
    }

    /**
     * Check group already posted
     * @param actionItemGroupId
     * @return true/false
     */
    def checkGroupPosted( Long actionItemGroupId ) {
        def group = ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
        return group.postingInd == AIPConstants.YES_IND
    }

    /**
     * Validation before create
     * @param domainModelOrMap
     */
    def preCreate( domainModelOrMap ) {
        ActionItemGroup aig = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemGroup

        if (!aig.validate()) {
            def errorCodes = aig.errors.allErrors.codes[0]
            if (errorCodes.contains( 'actionItemGroup.name.nullable' )) {
                throw new ApplicationException( ActionItem, NO_NAME_ERROR, 'actionItemGroup.name.nullable.error' )
            }
            if (errorCodes.contains( 'actionItemGroup.title.nullable' )) {
                throw new ApplicationException( ActionItem, NO_TITLE_ERROR, 'actionItemGroup.title.nullable.error' )
            } else if (errorCodes.contains( 'actionItemGroup.folderId.nullable' )) {
                throw new ApplicationException( ActionItem, NO_FOLDER_ERROR, 'actionItemGroup.folderId.nullable.error' )
            } else if (errorCodes.contains( 'actionItemGroup.status.nullable' )) {
                throw new ApplicationException( ActionItem, NO_STATUS_ERROR, 'actionItemGroup.status.nullable.error' )
            } else if (errorCodes.contains( 'maxSize.exceeded' )) {
                throw new ApplicationException( ActionItem, MAX_SIZE_ERROR, 'actionItemGroup.max.size.error' )
            } else {
                throw new ApplicationException( ActionItemGroup, OTHER_VALIDATION_ERROR, 'operation.not.permitted' )
            }
        }
        if (!CommunicationFolder.get( aig.folderId )) {
            throw new ApplicationException( ActionItemGroup, FOLDER_VALIDATION_ERROR, 'actionItemGroup.folder.validation.error', [aig.folderId] )
        }

        if (ActionItemGroup.existsSameNameInFolder( aig.folderId, aig.name )) {
            throw new ApplicationException( ActionItemGroup, UNIQUE_NAME_ERROR, 'actionItemGroup.name.unique.error' )
        }
    }
}
