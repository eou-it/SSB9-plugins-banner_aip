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

        @NamedQuery(name = "MonitorActionItemReadOnly.fetchActionItemNames",
                query = """SELECT a.actionItemId,actionItemName FROM MonitorActionItemReadOnly a
                           GROUP BY a.actionItemId,a.actionItemName"""),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByActionItemIdAndPersonNameCount",
                query = """ select count(a.id) FROM  MonitorActionItemReadOnly a
                            where a.actionItemId = :actionItemId
                            and upper(a.actionItemPersonName) like :personName
                           """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByActionItemAndSpridenIdCount",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where a.actionItemId = :actionItemId
                            and upper(a.spridenId) = :spridenId
                        """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByActionItemIdCount",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where a.actionItemId = :actionItemId
                        """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByPersonIdCount",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where upper(a.spridenId) = :personId
                        """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByPersonNameCount",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where upper(a.actionItemPersonName) like :personName
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
     * Spriden Id of the action item associated to
     */
    @Column(name = "ACTION_ITEM_SPRIDEN_ID")
    String spridenId

    /**
     * Group Name of the action item associated to
     */
    @Column(name = "ACTION_ITEM_PERSON_NAME")
    String actionItemPersonName

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
     * Check the list of action item names
     * @param
     * @return
     */
    static def fetchActionItemNames() {
        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchActionItemNames')
                    .list()
        }
    }

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
            eq("actionItemId", actionItem)
            ilike("actionItemPersonName", nameSearchParameter)

            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn).ignoreCase() : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
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
     * @param spridenId spriden ID of the person
     * @return List of Action items
     */
    static def fetchByActionItemAndSpridenId(Long actionItem, String spridenId, def pagingAndSortParams) {

        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("actionItemId", actionItem)
            eq("spridenId", spridenId, [ignoreCase: true])
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn).ignoreCase() : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }

    static def fetchByActionItemAndSpridenIdCount(Long actionItem, String spridenId) {
        String nameSpridenId = spridenId ? spridenId : ""
        nameSpridenId = nameSpridenId.toUpperCase()

        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByActionItemAndSpridenIdCount')
                    .setLong("actionItemId", actionItem)
                    .setString("spridenId", nameSpridenId)
                    .uniqueResult()
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
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn).ignoreCase() : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }

    /**
     * count of action items based on the filter action item ID only
     * @param actionItem action item ID
     * @return count of Action items
     */
    static def fetchByActionItemIdCount(Long actionItem) {
        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByActionItemIdCount')
                    .setLong("actionItemId", actionItem)
                    .uniqueResult()
        }
    }

    /**
     * Gets the list of action items based on the filter person ID
     * @param spridenId spriden ID of the person
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */
    static def fetchByPersonId(String spridenId, def pagingAndSortParams) {
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("spridenId", spridenId, [ignoreCase: true])
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn).ignoreCase() : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }

    /**
     * count of action items based on the filter person ID
     * @param spridenId spriden ID of the person
     * @return count of Action items
     */

    static def fetchByPersonIdCount(String spridenId) {
        String personIDparam = spridenId ? spridenId : ""
        personIDparam = personIDparam.toUpperCase()

        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByPersonIdCount')
                    .setString("personId", personIDparam)
                    .uniqueResult()
        }
    }

    /**
     * Gets the list of action items based on the filter person's name
     * @param personName Name of the person
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */
    static def fetchByPersonName(String personName,  def pagingAndSortParams) {
        String personNameParam = personName ? personName : ""
        personNameParam = "%" + personNameParam + "%"
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            ilike("actionItemPersonName", personNameParam)
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn).ignoreCase() : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
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
