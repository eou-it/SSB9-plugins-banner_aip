/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type
import org.hibernate.criterion.Order

import javax.persistence.*

@NamedQueries(value = [

        @NamedQuery(name = "MonitorActionItemReadOnly.fetchActionItemNames",
                query = """SELECT a.actionItemId,actionItemName FROM MonitorActionItemReadOnly a
                           GROUP BY a.actionItemId,a.actionItemName"""),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName",
                query = """ select count(a.id) FROM  MonitorActionItemReadOnly a
                            where a.actionItemId = :actionItemId
                            and upper(a.actionItemPersonName) like :personName
                           """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByActionItemAndSpridenId",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where a.actionItemId = :actionItemId
                            and upper(a.spridenId) = :spridenId
                        """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByActionItemId",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where a.actionItemId = :actionItemId
                        """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByPersonId",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where upper(a.spridenId) = :personId
                        """),
        @NamedQuery(name = "MonitorActionItemReadOnly.fetchByPersonName",
                query = """ select count(a.id) FROM MonitorActionItemReadOnly a
                            where upper(a.actionItemPersonName) like :personName
                        """)
])

@Entity
@Table(name = "GVQ_GCRAMTR")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Class for Assigned Action Item view Details
 */
class MonitorActionItemReadOnly implements Serializable {

    /**
     *
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
    @Column(name = "ACTION_ITEM_STATUS")
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
     * Code of Review state that the action item is in.
     */
    @Column(name = "ACTION_ITEM_REVIEW_STATE_CDE")
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
                                                def filterData, def pagingAndSortParams) {
        String nameSearchParameter = personName ? personName : ""
        nameSearchParameter = "%" + nameSearchParameter.toUpperCase() + "%"

        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("actionItemId", actionItem)
            ilike("actionItemPersonName", nameSearchParameter)
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }

    static def fetchByActionItemIdAndPersonNameCount(Long actionItem, String personName) {
        String nameSearchParameter = personName ? personName : ""
        nameSearchParameter = "%" + nameSearchParameter.toUpperCase() + "%"
        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByActionItemIdAndPersonName')
                    .setLong("actionItemId", actionItem)
                    .setString("personName", nameSearchParameter)
                    .uniqueResult()
        }
    }

    /**
     * Gets the list of action items based on the filter action item ID and Spriden ID
     * @param actionItem action item ID
     * @param spridenId spriden ID of the person
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */

    static def fetchByActionItemAndSpridenId(Long actionItem, String spridenId,
                                             def filterData, def pagingAndSortParams) {

        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("actionItemId", actionItem)
            eq("spridenId", spridenId, [ignoreCase: true])
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }

    static def fetchByActionItemAndSpridenIdCount(Long actionItem, String spridenId) {
        String nameSpridenId = spridenId ? spridenId : ""
        nameSpridenId = nameSpridenId.toUpperCase()

        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByActionItemAndSpridenId')
                    .setLong("actionItemId", actionItem)
                    .setString("spridenId", nameSpridenId)
                    .uniqueResult()
        }
    }

    /**
     * Gets the list of action items based on the filter action item ID only
     * @param actionItem action item ID
     * @return List < MonitorActionItemReadOnly >  List of Action items
     */
    static def fetchByActionItemId(Long actionItem, def filterData, def pagingAndSortParams) {
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("actionItemId", actionItem)
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }


    static def fetchByActionItemIdCount(Long actionItem) {
        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByActionItemId')
                    .setLong("actionItemId", actionItem)
                    .uniqueResult()
        }
    }
/**
 * Gets the list of action items based on the filter person ID
 * @param spridenId spriden ID of the person
 * @return List < MonitorActionItemReadOnly >  List of Action items
 */
    static def fetchByPersonId(String spridenId, def filterData, def pagingAndSortParams) {
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            eq("spridenId", spridenId, [ignoreCase: true])
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }

    static def fetchByPersonIdCount(String spridenId) {
        String personIDparam = spridenId ? spridenId : ""
        personIDparam = personIDparam.toUpperCase()

        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByPersonId')
                    .setString("personId", personIDparam)
                    .uniqueResult()
        }
    }
/**
 * Gets the list of action items based on the filter person's name
 * @param personName Name of the person
 * @return List < MonitorActionItemReadOnly >  List of Action items
 */
    static def fetchByPersonName(String personName, def filterData, def pagingAndSortParams) {
        String personNameParam = personName ? personName : ""
        personNameParam = "%" + personNameParam + "%"
        def queryCriteria = MonitorActionItemReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            ilike("actionItemPersonName", personNameParam)
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }
    }

    static def fetchByPersonNameCount(String personName) {
        String personNameParam = personName ? personName : ""
        personNameParam = "%" + personNameParam.toUpperCase() + "%"

        MonitorActionItemReadOnly.withSession { session ->
            session.getNamedQuery('MonitorActionItemReadOnly.fetchByPersonName')
                    .setString("personName", personNameParam)
                    .uniqueResult()
        }
    }


}
