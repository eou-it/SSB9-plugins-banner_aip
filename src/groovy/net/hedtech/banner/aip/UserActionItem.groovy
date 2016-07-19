/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

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
@Table(name = "GCRCSRS")

class UserActionItem implements Serializable {

    /**
     * Surrogate ID for GCBCSRT
     */

    @Id
    @Column(name = "GCRCSRS_SURROGATE_ID")
    @SequenceGenerator(name = "GCRCSRS_SEQ_GEN", allocationSize = 1, sequenceName = "GCRCSRS_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRCSRS_SEQ_GEN")
    Long id

    /**
     * Related ID of the action item
     */
    @Column(name = "GCRCSRS_ACTION_ITEM_ID", length = 19)
    Long actionItemId

    /**
     * PIDM of the user action item belongs to
     */
    @Column(name = "GCRCSRS_PIDM", length = 9)
    Long pidm

    /**
     * Status of action item
     */
    @Column(name = "GCRCSRS_STATUS", length = 30)
    String status

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRCSRS_COMPLETED_DATE")
    Date completedDate


    /**
     * User action item pertains to
     */
    @Column(name = "GCRCSRS_USER_ID", length = 30)
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRCSRS_ACTIVITY_DATE")
    Date activityDate

    /**
     * UserID that created the action item
     */
    @Column(name = "GCRCSRS_CREATOR_ID", length = 30)
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "GCRCSRS_CREATE_DATE", length = 30)
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCRCSRS_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCRCSRS_DATA_ORIGIN", length = 30)
    String dataOrigin

    @Override
    public String toString() {
        return "UserActionItem{" +
                "id=" + id +
                ", actionItemId=" + actionItemId +
                ", pidm=" + pidm +
                ", status='" + status + '\'' +
                ", completedDate=" + completedDate +
                ", userId='" + userId + '\'' +
                ", activityDate=" + activityDate +
                ", creatorId='" + creatorId + '\'' +
                ", createDate=" + createDate +
                ", version=" + version +
                ", dataOrigin='" + dataOrigin + '\'' +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        UserActionItem that = (UserActionItem) o

        if (actionItemId != that.actionItemId) return false
        if (activityDate != that.activityDate) return false
        if (completedDate != that.completedDate) return false
        if (createDate != that.createDate) return false
        if (creatorId != that.creatorId) return false
        if (dataOrigin != that.dataOrigin) return false
        if (id != that.id) return false
        if (pidm != that.pidm) return false
        if (status != that.status) return false
        if (userId != that.userId) return false
        if (version != that.version) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (actionItemId != null ? actionItemId.hashCode() : 0)
        result = 31 * result + (pidm != null ? pidm.hashCode() : 0)
        result = 31 * result + (status != null ? status.hashCode() : 0)
        result = 31 * result + (completedDate != null ? completedDate.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0)
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0)
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0)
        return result
    }

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


    public static def fetchUserActionItemById( Long id ) {
        UserActionItem.withSession { session ->
            UserActionItem userActionItem = session.getNamedQuery('UserActionItem.fetchUserActionItemById').setLong( 'myId', id ).list()[0]
            return userActionItem
        }
    }

    public static def fetchUserActionItemsByPidm( Long pidm ) {
        UserActionItem.withSession { session ->
            List userActionItem = session.getNamedQuery('UserActionItem.fetchUserActionItemByPidm').setLong( 'myPidm', pidm ).list()
            return userActionItem
        }
    }


}
