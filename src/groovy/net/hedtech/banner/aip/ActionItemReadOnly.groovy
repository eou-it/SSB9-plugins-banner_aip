/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import javax.persistence.*


@NamedQueries(value = [

        @NamedQuery(name = "ActionItemReadOnly.fetchActionItemRO",
                query = """
           FROM ActionItemReadOnly a
          """),
        @NamedQuery(name = "ActionItemReadOnly.fetchActionItemROByFolder",
                query = """FROM ActionItemReadOnly a
           WHERE a.folderId = :myFolder
           """)
])
@Entity
@Table(name = "GVQ_GCBACTM")

class ActionItemReadOnly implements Serializable {

    /**
     *  ID for Action Item
     */

    @Id
    @Column(name = "ACTION_ITEM_ID")
    Long id

    /**
     * Name of the action item
     */

    @Column(name = "ACTION_ITEM_NAME")
    String name

    /**
     * ID of the action item folder
     */

    @Column(name = "ACTION_ITEM_FOLDER_ID")
    Long folderId

    /**
     * ID of the action item folder
     */

    @Column(name = "ACTION_ITEM_FOLDER_NAME")
    String folderName

    /**
     * PIDM of the user action item belongs to
     */

    @Column(name = "ACTION_ITEM_DESCRIPTION")
    String description

    /**
     * Status of action item
     */

    @Column(name = "ACTION_ITEM_STATUS", length = 30)
    String status

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


    @Override
    public String toString() {
        return "ActionItemReadOnly{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", folderId=" + folderId +
                ", folderName='" + folderName + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", activityDate=" + activityDate +
                ", userId='" + userId + '\'' +
                ", creatorId='" + creatorId + '\'' +
                ", createDate=" + createDate +
                ", version=" + version +
                '}';
    }


    boolean equals( o ) {
        if (this.is( o )) return true
        if (getClass() != o.class) return false

        ActionItemReadOnly that = (ActionItemReadOnly) o

        if (activityDate != that.activityDate) return false
        if (createDate != that.createDate) return false
        if (creatorId != that.creatorId) return false
        if (description != that.description) return false
        if (folderId != that.folderId) return false
        if (folderName != that.folderName) return false
        if (id != that.id) return false
        if (name != that.name) return false
        if (status != that.status) return false
        if (userId != that.userId) return false
        if (version != that.version) return false

        return true
    }


    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        result = 31 * result + (folderId != null ? folderId.hashCode() : 0)
        result = 31 * result + (folderName != null ? folderName.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (status != null ? status.hashCode() : 0)
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0)
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        return result
    }


    public static def fetchActionItemRO() {
        ActionItemReadOnly.withSession { session ->
            List actionItemReadOnly = session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemRO' ).list().sort { it.name }
            return actionItemReadOnly
        }
    }


    public static def fetchActionItemROByFolder( Long folderId ) {
        ActionItemReadOnly.withSession { session ->
            List actionItemReadOnly = session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemROByFolder' ).setLong( 'myFolder', folderId ).list()
            return actionItemReadOnly
        }
    }

}
