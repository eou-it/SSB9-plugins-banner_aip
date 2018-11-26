/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger
import net.hedtech.banner.aip.UserActionItem
import org.omg.CORBA.portable.ApplicationException
import net.hedtech.banner.i18n.MessageHelper


/**
 * Composite Service class for MonitorActionItemReadOnly domain
 */
class MonitorActionItemCompositeService extends ServiceBase {
    private static final def LOGGER = Logger.getLogger(this.class)
    def monitorActionItemReadOnlyService
    def userActionItemService
    def actionItemReviewAuditService
    def springSecurityService
    def actionItemProcessingCommonService
    /**
     * get Action Item
     */
    def getActionItem(Long userActionItemId) {
        def userActionItemDetails = monitorActionItemReadOnlyService.findById(userActionItemId)
        def result =
        [id                  : userActionItemDetails.id,
          actionItemId        : userActionItemDetails.actionItemId,
          actionItemName      : userActionItemDetails.actionItemName,
          actionItemGroupName : userActionItemDetails.actionItemGroupName,
          spridenId           : userActionItemDetails.spridenId,
          actionItemPersonName: userActionItemDetails.actionItemPersonName,
          status              : userActionItemDetails.status,
          responseDate        : userActionItemDetails.responseDate,
          displayStartDate    : userActionItemDetails.displayStartDate,
          displayEndDate      : userActionItemDetails.displayEndDate,
          responseId          : userActionItemDetails.responseId,
          currentResponseText : userActionItemDetails.currentResponseText,
          reviewIndicator     : userActionItemDetails.reviewIndicator,
          reviewState         : userActionItemDetails.reviewState,
          attachments         : userActionItemDetails.attachments]

        return result
    }

    /**
     * List of  action item Names
     * @return
     */
    def getActionItemNames() {
        def actionItems = monitorActionItemReadOnlyService.listOfActionItemNames()
        def actionItemNamesList = []
        actionItems.each {
            actionItemNamesList.add('id': it.getAt(0), 'name': it.getAt(1))
        }
        actionItemNamesList
    }

    def searchMonitorActionItems(Long actionId, String personName, String personId,
                                 def filterData, def pagingAndSortParams) {
        LOGGER.debug("Action ID : {$actionId} -- PersonName :{$personName} -- PersonID :{$personId}-- ${filterData} -- ${pagingAndSortParams}")
        def qryresult
        def count
        if (actionId && personName && !personId) {          // action id + person name combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionId, personName, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionId, personName)
        } else if (actionId && !personName && personId) {   //action id + person id combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenId(actionId, personId, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByActionItemAndSpridenIdCount(actionId, personId)
        } else if (actionId && !personName && !personId) {  //only action item id combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemId(actionId, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByActionItemIdCount(actionId)
        } else if (!actionId && personName && !personId) {  // search by person name
            qryresult = monitorActionItemReadOnlyService.fetchByPersonName(personName, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        } else if (!actionId && !personName && personId) {  // search by person id only
            qryresult = monitorActionItemReadOnlyService.fetchByPersonId(personId, filterData, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByPersonIdCount(personId)
        }

        def result = [];

        qryresult.each { it ->
            result.add([id                  : it.id,
                        actionItemId        : it.actionItemId,
                        actionItemName      : it.actionItemName,
                        actionItemGroupName : it.actionItemGroupName,
                        spridenId           : it.spridenId,
                        actionItemPersonName: it.actionItemPersonName,
                        status              : it.status,
                        responseDate        : it.responseDate,
                        displayStartDate    : it.displayStartDate,
                        displayEndDate      : it.displayEndDate,
                        currentResponseText : it.currentResponseText,
                        reviewIndicator     : it.reviewIndicator,
                        reviewState         : it.reviewState,
                        attachments         : it.attachments])

        }

        def resultMap = [result: result,
                         length: count];

        return resultMap
    }


    /**
     * Update Action Item Review
     * @return
     */
    def updateActionItemReview(requestMap){
        boolean isActionItemReviewUpdated = false
        Map result = null
        String message  = ''
        try{
            Long userActionItemId = Long.valueOf(requestMap?.userActionItemId)
            def userActionItem = monitorActionItemReadOnlyService.findById(userActionItemId)
            if(userActionItem){
                if(isValuesModified(requestMap,userActionItem)){
                    if(!validateDisplayEndDate(requestMap,userActionItem)){
                        return [success:false,message:MessageHelper.message('aip.review.action.item.end.date.error')]
                    }
                    userActionItem.displayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayEndDate )
                    userActionItem.reviewStateId = Long.valueOf(requestMap.reviewStateId)
                    userActionItemService.update( userActionItem )
                }
                createActionItemReviewAuditEntry(requestMap,userActionItem)
                isActionItemReviewUpdated = true
                message = MessageHelper.message('aip.common.save.successful')
            }
            }catch (ApplicationException e){
                isActionItemReviewUpdated = false
                message = MessageHelper.message('aip.review.action.update.exception.error')
            }

        result = [success:isActionItemReviewUpdated,message:message]
        return result
    }



    /**
     * Is values are modified
     * @return
     */
    private def isValuesModified(requestMap,userActionItem){
        boolean isValuesChanged = false
        Date displayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayEndDate )
        if( displayEndDate.compareTo(userActionItem.displayEndDate) != 0){
            isValuesChanged = true
        }
        if(userActionItem.reviewStateId != requestMap.reviewStateId){
            isValuesChanged = true
        }
        return   isValuesChanged
    }

    /**
     * Validate display end date
     * @return
     */
    private def validateDisplayEndDate(requestMap,userActionItem){
        Date displayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate( requestMap.displayEndDate )
        Date currentDate = actionItemProcessingCommonService.getLocaleBasedCurrentDate()
        if( displayEndDate.compareTo(userActionItem.displayEndDate) != 0){
            return (displayEndDate.compareTo(userActionItem.displayEndDate) > 0 && currentDate.compareTo( displayEndDate ) > 0 )
        }
        return true
    }


    /**
     * create action Item review audit entry
     * @return
     */
    private void createActionItemReviewAuditEntry(requestMap,userActionItem){
        def user = springSecurityService.getAuthentication()?.user
        ActionItemReviewAudit actionItemReviewAudit = new ActionItemReviewAudit()
        actionItemReviewAudit.actionItemId = userActionItem.actionItemId
        actionItemReviewAudit.pidm = userActionItem.pidm
        actionItemReviewAudit.responseId =  Long.valueOf(requestMap.responseId)
        actionItemReviewAudit.reviewerPidm = user.pidm
        actionItemReviewAudit.reviewDate = new Date()
        actionItemReviewAudit.reviewDecision = Long.valueOf(requestMap.reviewStateId)
        actionItemReviewAudit.externalCommetInd = requestMap.externalCommetInd
        actionItemReviewAudit.reviewComments = requestMap.reviewComments
        actionItemReviewAudit.contactInfo = requestMap.contactInfo
        actionItemReviewAuditService.create(actionItemReviewAudit)
    }

}
