/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Domain
 */
class ActionItemService extends ServiceBase {

    static final String UNIQUE_NAME_FOLDER_ERROR = '@@r1:UniqueNameInFolderError@@'
    static final String NO_TITLE_ERROR = '@@r1:TitleCanNotBeNullError@@'
    static final String NO_NAME_ERROR = '@@r1:NameCanNotBeNullError@@'
    static final String FOLDER_VALIDATION_ERROR = '@@r1:FolderDoesNotExist@@'
    static final String NO_FOLDER_ERROR = '@@r1:FolderCanNotBeNullError@@'
    static final String NO_STATUS_ERROR = '@@r1:StatusCanNotBeNullError@@'
    static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'
    static final String OTHER_VALIDATION_ERROR = '@@r1:ValidationError@@'

    /**
     *
     * @return
     */
    def listActionItems() {
        ActionItem.fetchActionItems()
    }

    /**
     *
     * @param actionItemId
     * @return
     */
    def getActionItemById( Long actionItemId ) {
        ActionItem.fetchActionItemById( actionItemId )
    }


    void preCreate( domainModelOrMap ) {
        ActionItem ai = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItem

        if (!ai.validate()) {
            def errorCodes = ai.errors.allErrors.codes[0]
            if (errorCodes.contains( 'actionItem.name.nullable' )) {
                throw new ApplicationException( ActionItem, NO_NAME_ERROR, 'actionItem.name.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.title.nullable' )) {
                throw new ApplicationException( ActionItem, NO_TITLE_ERROR, 'actionItem.title.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.folderId.nullable' )) {
                throw new ApplicationException( ActionItem, NO_FOLDER_ERROR, 'actionItem.folderId.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.status.nullable' )) {
                throw new ApplicationException( ActionItem, NO_STATUS_ERROR, 'actionItem.status.nullable.error' )
            } else if (errorCodes.contains( 'maxSize.exceeded' )) {
                throw new ApplicationException( ActionItem, MAX_SIZE_ERROR, 'actionItem.max.size.error' )
            }
            throw new ApplicationException( ActionItem, OTHER_VALIDATION_ERROR, 'actionItem.operation.not.permitted' )
        }

        if (!CommunicationFolder.get( ai.folderId )) {
            throw new ApplicationException( ActionItem, FOLDER_VALIDATION_ERROR, 'actionItem.folder.validation.error' )
        }
        if (ActionItem.existsSameNameInFolder( ai.folderId, ai.name )) {
            throw new ApplicationException( ActionItem, UNIQUE_NAME_FOLDER_ERROR, 'actionItem.name.unique.error' )
        }
    }


    void validateUpdate( ai, oldFolderId ) {
        if (!ai.validate()) {
            def errorCodes = ai.errors.allErrors.codes[0]
            if (errorCodes.contains( 'actionItem.name.nullable' )) {
                throw new ApplicationException( ActionItem, NO_NAME_ERROR, 'actionItem.name.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.title.nullable' )) {
                throw new ApplicationException( ActionItem, NO_TITLE_ERROR, 'actionItem.title.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.folderId.nullable' )) {
                throw new ApplicationException( ActionItem, NO_FOLDER_ERROR, 'actionItem.folderId.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.status.nullable' )) {
                throw new ApplicationException( ActionItem, NO_STATUS_ERROR, 'actionItem.status.nullable.error' )
            } else if (errorCodes.contains( 'maxSize.exceeded' )) {
                throw new ApplicationException( ActionItem, MAX_SIZE_ERROR, 'actionItem.max.size.error' )
            }
            throw new ApplicationException( ActionItem, OTHER_VALIDATION_ERROR, 'actionItem.operation.not.permitted' )
        }
        if (!CommunicationFolder.get( ai.folderId )) {
            throw new ApplicationException( ActionItem, FOLDER_VALIDATION_ERROR, 'actionItem.folder.validation.error' )
        }
        if (oldFolderId != ai.folderId && ActionItem.existsSameNameInFolder( ai.folderId, ai.name )) {
            throw new ApplicationException( ActionItem, UNIQUE_NAME_FOLDER_ERROR, 'actionItem.name.unique.error' )
        }
    }
}
