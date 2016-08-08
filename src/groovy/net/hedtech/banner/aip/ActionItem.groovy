/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItem.fetchActionItems",
                query = """
           FROM ActionItem a
          """),
        @NamedQuery(name = "ActionItem.fetchActionItemById",
                query = """
           FROM ActionItem a
           WHERE a.id = :myId
          """)
])

@Entity
@Table(name = "GCBACTM")

class ActionItem implements Serializable {

    /**
     * Surrogate ID for GCBACTM
     */

    @Id
    @Column(name = "GCBACTM_SURROGATE_ID")
    @SequenceGenerator(name = "GCBACTM_SEQ_GEN", allocationSize = 1, sequenceName = "GCBACTM_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBACTM_SEQ_GEN")
    Long id

    /**
     * Name of the action item
     */
    @Column(name = "GCBACTM_NAME", length = 2048)
    String title

    /**
     * Indicator that the action item is active
     */
    @Column(name = "GCBACTM_STATUS", length = 1)
    String active

    /**
     * User action item pertains to
     */
    @Column(name = "GCBACTM_USER_ID", length = 30)
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCBACTM_ACTIVITY_DATE")
    Date activityDate

    /**
     * Description for action item
     */
    @Column(name = "GCBACTM_DESCRIPTION")
    /*need to figure out what to limit length to for display*/
    String description

    /**
     * UserID that created the action item
     */
    @Column(name = "GCBACTM_CREATOR_ID", length = 30)
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "GCBACTM_CREATE_DATE", length = 30)
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCBACTM_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCBACTM_DATA_ORIGIN", length = 30)
    String dataOrigin


    public String toString() {
        """ActionItem[
                id:$id,
                name:$title,
                state:$active,
                title:$title,
                active:$active,
                userId:$userId,
                activityDate:$activityDate,
                description:$description,
                creatorId:$creatorId
                createDate:$createDate,
                version:$version,
                dataOrigin=$dataOrigin]"""
    }


    boolean equals( o ) {
        if (this.is( o )) return true
        if (!(o instanceof ActionItem)) return false

        ActionItem that = (ActionItem) o

        if (active != that.active) return false
        if (activityDate != that.activityDate) return false
        if (createDate != that.createDate) return false
        if (creatorId != that.creatorId) return false
        if (dataOrigin != that.dataOrigin) return false
        if (description != that.description) return false
        if (title != that.title) return false
        if (userId != that.userId) return false
        if (version != that.version) return false
        if (id != that.id) return false

        return true
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
        description(nullable: true) //summary length only for now
        creatorId(nullable: true, maxSize: 30)
        createDate(nullable: true, maxSize: 30)
        version(nullable: false, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 19)
    }


    public static def fetchActionItems( ) {
        ActionItem.withSession { session ->
            List actionItem = session.getNamedQuery('ActionItem.fetchActionItems').list()
            return actionItem
        }
    }

}
