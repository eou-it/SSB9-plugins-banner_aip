/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import org.hibernate.annotations.Type
import org.hibernate.criterion.Order

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.Table


@NamedQueries(value = [

        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount",
                query = """ select count(a.id) FROM  MonitorActionItemReadOnly a
                            where a.actionItemId = :actionItemId and
                            a.personSearchFullName like :personName
                           and a.spridenChangeInd IS NULL
                           """),

        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByPersonNameCount",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where a.personSearchFullName like :personName 
                               and a.spridenChangeInd IS NULL
                        """)
])

@Entity
@Table(name = "GVQ_GCRAMTR")
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Class for Assigned Action Item view Details
 */
class MonitorActionItemReadOnly implements Serializable {

    /**
     *  ID for GCRAACT
     */
    @Id
    @Column(name = "GCRAACT_SURROGATE_ID")
    Long id
    /**
     *  Action Item ID
     */

    @Column(name = "ACTION_ITEM_ID")
    Long actionItemId

    /**
     * Name of the action item
     */
    @Column(name = "ACTION_ITEM_NAME")
    String actionItemName

    /**
     * Group Name of the action item associated to
     */
    @Column(name = "ACTION_ITEM_GROUP_NAME")
    String actionItemGroupName

    /**
     * Name of the action item
     */
    @Column(name = "ACTION_ITEM_PIDM")
    Long pidm

    /**
     * Spriden Id of the action item associated to
     */
    @Column(name = "ACTION_ITEM_SPRIDEN_ID")
    String spridenId

    /**
     * Search Person Last Name
     */
    @Column(name = "PERSON_SEARCH_LAST_NAME")
    String personSearchLastName

    /**
     * Search Person First Name
     */
    @Column(name = "PERSON_SEARCH_FIRST_NAME")
    String personSearchFirstName

    /**
     * Search Person Middle Name
     */
    @Column(name = "PERSON_SEARCH_MI_NAME")
    String personSearchMiddleName
    /**
     * Search Person Full Name
     */
    @Column(name = "PERSON_SEARCH_FULL_NAME")
    String personSearchFullName
    /**
     * Person Display Name
     */
    @Column(name = "PERSON_DISPLAY_FULL_NAME")
    String personDisplayName
    /**
     * Change Indicator
     */
    @Column(name = "SPRIDEN_CHANGE_IND")
    String spridenChangeInd

    /**
     * Status of action item
     */
    @Column(name = "ACTION_ITEM_STATUS_NAME")
    String status

    /**
     * Last activity date for the action item
     */
    @Column(name = "ACTION_ITEM_USER_RESPONSE_DATE")
    Date responseDate

    /**
     * Display Start Date
     */
    @Column(name = "ACTION_ITEM_DISPLAY_START_DATE")
    Date displayStartDate

    /**
     * Display End Date
     */
    @Column(name = "ACTION_ITEM_DISPLAY_END_DATE")
    Date displayEndDate

    /**
     * Current response ID of the action item
     */
    @Column(name = "ACTION_ITEM_RESPONSE_ID")
    Long responseId

    /**
     * Current response text of the action item
     */
    @Column(name = "ACTION_ITEM_RESPONSE_TEXT")
    String currentResponseText

    /**
     * Review  indicator of the action item
     */
    @Type(type = "yes_no")
    @Column(name = "ACTION_ITEM_REVIEW_IND")
    Boolean reviewIndicator = false

    /**
     * Code of Review status that the action item is in.
     */
    @Column(name = "ACTION_ITEM_REVIEW_STATUS_CODE")
    String reviewStateCode
    /**
     * Attachments of the action item
     */
    @Column(name = "ACTION_ITEM_ATTACHMENTS")
    Long attachments

    /**
     * Gets the list of action items based on the filter action item ID and person name
     * @param actionItem action item ID
     * @param personName Name of the person
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */
    static def fetchByActionItemIdAndPersonName(Long actionItem, String personName,
                                                def pagingAndSortParams) {
        String nameSearchParameter = personName ? personName : ""
        nameSearchParameter = "%" + nameSearchParameter.toUpperCase() + "%"

        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            and {
                eq("actionItemId", actionItem)
                ilike("personSearchFullName", nameSearchParameter)
                isNull("spridenChangeInd")
            }
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)))
        }
    }

    /**
     * Count of action items based on the filter action item ID and person name
     * @param actionItem action item ID
     * @param personName Name of the person
     * @return count of action items
     */
    static def fetchByActionItemIdAndPersonNameCount(Long actionItem, String personName) {
        String nameSearchParameter = personName ? personName : ""
        nameSearchParameter = "%" + nameSearchParameter.toUpperCase() + "%"
        MonitorActionItemReadOnly.withSession { session ->

            session.getNamedQuery('MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount')
                    .setLong("actionItemId", actionItem)
                    .setString("personName", nameSearchParameter)
                    .uniqueResult()
        }
    }

    /**
     * Gets the list of action items based on the filter action item ID and Spriden ID
     * @param actionItem action item ID
     * @param pidm pidm of the person
     * @return List of Action items
     */
    static def fetchByActionItemAndPidm(Long actionItem, Long pidm, def pagingAndSortParams) {

        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("actionItemId", actionItem)
            eq("pidm", pidm)
            isNull("spridenChangeInd")
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)))
        }
    }

    /**
     * List of action items based on the filter action item ID only
     * @param actionItem action item ID
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */
    static def fetchByActionItemId(Long actionItem, def pagingAndSortParams) {
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("actionItemId", actionItem)
            isNull("spridenChangeInd")
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)))
        }
    }

    /**
     * Gets the list of action items based on the filter person ID
     * @param pidm pidm of the person
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */
    static def fetchByPidm(Long pidm, def pagingAndSortParams) {
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("pidm", pidm)
            isNull("spridenChangeInd")
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)))
        }
    }

    /**
     * Gets the list of action items based on the filter person's name
     * @param personName Name of the person
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */
    static def fetchByPersonName(String personName, def pagingAndSortParams) {
        String personNameParam = personName ? personName : ""
        personNameParam = "%" + personNameParam + "%"
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            ilike("personSearchFullName", personNameParam)
            isNull("spridenChangeInd")
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)))
        }
    }

    /**
     * count of  action items based on the filter person's name
     * @param personName Name of the person
     * @return count of action items
     */
    static def fetchByPersonNameCount(String personName) {
        String personNameParam = personName ? personName : ""
        personNameParam = "%" + personNameParam.toUpperCase() + "%"

        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByPersonNameCount')
                    .setString("personName", personNameParam)
                    .uniqueResult()
        }
    }
}
