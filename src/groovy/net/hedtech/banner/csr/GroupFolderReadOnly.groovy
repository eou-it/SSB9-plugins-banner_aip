/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "GroupFolderReadOnly.fetchGroupFolders",
                query = """
           FROM GroupFolderReadOnly a
          """),
        @NamedQuery(name = "GroupFolderReadOnly.fetchGroupFoldersById",
                query = """
           FROM GroupFolderReadOnly a
           WHERE a.id = :myId
          """)
])

@Entity
@Table(name = "GVQ_GCBAGRP")

class GroupFolderReadOnly implements Serializable {

    /**
     *  ID for the action item group
     */

    @Id
    @Column(name = "ACTION_ITEM_GROUP_ID")
    Long groupId

    /**
     * action item group title
     */

    @Column(name = "ACTION_ITEM_GROUP_TITLE")
    String groupTitle

    /**
     * Indicator that the action item is active
     */

    @Column(name = "ACTION_ITEM_GROUP_DESC")
    String groupDesc

    /**
     * status of the action item group
     */

    @Column(name = "ACTION_ITEM_GROUP_STATUS")
    String groupStatus

    /*
    **
    * date the action item group was last modified
    */
    @Column(name = "ACTION_ITEM_GROUP_ADATE")
    Date groupActivityDate

    /**
     * userid last modified group
     */
    @Column(name = "ACTION_ITEM_GROUP_USERID")
    /*need to figure out what to limit length to for display*/
    String groupUserId

    /**
     * Version of the action item group
     */
    @Version
    @Column(name = "ACTION_ITEM_GROUP_VERSION")
    Long groupVersion


    /**
     * VPDI Code
     */

    @Column(name = "ACTION_ITEM_GROUP_VPDI_CODE")
    String groupVpdiCode

    /**
     * Folder name
     */

    @Column(name = "ACTION_ITEM_FOLDER_NAME")
    String folderName

    /**
     * Folder Description
     */
    @Column(name = "ACTION_ITEM_FOLDER_DESC")
    String folderDesc


    public String toString() {
        return "GroupFolderReadOnly{" +
                "groupId=" + groupId +
                ", groupTitle='" + groupTitle + '\'' +
                ", groupDesc='" + groupDesc + '\'' +
                ", groupStatus='" + groupStatus + '\'' +
                ", groupActivityDate=" + groupActivityDate +
                ", groupUserId='" + groupUserId + '\'' +
                ", groupVersion=" + groupVersion +
                ", groupVpdiCode='" + groupVpdiCode + '\'' +
                ", folderName='" + folderName + '\'' +
                ", folderDesc='" + folderDesc + '\'' +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        GroupFolderReadOnly that = (GroupFolderReadOnly) o

        if (folderDesc != that.folderDesc) return false
        if (folderName != that.folderName) return false
        if (groupActivityDate != that.groupActivityDate) return false
        if (groupDesc != that.groupDesc) return false
        if (groupId != that.groupId) return false
        if (groupStatus != that.groupStatus) return false
        if (groupTitle != that.groupTitle) return false
        if (groupUserId != that.groupUserId) return false
        if (groupVersion != that.groupVersion) return false
        if (groupVpdiCode != that.groupVpdiCode) return false

        return true
    }

    int hashCode() {
        int result
        result = (groupId != null ? groupId.hashCode() : 0)
        result = 31 * result + (groupTitle != null ? groupTitle.hashCode() : 0)
        result = 31 * result + (groupDesc != null ? groupDesc.hashCode() : 0)
        result = 31 * result + (groupStatus != null ? groupStatus.hashCode() : 0)
        result = 31 * result + (groupActivityDate != null ? groupActivityDate.hashCode() : 0)
        result = 31 * result + (groupUserId != null ? groupUserId.hashCode() : 0)
        result = 31 * result + (groupVersion != null ? groupVersion.hashCode() : 0)
        result = 31 * result + (groupVpdiCode != null ? groupVpdiCode.hashCode() : 0)
        result = 31 * result + (folderName != null ? folderName.hashCode() : 0)
        result = 31 * result + (folderDesc != null ? folderDesc.hashCode() : 0)
        return result
    }

    public static def fetchGroupFolders( ) {
        GroupFolderReadOnly.withSession { session ->
            List groupFolderList = session.getNamedQuery( 'GroupFolderReadOnly.fetchGroupFolders' ).list()
            return groupFolderList
        }
    }

    public static def fetchGroupFoldersById(Long id) {
        GroupFolderReadOnly.withSession { session ->
            List groupFolderListById = session.getNamedQuery( 'GroupFolderReadOnly.fetchGroupFoldersById' ).setLong( 'myId', id ).list()
            return groupFolderListById
        }
    }
}