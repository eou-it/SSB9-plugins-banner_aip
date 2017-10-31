/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.converters.JSON
import net.hedtech.banner.MessageUtility
import net.hedtech.banner.service.ServiceBase

/**
 * Composite Service class for UserActionItemReadOnly domain
 */
class UserActionItemReadOnlyCompositeService extends ServiceBase {
    def groupFolderReadOnlyService
    def userActionItemReadOnlyService
    def springSecurityService

    /**
     * Lists user specific action items
     * @return
     */
    def listActionItemByPidmWithinDate() {
        def user = springSecurityService.getAuthentication()?.user
        def actionItems = userActionItemReadOnlyService.listActionItemByPidmWithinDate( user.pidm)

        def userGroupInfo = []
        actionItems.each { item ->
            def exist = userGroupInfo.findIndexOf { it ->
                it.id == item.actionItemGroupID
            }
            if (exist == -1) {
                def group = groupFolderReadOnlyService.getActionItemGroupById( item.actionItemGroupID )
                def newGroup = [
                        id         : group.groupId,
                        name       : group.groupName,
                        title      : group.groupTitle,
                        discription: group.groupDesc ? group.groupDesc : MessageUtility.message( "aip.placeholder.nogroups" ),
                        status     : group.groupStatus,
                        postInd    : group.postedInd,
                        folderName : group.folderName,
                        folderId   : group.folderId,
                        folderDesc : group.folderDesc,
                        items      : [],
                        header     : ["title", "status", "completedDate", "description"]
                ]
                newGroup.items.push( item )
                userGroupInfo.push( newGroup )
            } else {
                userGroupInfo[exist].items.push( item )
            }
        }
        //TODO:: order action items in group by seq number
        def orderBySeqNumber = new OrderBy( [{it.actionItemSequenceNumber}] )
        //userGroupInfo.items.sort( orderBySeqNumber )
        [
                groups: userGroupInfo,
                header: ["title", "state", "completedDate", "description"]
        ]
    }
}
