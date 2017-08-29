/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@NamedQueries(value = [

        @NamedQuery(name = "UserActionItem.fetchUserActionItemById",
                query = """
           FROM UserActionItem a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "UserActionItem.fetchUserActionItemByPidm",
                query = """
           FROM UserActionItem a
           WHERE a.pidm = :myPidm
          """)
])

@Entity
@Table(name = "GCRAACT")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class UserActionItem implements Serializable {

    /**
     * Surrogate ID for GCBACTM
     */

    @Id
    @Column(name = "GCRAACT_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAACT_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAACT_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAACT_SEQ_GEN")
    Long id

    /**
     * Related ID of the action item
     */
    @Column(name = "GCRAACT_ACTION_ITEM_ID", length = 19)
    Long actionItemId

    /**
     * PIDM of the user action item belongs to
     */
    @Column(name = "GCRAACT_PIDM", length = 9)
    Long pidm

    /**
     * Status of action item
     */
    @Column(name = "GCRAACT_STATUS_ID", length = 19)
    String status

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRAACT_COMPLETED_DATE")
    Date completedDate

    /**
     * User action item pertains to
     */
    @Column(name = "GCRAACT_USER_ID", length = 30)
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRAACT_ACTIVITY_DATE")
    Date activityDate

    /**
     * UserID that created the action item
     */
    @Column(name = "GCRAACT_CREATOR_ID", length = 30)
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "GCRAACT_CREATE_DATE", length = 30)
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCRAACT_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCRAACT_DATA_ORIGIN", length = 30)
    String dataOrigin

    static constraints = {
        id( nullable: false, maxSize: 19 )
        actionItemId( nullable: false, maxSize: 19 )
        pidm( nullable: false, maxSize: 9 )
        status( nullable: false, maxSize: 30 )
        userId( nullable: false, maxSize: 30 )
        completedDate( nullable: true, maxSize: 30 )
        activityDate( nullable: false, maxSize: 30 )
        creatorId( nullable: true, maxSize: 30 )
        createDate( nullable: true, maxSize: 30 )
        version( nullable: false, maxSize: 19 )
        dataOrigin( nullable: true, maxSize: 19 )
    }

    /**
     *
     * @param id
     * @return
     */
    public static def fetchUserActionItemById( Long id ) {
        UserActionItem.withSession {session ->
            UserActionItem userActionItem = session.getNamedQuery( 'UserActionItem.fetchUserActionItemById' ).setLong( 'myId', id ).list()[0]
            return userActionItem
        }
    }

    /**
     *
     * @param pidm
     * @return
     */
    public static def fetchUserActionItemsByPidm( Long pidm ) {
        UserActionItem.withSession {session ->
            List userActionItem = session.getNamedQuery( 'UserActionItem.fetchUserActionItemByPidm' ).setLong( 'myPidm', pidm ).list()
            return userActionItem
        }
    }
}
