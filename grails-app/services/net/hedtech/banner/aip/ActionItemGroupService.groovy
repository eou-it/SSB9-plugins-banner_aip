/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.service.ServiceBase


class ActionItemGroupService extends ServiceBase {

    boolean transactional = true

    static final String UNIQUE_TITLE_ERROR = '@@r1:UniqueTitleInFolderError@@'

    static final String NO_TITLE_ERROR = '@@r1:TitleCanNotBeNullError@@'

    static final String FOLDER_VALIDATION_ERROR = '@@r1:FolderDoesNotExist@@'

    static final String NO_FOLDER_ERROR = '@@r1:FolderCanNotBeNullError@@'

    static final String NO_STATUS_ERROR = '@@r1:StatusCanNotBeNullError@@'

    static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'

    static final String OTHER_VALIDATION_ERROR = '@@r1:ValidationError@@'

    //simple return of all action items
    def listActionItemGroups() {
        return ActionItemGroup.fetchActionItemGroups( )
    }

    def getActionItemGroupById(Long actionItemGroupId) {
        return ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
    }


    def preCreate( domainModelOrMap ) {
        ActionItemGroup aig = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemGroup

        if (!aig.validate()) {
            def errorCodes = aig.errors.allErrors.codes[0]
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
            throw new ApplicationException( ActionItemGroup, FOLDER_VALIDATION_ERROR, 'actionItemGroup.folder.validation.error' )
        }

        if (ActionItemGroup.existsSameTitleInFolder( aig.folderId, aig.title )) {
            throw new ApplicationException( ActionItemGroup, UNIQUE_TITLE_ERROR, 'actionItemGroup.title.unique.error' )
        }
    }
}
