/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.service.ServiceBase


class ActionItemService extends ServiceBase {

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
            if (errorCodes.contains( 'actionItem.title.unique' )) {
                throw new ApplicationException( ActionItem, "@@r1:UniqueTitleInFolderError@@" )
            } else if (errorCodes.contains( 'actionItem.title.nullable' )) {
                throw new ApplicationException( ActionItem, "@@r1:TitleCanNotBeNullError@@" )
            } else {
                throw new ApplicationException( ActionItem, "@@r1:ValidationError@@" )
            }
        }

        if (!CommunicationFolder.fetchById( ai.folderId )) {
            throw new ApplicationException( ActionItem, "@@r1:FolderDoesNotExist@@" )
        }
    }

}