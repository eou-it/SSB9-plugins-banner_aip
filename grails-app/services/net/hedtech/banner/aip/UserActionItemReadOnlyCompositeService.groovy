/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger

/**
 * Composite Service class for UserActionItemReadOnly domain
 */
class UserActionItemReadOnlyCompositeService extends ServiceBase {
    private static final def LOGGER = Logger.getLogger( this.class )
    def groupFolderReadOnlyService
    def userActionItemReadOnlyService
    def springSecurityService
    def actionItemContentService
    def actionItemReadOnlyService

    /**
     * Lists user specific action items
     * @return
     */
    def listActionItemByPidmWithinDate() {
        def user = springSecurityService.getAuthentication()?.user
        List<UserActionItemReadOnly> actionItems = userActionItemReadOnlyService.listActionItemByPidmWithinDate( user.pidm )

        def userGroupInfo = []
        actionItems.each {item ->
            def exist = userGroupInfo.findIndexOf {it ->
                it.id == item.actionItemGroupID
            }
            if (exist == -1) {
                def group = groupFolderReadOnlyService.getActionItemGroupById( item.actionItemGroupID )
                def newGroup = [
                        id         : group.groupId,
                        name       : group.groupName,
                        title      : group.groupTitle,
                        discription: group.groupDesc ? group.groupDesc : MessageHelper.message( "aip.placeholder.nogroups" ),
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
        [
                groups: userGroupInfo,
                header: ["title", "state", "completedDate", "description"]
        ]
    }

    /**
     * Get Group or action item detail Information
     * @param params
     * @return
     */
    def actionItemOrGroupInfo( params ) {
        def itemDetailInfo = []
        LoggerUtility.debug( LOGGER, 'Params for actionItemOrGroupInfo ' + params )
        if (params.searchType == "group") {
            def group = groupFolderReadOnlyService.getActionItemGroupById( Long.parseLong( params.groupId ) )
            if (group) {
                def groupDesc = group.groupDesc
                if (!groupDesc) {
                    groupDesc = MessageHelper.message( "aip.placeholder.nogroups" )
                }
                def groupItem = [
                        id      : group.groupId,
                        title   : group.groupTitle,
                        status  : group.groupStatus,
                        userId  : group.groupUserId,
                        text    : groupDesc,
                        activity: group.groupActivityDate,
                        version : group.groupVersion
                ]
                itemDetailInfo << groupItem
            }
        } else if (params.searchType == "actionItem") {
            def itemDetail = actionItemContentService.listActionItemContentById( Long.parseLong( params.actionItemId ) )
            def templateInfo = actionItemReadOnlyService.getActionItemROById( Long.parseLong( params.actionItemId ) )
            if (itemDetail) {
                itemDetailInfo << itemDetail
            }
            if (templateInfo) {
                itemDetailInfo << templateInfo
            }
        }
        itemDetailInfo
    }
}
