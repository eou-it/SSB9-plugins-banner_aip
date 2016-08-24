/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.service.ServiceBase


class ActionItemService extends ServiceBase {

    static final String UNIQUE_TITLE_ERROR = '@@r1:UniqueTitleInFolderError@@'
    static final String NO_TITLE_ERROR = '@@r1:TitleCanNotBeNullError@@'
    static final String FOLDER_VALIDATION_ERROR = '@@r1:FolderDoesNotExist@@'
    static final String NO_FOLDER_ERROR = '@@r1:FolderCanNotBeNullError@@'
    static final String NO_STATUS_ERROR = '@@r1:StatusCanNotBeNullError@@'
    static final String OTHER_VALIDATION_ERROR = '@@r1:ValidationError@@'


    def communicationFolderService

    //simple return of all action items
    def listActionItems() {
        return ActionItem.fetchActionItems()
    }


    def getActionItemById( Long actionItemId ) {
        return ActionItem.fetchActionItemById( actionItemId )
    }


    def preCreate( domainModelOrMap ) {
        ActionItem ai = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItem

        if (!ai.validate()) {
            def errorCodes = ai.errors.allErrors.codes[0]
            if (errorCodes.contains( 'actionItem.title.nullable' )) {
                throw new ApplicationException( ActionItem, NO_TITLE_ERROR, 'actionItem.title.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.folderId.nullable' )) {
                throw new ApplicationException( ActionItem, NO_FOLDER_ERROR, 'actionItem.folderId.nullable.error' )
            } else if (errorCodes.contains( 'actionItem.status.nullable' )) {
                throw new ApplicationException( ActionItem, NO_STATUS_ERROR, 'actionItem.status.nullable.error' )
            } else {
                throw new ApplicationException( ActionItem, OTHER_VALIDATION_ERROR, 'actionItem.operation.not.permitted' )
            }
        }

        if (!CommunicationFolder.fetchById( ai.folderId )) {
            throw new ApplicationException( ActionItem, FOLDER_VALIDATION_ERROR, 'actionItem.folder.validation.error' )
        }

        if (ActionItem.existsSameTitleInFolder( ai.folderId, ai.title )) {
            throw new ApplicationException( ActionItem, UNIQUE_TITLE_ERROR, 'actionItem.title.unique.error' )
        }
    }

}