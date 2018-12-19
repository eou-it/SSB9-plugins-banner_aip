/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "UserActionItemReadOnly.fetchUserActionItemsROByPidmDate",
                query = """FROM UserActionItemReadOnly a
                    WHERE a.pidm = :myPidm
                    AND trunc(CURRENT_DATE) BETWEEN trunc(a.displayStartDate) AND trunc(a.displayEndDate)
                    ORDER BY a.actionItemGroupName,  a.actionItemSequenceNumber
                    """),
        @NamedQuery(name = "UserActionItemReadOnly.checkIfUserActionItemsPresentByPidmAndCurrentDate",
                query = """select count (id) FROM UserActionItemReadOnly a
                            WHERE a.pidm = :myPidm
                             AND trunc(CURRENT_DATE) BETWEEN trunc(a.displayStartDate) AND trunc(a.displayEndDate)
                            """)
])

@Entity
@Table(name = "GVQ_GCRAACT")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Class for Assigned Action Item view
 */
class UserActionItemReadOnly implements Serializable {

    /**
     *  ID for GCRAACT
     */

    @Id
    @Column(name = "ACTION_ITEM_ID")
    Long id

    @Column(name = "ACTION_ITEM_SEQ_NUMBER")
    Long actionItemSequenceNumber

    /**
     * Title of the action item
     */
    @Column(name = "ACTION_ITEM_TITLE")
    String title

    /**
     * Name of the action item
     */
    @Column(name = "ACTION_ITEM_NAME")
    String name

    /**
     * Group Id of the action item associated to
     */
    @Column(name = "ACTION_ITEM_GROUP_ID")
    Long actionItemGroupID

    /**
     * Group Name of the action item associated to
     */
    @Column(name = "ACTION_ITEM_GROUP_NAME")
    String actionItemGroupName

    /**
     * Group Title of the action item associated to
     */
    @Column(name = "ACTION_ITEM_GROUP_TITLE")
    String actionItemGroupTitle
    /**
     * Indicator that the action item is active
     */
    @Column(name = "ACTION_ITEM_TMPL_STATUS")
    String activeTmpl

    /**
     * Last activity date for the action item
     */
    @Column(name = "ACTION_ITEM_TMPL_ACTIVITY_DATE")
    Date activityDateTmpl

    /*
    **
    * User action item pertains to
    */
    @Column(name = "ACTION_ITEM_TMPL_USER_ID")
    String userIdTmpl

    /**
     * Description for action item
     */
    @Column(name = "ACTION_ITEM_DESCRIPTION", columnDefinition = "TEXT")
    /*need to figure out what to limit length to for display*/
    String description

    /**
     * UserID that created the action item
     */
    @Column(name = "ACTION_ITEM_TMPL_CREATOR_ID")
    String creatorIdTmpl

    /**
     * Date the action item was created
     */
    @Column(name = "ACTION_ITEM_TMPL_CREATE_DATE")
    Date createDateTmpl

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "ACTION_ITEM_TMPL_VERSION")
    Long versionTmpl

    /**
     * PIDM of the user action item belongs to
     */
    @Column(name = "ACTION_ITEM_PIDM")
    Long pidm

    /**
     * Status of action item
     */
    @Column(name = "ACTION_ITEM_STATUS_ID")
    Long statusId

    /**
     * Status of action item
     */
    @Column(name = "ACTION_ITEM_STATUS")
    String status

    @Type(type = "yes_no")
    @Column(name = "ACTION_ITEM_IS_BLOCKING")
    Boolean isBlocking = false

    /**
     * Last activity date for the action item
     */
    @Column(name = "ACTION_ITEM_USER_RESPONSE_DATE")
    Date completedDate

    /**
     * Last activity date for the action item
     */
    @Column(name = "ACTION_ITEM_ACTIVITY_DATE")
    Date activityDate

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
     * User action item pertains to
     */
    @Column(name = "ACTION_ITEM_USER_ID")
    String userId

    /**
     * UserID that created the action item
     */
    @Column(name = "ACTION_ITEM_CREATOR_ID")
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "ACTION_ITEM_CREATE_DATE")
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "ACTION_ITEM_VERSION")
    Long version

    /**
     * Current response text of the action item
     */
    @Column(name = "ACTION_ITEM_RESPONSE_TEXT")
    String currentResponseText

    /**
     * Review state name of the action item
     */
    @Column(name = "ACTION_ITEM_REVIEW_STATUS_CODE")
    String reviewStateCode

    /**
     * Review comment of an action item
     */
    @Column(name = "ACTION_ITEM_REVIEW_COMMENT")
    String reviewComment

    /**
     * Review contact information of an action item
     */
    @Column(name = "ACTION_ITEM_REVIEW_CONTACT")
    String reviewContact
    /**
     * Lists user specific action items
     * @param pidm
     * @return
     */
    static def fetchUserActionItemsROByPidmDate( Long pidm ) {
        UserActionItemReadOnly.withSession {session ->
            session.getNamedQuery( 'UserActionItemReadOnly.fetchUserActionItemsROByPidmDate' )
                    .setLong( 'myPidm', pidm )
                    .list()
        }
    }

    /**
     * Check if Lists of user specific action items present
     * @param pidm
     * @return
     */
    static def checkIfActionItemPresent( Long pidm ) {
        UserActionItemReadOnly.withSession {session ->
            session.getNamedQuery( 'UserActionItemReadOnly.checkIfUserActionItemsPresentByPidmAndCurrentDate' )
                    .setLong( 'myPidm', pidm )
                    .uniqueResult() > 0
        }
    }

}
