/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.util.Holders
import groovy.sql.Sql
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger
import org.omg.CORBA.portable.ApplicationException
import net.hedtech.banner.i18n.MessageHelper

import java.sql.SQLException
import java.util.regex.Pattern


/**
 * Composite Service class for MonitorActionItemReadOnly domain
 */
class MonitorActionItemCompositeService extends ServiceBase {
    private static final def LOGGER = Logger.getLogger(this.class)
    private static final String WILDCARD = ".*"
    def monitorActionItemReadOnlyService
    def userActionItemService
    def actionItemReviewAuditService
    def springSecurityService
    def actionItemProcessingCommonService
    def configUserPreferenceService
    def aipReviewStateService
    def actionItemService
    def preferredNameService

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
                 actionItemPersonName: getPreferredName(userActionItemDetails.pidm),
                 status              : userActionItemDetails.status,
                 responseDate        : userActionItemDetails.responseDate,
                 displayStartDate    : userActionItemDetails.displayStartDate,
                 displayEndDate      : userActionItemDetails.displayEndDate,
                 responseId          : userActionItemDetails.responseId,
                 currentResponseText : userActionItemDetails.currentResponseText,
                 reviewIndicator     : userActionItemDetails.reviewIndicator,
                 reviewAuditObject   : getRecentReviewAuditEntry(userActionItemDetails.id),
                 reviewStateObject   : [code: userActionItemDetails.reviewStateCode, name: aipReviewStateService.fetchReviewStateNameByCodeAndLocale(userActionItemDetails.reviewStateCode, getLocaleSting())],
                 attachments         : userActionItemDetails.attachments]

        return result
    }

    /**
     * List of  action item Names
     * @return
     */
    def getActionItemNames() {
        def actionItems = actionItemService.listActionItemsIdAndName()
        def actionItemNamesList = []
        actionItems.each {
            actionItemNamesList.add('id': it.getAt(0), 'name': it.getAt(1))
        }
        actionItemNamesList
    }

    /**
     * Performs search on monitor action items
     * @param actionId Id of the action item
     * @param personName Name of the person to be serached
     * @param personId banner id of the person to be searched
     * @param filterData filter data object
     * @param pagingAndSortParams pagination and sorting params object
     * @return filtered list
     * */
    def searchMonitorActionItems(Long actionId, String personName, String personId,
                                 def filterData, def pagingAndSortParams) {
        LOGGER.debug("Action ID : {$actionId} -- PersonName :{$personName} -- PersonID :{$personId}-- ${filterData} -- ${pagingAndSortParams}")
        def qryresult
        def count

        pagingAndSortParams?.sortColumn = (pagingAndSortParams?.sortColumn == 'actionItemPersonName')?'personSearchFullName':pagingAndSortParams?.sortColumn
        if (actionId && personName && !personId) {          // action id + person name combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonName(actionId, personName, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByActionItemIdAndPersonNameCount(actionId, personName)
        } else if (actionId && !personName && personId) {   //action id + person id combination
            def person = PersonUtility.getPerson(personId.toUpperCase())
            if (person) {
                qryresult = monitorActionItemReadOnlyService.fetchByActionItemAndPidm(actionId, person.pidm, pagingAndSortParams)
                count = userActionItemService.countUserActionItemByActionItemIdAndPidm(actionId, person.pidm)
            } else {
                LOGGER.debug("Person does not exist")
                qryresult = []
                count = 0
            }
        } else if (actionId && !personName && !personId) {  //only action item id combination
            qryresult = monitorActionItemReadOnlyService.fetchByActionItemId(actionId, pagingAndSortParams)
            count = userActionItemService.countUserActionItemByActionItemId(actionId)
        } else if (!actionId && personName && !personId) {  // search by person name
            qryresult = monitorActionItemReadOnlyService.fetchByPersonName(personName, pagingAndSortParams)
            count = monitorActionItemReadOnlyService.fetchByPersonNameCount(personName)
        } else if (!actionId && !personName && personId) {  // search by person id only
            def person = PersonUtility.getPerson(personId.toUpperCase())
            if (person) {
                qryresult = monitorActionItemReadOnlyService.fetchByPidm(person?.pidm, pagingAndSortParams)
                count = userActionItemService.countUserActionItemByPidm(person?.pidm)
            } else {
                LOGGER.debug("Person does not exist")
                qryresult = []
                count = 0
            }

        }

        def result = [];
        qryresult.each { it ->
            result.add([id                  : it.id,
                        actionItemId        : it.actionItemId,
                        actionItemName      : it.actionItemName,
                        actionItemGroupName : it.actionItemGroupName,
                        spridenId           : it.spridenId,
                        actionItemPersonName: getPreferredName(it.pidm),
                        status              : it.status,
                        responseDate        : it.responseDate,
                        displayStartDate    : it.displayStartDate,
                        displayEndDate      : it.displayEndDate,
                        currentResponseText : it.currentResponseText,
                        reviewIndicator     : it.reviewIndicator,
                        reviewStateCode     : aipReviewStateService.fetchReviewStateNameByCodeAndLocale(it.reviewStateCode, getLocaleSting()),
                        attachments         : it.attachments])

        }
        def resultMap = [result: filterResults(result, filterData.params.searchString),
                         length: filterData.params.searchString ? filterResults(result, filterData.params.searchString).size() : count];

        return resultMap
    }

    /**
     * Update Action Item Review
     * @param requestMap Action Item review request map
     * @return
     */
    def updateActionItemReview(requestMap) {
        boolean isActionItemReviewUpdated = false
        Map result = null
        String message = ''
        try {
            Long userActionItemId = Long.valueOf(requestMap?.userActionItemId)
            def userActionItem = userActionItemService.getUserActionItemById(userActionItemId)
            if (userActionItem) {
                if (isValuesModified(requestMap, userActionItem)) {
                    if (!validateDisplayEndDate(requestMap, userActionItem)) {
                        return [success: false, message: MessageHelper.message('aip.review.action.item.end.date.error')]
                    }
                    userActionItem.displayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.displayEndDate)
                    userActionItem.reviewStateCode = requestMap.reviewStateCode
                    userActionItemService.update(userActionItem)
                }
                createActionItemReviewAuditEntry(requestMap, userActionItem)
                isActionItemReviewUpdated = true
                message = MessageHelper.message('aip.common.save.successful')
            }
        } catch (ApplicationException e) {
            isActionItemReviewUpdated = false
            message = MessageHelper.message('aip.review.action.update.exception.error')
        }

        result = [success: isActionItemReviewUpdated, message: message]
        return result
    }

    def getReviewStatusList() {
        def reviewStateList = []
        def result = aipReviewStateService.fetchNonDefaultReviewStates(getLocaleSting())
        result.each {
            reviewStateList.add('code': it.reviewStateCode, 'name': it.reviewStateName)
        }
        return reviewStateList
    }

    /**
     * Is Display end date and Review status values are modified
     * @param requestMap Action Item reveiw request map
     * @param userActionItem user Action Item object
     * @return
     */
    private def isValuesModified(requestMap, userActionItem) {
        boolean isValuesChanged = false
        Date displayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.displayEndDate)
        if (displayEndDate.compareTo(userActionItem.displayEndDate) != 0) {
            isValuesChanged = true
        }
        if (userActionItem.reviewStateCode != requestMap.reviewStateCode) {
            isValuesChanged = true
        }
        return isValuesChanged
    }

    /**
     * Validate display end date
     * @param requestMap Action Item reveiw request map
     * @param userActionItem user Action Item object
     * @return
     */
    private def validateDisplayEndDate(requestMap, userActionItem) {
        Date displayEndDate = actionItemProcessingCommonService.convertToLocaleBasedDate(requestMap.displayEndDate)
        if (displayEndDate.compareTo(userActionItem.displayEndDate) != 0) {
            return (displayEndDate.compareTo(userActionItem.displayEndDate) > 0)
        }
        return true
    }

    /**
     * create action Item review audit entry
     * @param requestMap Action Item reveiw request map
     * @param userActionItem user Action Item object
     * @return
     */
    private void createActionItemReviewAuditEntry(requestMap, userActionItem) {
        def user = springSecurityService.getAuthentication()?.user
        ActionItemReviewAudit actionItemReviewAudit = new ActionItemReviewAudit()
        actionItemReviewAudit.userActionItemId = userActionItem.id
        actionItemReviewAudit.responseId = requestMap.responseId
        actionItemReviewAudit.reviewerPidm = user.pidm
        actionItemReviewAudit.reviewDate = new Date()
        actionItemReviewAudit.reviewStateCode = requestMap.reviewStateCode
        actionItemReviewAudit.externalCommentInd = requestMap.externalCommentInd
        actionItemReviewAudit.reviewComments = requestMap.reviewComments
        actionItemReviewAudit.contactInfo = requestMap.contactInfo ? java.net.URLDecoder.decode(requestMap.contactInfo, "UTF-8") : requestMap.contactInfo
        actionItemReviewAuditService.create(actionItemReviewAudit)
    }

    /**
     * Filters the list of results based on the search string
     * @param result List of result
     * @param searchParam search string to be searched.
     * @return filtered list
     * */
    private def filterResults(def result, def searchParam) {
        def filteredResult
        String regexPattern
        regexPattern = searchParam ? WILDCARD + Pattern.quote(searchParam.toString().toUpperCase())+ WILDCARD : WILDCARD + WILDCARD;
        filteredResult = result.findAll { it ->
            it.actionItemName.toString().toUpperCase().matches(regexPattern) ||
                    it.status.toString().toUpperCase().matches(regexPattern) ||
                    (it.currentResponseText ? it.currentResponseText?.toString() : "").toUpperCase().matches(regexPattern) ||
                    it.actionItemGroupName.toString().toUpperCase().matches(regexPattern) ||
                    it.actionItemPersonName.toString().toUpperCase().matches(regexPattern) ||
                    it.spridenId.toString().toUpperCase().matches(regexPattern) ||
                    (it.reviewStateCode ? it.reviewStateCode : "").toString().toUpperCase().matches(regexPattern)
        }
        filteredResult
    }

    /**
     * Getting user or default Locale string
     * @return Locale string
     * */
    private def getLocaleSting() {
        Locale userLocale = configUserPreferenceService.getUserLocale()
        if (!userLocale) {
            userLocale = Locale.getDefault()
        }
        userLocale.toString()
    }

    /**
     * Getting recent contact
     * @return Locale string
     * */
    private def getRecentReviewAuditEntry(Long userActionItemId) {
        def reviewAuditObject = null
        def reviewAuditObjectList = actionItemReviewAuditService.fetchReviewAuditByUserActionItemId(userActionItemId)
        if (reviewAuditObjectList && reviewAuditObjectList[0]) {
            reviewAuditObject = reviewAuditObjectList[0]
        }
        reviewAuditObject
    }

    public String getPreferredName(pidm) {
        def params = [:]
        def conn
        params.put("pidm", pidm)
        params.put("productname", "General")
        params.put("appname", "GeneralSsb")
        params.put("pagename", "Monitor Action Item")
        params.put("sectionname", "Search Results")

        if (isSsbEnabled()) {
            conn = dataSource.getSsbConnection()
            log.debug "MonitorActionItem.getPreferredName using banssuser ssb connection"
        } else {
            conn = dataSource.getConnection()
            log.debug "MonitorActionItem.getPreferredName using banproxy connection"
        }
        try {
            preferredNameService.getName(params, conn)
        }
        catch (net.hedtech.banner.exceptions.ApplicationException aex) {
            log.error "ApplicationException occurred while fetching Preferred Name with :${aex}"
        }
        finally {
            conn?.close()
        }
    }
    private def isSsbEnabled() {
        Holders.config.ssbEnabled instanceof Boolean ? Holders.config.ssbEnabled : false
    }
}
