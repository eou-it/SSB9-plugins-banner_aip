/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import org.hibernate.annotations.Type
import javax.persistence.*


@NamedQueries(value = [

        @NamedQuery(name = "UserActionItem.fetchActionItemById",
                query = """
           FROM UserActionItem a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "UserActionItem.fetchActionItemByPidm",
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
     * Name of the action item
     */
    @Column(name = "GCRCSRS_CSRT_ID", length = 19)
    Long csrtId

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


    public String toString() {
        """UserActionItem[
                id:$id,
                csrtId:$csrtId,
                status:$status,
                pidm:$pidm,
                completedDate:$completedDate,
                userId:$userId,
                activityDate:$activityDate,
                creatorId:$creatorId
                createDate:$createDate,
                version:$version,
                dataOrigin=$dataOrigin]"""
    }

    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0);
        return result;
    }

    static constraints = {
        id(nullable: false, maxSize: 19)
        title(nullable: false, maxSize: 2048)
        active(nullable: false, maxSize: 1)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false, maxSize: 30)
        description(nullable: true, maxSize: 4000) //summary length only for now
        creatorId(nullable: true, maxSize: 30)
        createDate(nullable: true, maxSize: 30)
        version(nullable: false, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 19)
    }


    public static def fetchUserActionItemById( Long id ) {
        UserActionItem.withSession { session ->
            def userActionItem = session.getNamedQuery('UserActionItem.fetchUserActionItemById').setLong( 'myId', theId ).list()[0]
            return userActionItem
        }
    }

    public static def fetchUserActionItemByPidm( Long pidm ) {
        UserActionItem.withSession { session ->
            def userActionItem = session.getNamedQuery('UserActionItem.fetchUserActionItemByPidm').setLong( 'myPidm',
                    pidm ).list()[0]
            return userActionItem
        }
    }

}
