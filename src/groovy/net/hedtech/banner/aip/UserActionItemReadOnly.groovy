/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "UserActionItemReadOnly.fetchUserActionItemROByPidm",
                query = """FROM UserActionItemReadOnly a
           WHERE a.pidm = :myPidm
           """),
        @NamedQuery(name = "UserActionItemReadOnly.fetchBlockingUserActionItemROByPidm",
                query = """FROM UserActionItemReadOnly a
                   WHERE a.pidm = :myPidm
                   AND a.isBlocking is true
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
     *  ID for GCBCSRT
     */

    @Id
    @Column(name = "ACTION_ITEM_ID")
    Long id

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
    @Column(name = "ACTION_ITEM_TMPL_USER_ID", length = 30)
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
    @Column(name = "ACTION_ITEM_TMPL_CREATOR_ID", length = 30)
    String creatorIdTmpl

    /**
     * Date the action item was created
     */
    @Column(name = "ACTION_ITEM_TMPL_CREATE_DATE", length = 30)
    Date createDateTmpl

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "ACTION_ITEM_TMPL_VERSION", length = 19)
    Long versionTmpl

    /**
     * PIDM of the user action item belongs to
     */
    @Column(name = "ACTION_ITEM_PIDM", length = 9)
    Long pidm

    /**
     * Status of action item
     */
    @Column(name = "ACTION_ITEM_STATUS", length = 30)
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
    @Column(name = "ACTION_ITEM_ACTIVITY_DATE")
    Date displayStartDate

    /**
     * Display End Date
     */
    @Column(name = "ACTION_ITEM_ACTIVITY_DATE")
    Date displayEndDate

    /**
     * User action item pertains to
     */
    @Column(name = "ACTION_ITEM_USER_ID", length = 30)
    String userId

    /**
     * UserID that created the action item
     */
    @Column(name = "ACTION_ITEM_CREATOR_ID", length = 30)
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
    @Column(name = "ACTION_ITEM_VERSION", length = 19)
    Long version

    static constraints = {
        id( nullable: false, maxSize: 19 )
        title( nullable: false, maxSize: 2048 )
        name( nullable: false, maxSize: 60 )
        description( nullable: true )
        activeTmpl( nullable: false, maxSize: 1 )
        completedDate( nullable: true, maxSize: 30 )
        userId( nullable: false, maxSize: 30 )
        activityDate( nullable: false, maxSize: 30 )
        displayStartDate( nullable: false )
        displayEndDate( nullable: false )
        creatorId( nullable: true, maxSize: 30 )
        createDate( nullable: true )
        version( nullable: false )
        pidm( nullable: false, maxSize: 9 )
        status( nullable: false, maxSize: 30 )
        isBlocking( nullable: false, maxSize: 1 )
        userIdTmpl( nullable: false, maxSize: 30 )
        activityDateTmpl( nullable: false )
        creatorIdTmpl( nullable: true, maxSize: 30 )
        createDateTmpl( nullable: true )
        versionTmpl( nullable: false, maxSize: 19 )
    }

    /**
     *
     * @param pidm
     * @return
     */
    public static def fetchUserActionItemsROByPidm( Long pidm ) {
        UserActionItemReadOnly.withSession {session ->
            List<UserActionItemReadOnly> userActionItemsReadOnly = session.getNamedQuery( 'UserActionItemReadOnly.fetchUserActionItemROByPidm' )
                    .setLong(
                    'myPidm', pidm )
                    .list()
            return userActionItemsReadOnly
        }
    }

    /**
     *
     * @param pidm
     * @return
     */
    public static def fetchBlockingUserActionItemsROByPidm( Long pidm ) {
        UserActionItemReadOnly.withSession {session ->
            List<UserActionItemReadOnly> userActionItemsReadOnly = session.getNamedQuery( 'UserActionItemReadOnly' +
                                                                                                  '.fetchBlockingUserActionItemROByPidm' )
                    .setLong(
                    'myPidm', pidm )
                    .list()
            return userActionItemsReadOnly
        }
    }
}
