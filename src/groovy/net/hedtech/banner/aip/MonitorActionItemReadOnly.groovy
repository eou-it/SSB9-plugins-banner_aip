/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

@NamedQueries(value = [

        @NamedQuery(name = "MonitorActionItemReadOnly.fetchActionItemNames",
                query = """SELECT actionItemId,actionItemName FROM MonitorActionItemReadOnly a
                           GROUP BY a.actionItemId,a.actionItemName""")
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
     *  ID for GCRAACT
     */

    @Id
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
    String spriden_id

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
     * Current response text of the action item
     */
    @Column(name = "ACTION_ITEM_CURR_RESP_TEXT")
    String currentResponseText

    /**
     * Review  indicator of the action item
     */
    @Type(type = "yes_no")
    @Column(name = "ACTION_ITEM_REVIEW_IND")
    Boolean reviewIndicator = false

    /**
     * Review  state of the action item
     */
    @Column(name = "ACTION_ITEM_REVIEW_STATE")
    String reviewState
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
            session.getNamedQuery( 'MonitorActionItemReadOnly.fetchActionItemNames' )
                    .list()
        }
    }
}
