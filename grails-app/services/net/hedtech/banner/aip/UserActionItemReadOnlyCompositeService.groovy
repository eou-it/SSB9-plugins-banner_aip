/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger

/**
 * Composite Service class for UserActionItemReadOnly domain
 */
@Transactional
@Slf4j
class UserActionItemReadOnlyCompositeService extends ServiceBase {
    def groupFolderReadOnlyService
    def userActionItemReadOnlyService
    def springSecurityService
    def actionItemContentService
    def actionItemReadOnlyService
    def actionItemStatusRuleService
    def actionItemBlockedProcessReadOnlyService
    def configUserPreferenceService
    def aipReviewStateService


    /**
     * Lists user specific action items
     * @return
     */
    def listActionItemByPidmWithinDate() {
        def user = springSecurityService.getAuthentication()?.user
        Locale userLocale = configUserPreferenceService.getUserLocale()
        if(!userLocale){
            userLocale = Locale.getDefault()
        }
        def actionItems = userActionItemReadOnlyService.listActionItemByPidmWithinDate( user.pidm )
        def actionItemIds = actionItems.collect{it.id}
        actionItems = actionItems.collect {UserActionItemReadOnly it ->
            def reviewState = aipReviewStateService.fetchReviewStateNameByCodeAndLocale(it.reviewStateCode,userLocale.toString())
            if(it.reviewStateCode && !reviewState) {
                reviewState = MessageHelper.message( 'aip.review.status.text.unavailable' )
            }

            [
                    id                      : it.id,
                    version                 : it.version,
                    actionItemGroupID       : it.actionItemGroupID,
                    actionItemGroupName     : it.actionItemGroupName,
                    actionItemGroupTitle    : it.actionItemGroupTitle,
                    actionItemSequenceNumber: it.actionItemSequenceNumber,
                    activeTmpl              : it.activeTmpl,
                    activityDate            : it.activityDate,
                    activityDateTmpl        : it.activityDateTmpl,
                    completedDate           : it.completedDate,
                    createDate              : it.createDate,
                    createDateTmpl          : it.createDateTmpl,
                    creatorId               : it.creatorId,
                    creatorIdTmpl           : it.creatorIdTmpl,
                    description             : it.description,
                    displayEndDate          : it.displayEndDate,
                    displayStartDate        : it.displayStartDate,
                    isBlocking              : it.isBlocking,
                    name                    : it.name,
                    pidm                    : it.pidm,
                    status                  : it.status,
                    title                   : it.title,
                    userId                  : it.userId,
                    userIdTmpl              : it.userIdTmpl,
                    currentResponse         : it.completedDate ? it.currentResponseText : null,
                    currentReviewState      : reviewState,
                    currentContact          : it.reviewContact ,
                    currentComment          : it.reviewComment
            ]
        }
        def haltProcesses = []
        if(actionItemIds){
            haltProcesses =  actionItemBlockedProcessReadOnlyService.fetchByListOfActionItemIds(actionItemIds)
        }

        def userGroupInfo = []
        actionItems.each {item ->
            def haltProcessNamesList = haltProcesses.findAll{it[0] == item.id}.collect{it[1]}
            def groupHalted = false
            if(haltProcessNamesList){
                item.haltProcesses = haltProcessNamesList
                item.actionItemHalted = true
                groupHalted = true
            }
            def exist = userGroupInfo.findIndexOf {it ->
                it.id == item.actionItemGroupID
            }
            if (exist == -1) {
                def group = groupFolderReadOnlyService.getActionItemGroupById( item.actionItemGroupID )
                def newGroup = [
                        id         : group.groupId,
                        name       : group.groupName,
                        title      : group.groupTitle,
                        discription: group.groupDesc ? group.groupDesc : MessageHelper.message( 'aip.placeholder.nogroups' ),
                        status     : group.groupStatus,
                        postInd    : group.postedInd,
                        folderName : group.folderName,
                        folderId   : group.folderId,
                        folderDesc : group.folderDesc,
                        items      : [],
                        header     : ['title', 'status', 'completedDate', 'description'],
                        groupHalted: groupHalted
                ]
                newGroup.items.push( item )
                userGroupInfo.push( newGroup )
            } else {
                userGroupInfo[exist].items.push( item )
                if(item.actionItemHalted){
                    userGroupInfo[exist].groupHalted = groupHalted
                }
            }
        }
        [
                groups: userGroupInfo,
                header: ['title', 'state', 'completedDate', 'description']
        ]
    }

    /**
     * Get Group or action item detail Information
     * @param params
     * @return
     */
    def actionItemOrGroupInfo( params ) {
        def itemDetailInfo = []
        log.debug(  'Params for actionItemOrGroupInfo ' + params )
        if (params.searchType == 'group') {
            def group = groupFolderReadOnlyService.getActionItemGroupById( Long.parseLong( params.groupId ) )
            if (group) {
                def groupDesc = group.groupDesc
                if (!groupDesc) {
                    groupDesc = MessageHelper.message( 'aip.placeholder.nogroups' )
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
        } else if (params.searchType == 'actionItem') {

            def itemDetail = actionItemContentService.listActionItemContentById( Long.parseLong( params.actionItemId ) )
            def templateInfo = actionItemReadOnlyService.getActionItemROById( Long.parseLong( params.actionItemId ) )
            def actionItem=[]
            def actionItemContent=[]
            if (itemDetail) {
                  actionItem=[
                          id:itemDetail.id,
                          actionItemId:itemDetail.actionItemId,
                          text:itemDetail.text,
                          actionItemTemplateId:itemDetail.actionItemTemplateId,
                          lastModifiedBy:itemDetail.lastModifiedBy,
                          version:itemDetail.version
                  ]
                itemDetailInfo << actionItem
            }

            if (templateInfo) {
                actionItemContent=[
                        actionItemId:templateInfo.actionItemId,
                        actionItemName:templateInfo.actionItemName,
                        actionItemTitle:templateInfo.actionItemTitle,
                        folderId:templateInfo.folderId,
                        folderName:templateInfo.folderName,
                        folderDesc:templateInfo.folderDesc,
                        actionItemDesc:templateInfo.actionItemDesc,
                        actionItemStatus:templateInfo.actionItemStatus,
                        actionItemPostedStatus:templateInfo.actionItemPostedStatus,
                        actionItemActivityDate:templateInfo.actionItemActivityDate,
                        actionItemUserId:templateInfo.actionItemUserId,
                        actionItemContentUserId:templateInfo.actionItemContentUserId,
                        actionItemCreatorId:templateInfo.actionItemCreatorId,
                        actionItemCompositeDate:templateInfo.actionItemCompositeDate,
                        actionItemLastUserId:templateInfo.actionItemLastUserId,
                        actionItemVersion:templateInfo.actionItemVersion,
                        actionItemTemplateId:templateInfo.actionItemTemplateId,
                        actionItemTemplateName:templateInfo.actionItemTemplateName,
                        actionItemTemplateDesc:templateInfo.actionItemTemplateDesc,
                        actionItemPageName:templateInfo.actionItemPageName,
                        actionItemContentId:templateInfo.actionItemContentId,
                        actionItemContentDate:templateInfo.actionItemContentDate,
                        actionItemContent:templateInfo.actionItemContent,
                ]
                itemDetailInfo << actionItemContent
            }
        }

    }
}
