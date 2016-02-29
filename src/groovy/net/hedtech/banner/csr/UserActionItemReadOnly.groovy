/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import org.hibernate.annotations.Type
import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "UserActionItemReadOnly.fetchUserActionItemROByPidm",
                query = """FROM UserActionItemReadOnly a
           WHERE a.pidm = :myPidm
           """)
])

@Entity
@Table(name = "GVQ_GCRCSRS")

class UserActionItemReadOnly implements Serializable {

    /**
     *  ID for GCBCSRT
     */

    @Id
    @Column(name = "ACTION_ITEM_ID")
    Long id

    /**
     * Name of the action item
     */
    @Id
    @Column(name = "ACTION_ITEM_NAME")
    String title

    /**
     * Indicator that the action item is active
     */
    @Id
    @Column(name = "ACTION_ITEM_TMPL_ACTIVE")
    String activeTmpl

    /**
     * Last activity date for the action item
     */
    @Id
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
    @Column(name = "ACTION_ITEM_DESCRIPTION")
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

    /**
     * Last activity date for the action item
     */
    @Column(name = "ACTION_ITEM_COMPLETED_DATE")
    Date completedDate


    /**
     * Last activity date for the action item
     */
    @Column(name = "ACTION_ITEM_ACTIVITY_DATE")
    Date activityDate

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



    public String toString() {
        """UserActionItemReadOnly[
                id:$id,
                title:$title,
                activeTmpl:$activeTmpl,
                activityDateTmpl:$activityDateTmpl,
                userIdTmpl:$userIdTmpl,
                description:$description,
                creatorId:$creatorIdTmpl,
                createDateTmpl:$createDateTmpl,
                versionTmpl:$versionTmpl,
                pidm:$pidm,
                status:$status,
                completedDate:$completedDate,
                activityDate:$activityDate,
                userId:$userId,
                creatorId:$creatorId
                createDate:$createDate,
                version:$version]"""
    }

    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? description.hashCode() : 0);
        result = 31 * result + (pidm != null ? description.hashCode() : 0);
        result = 31 * result + (activeTmpl != null ? active.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (userIdTmpl != null ? version.hashCode() : 0);
        result = 31 * result + (activityDateTmpl != null ? version.hashCode() : 0);
        result = 31 * result + (creatorIdTmpl != null ? version.hashCode() : 0);
        result = 31 * result + (createDateTmpl != null ? version.hashCode() : 0);
        result = 31 * result + (versionTmpl != null ? version.hashCode() : 0);

        return result;
    }

    static constraints = {
        id(nullable: false, maxSize: 19)
        title(nullable: false, maxSize: 2048)
        description(nullable: true)
        activeTmpl(nullable: false, maxSize: 1)
        completedDate(nullable: true, maxSize: 30)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false, maxSize: 30)
        creatorId(nullable: true, maxSize: 30)
        createDate(nullable: true, maxSize: 30)
        version(nullable: false, maxSize: 19)
        pidm(nullable: false, maxSize: 9)
        status(nullable: false, maxSize: 30)
        userIdTmpl(nullable: false, maxSize: 30)
        activityDateTmpl(nullable: false, maxSize: 30)
        creatorIdTmpl(nullable: true, maxSize: 30)
        createDateTmpl(nullable: true, maxSize: 30)
        versionTmpl(nullable: false, maxSize: 19)
    }


    public static def fetchUserActionItemROByPidm( Long pidm ) {
        UserActionItemReadOnly.withSession { session ->
            List userActionItemReadOnly = session.getNamedQuery('UserActionItemReadOnly.fetchUserActionItemROByPidm').setLong( 'myPidm', pidm ).list()
            return userActionItemReadOnly
        }
    }

}
