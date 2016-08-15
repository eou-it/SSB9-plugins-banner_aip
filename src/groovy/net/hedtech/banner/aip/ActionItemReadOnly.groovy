/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type
import org.hibernate.criterion.Order
import net.hedtech.banner.general.CommunicationCommonUtility


import javax.persistence.*


@NamedQueries(value = [

        @NamedQuery(name = "ActionItemReadOnly.fetchActionItemRO",
                query = """
           FROM ActionItemReadOnly a
          """),
        @NamedQuery(name = "ActionItemReadOnly.fetchActionItemROByFolder",
                query = """FROM ActionItemReadOnly a
           WHERE a.folderId = :myFolder
           """),
        @NamedQuery(name="ActionItemReadOnly.fetchActionItemROCount",
            query = """SELECT COUNT(a.actionItemId) FROM ActionItemReadOnly a
            """
        )

])
@Entity
@Table(name = "GVQ_GCBACTM")

class ActionItemReadOnly implements Serializable {

    /**
     *  ID for Action Item
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
    String actionItemDesc

    /**
     * Status of action item
     */

    @Column(name = "ACTION_ITEM_STATUS", length = 30)
    String actionItemStatus

    /**
     * Last activity date for the action item
     */

    @Column(name = "ACTION_ITEM_ACTIVITY_DATE")
    Date actionItemActivityDate

    /**
     * User action item pertains to
     */

    @Column(name = "ACTION_ITEM_USER_ID", length = 30)
    String actionItemUserId

    /**
     * UserID that created the action item
     */

    @Column(name = "ACTION_ITEM_CREATOR_ID", length = 30)
    String actionItemCreatorId

    /**
     * Date the action item was created
     */

    @Column(name = "ACTION_ITEM_CREATE_DATE")
    Date actionItemCreateDate

    /**
     * Version of the action item
     */

    @Version
    @Column(name = "ACTION_ITEM_VERSION", length = 19)
    Long actionItemVersion


    @Override
    public String toString() {
        return "ActionItemReadOnly{" +
                "actionItemId=" + actionItemId +
                ", actionItemName='" + actionItemName + '\'' +
                ", folderId=" + folderId +
                ", folderName='" + folderName + '\'' +
                ", actionItemDesc='" + actionItemDesc + '\'' +
                ", actionItemStatus='" + actionItemStatus + '\'' +
                ", actionItemActivityDate=" + actionItemActivityDate +
                ", actionItemUserId='" + actionItemUserId + '\'' +
                ", actionItemCreatorId='" + actionItemCreatorId + '\'' +
                ", actionItemCreateDate=" + actionItemCreateDate +
                ", actionItemVersion=" + actionItemVersion +
                '}';
    }

    boolean equals( o ) {
        if (this.is( o )) return true
        if (getClass() != o.class) return false

        ActionItemReadOnly that = (ActionItemReadOnly) o

        if (actionItemActivityDate != that.actionItemActivityDate) return false
        if (actionItemCreateDate != that.actionItemCreateDate) return false
        if (actionItemCreatorId != that.actionItemCreatorId) return false
        if (actionItemDesc != that.actionItemDesc) return false
        if (actionItemId != that.actionItemId) return false
        if (actionItemName != that.actionItemName) return false
        if (actionItemStatus != that.actionItemStatus) return false
        if (actionItemUserId != that.actionItemUserId) return false
        if (actionItemVersion != that.actionItemVersion) return false
        if (folderId != that.folderId) return false
        if (folderName != that.folderName) return false

        return true
    }


    int hashCode() {
        int result
        result = (actionItemId != null ? actionItemId.hashCode() : 0)
        result = 31 * result + (actionItemName != null ? actionItemName.hashCode() : 0)
        result = 31 * result + (folderId != null ? folderId.hashCode() : 0)
        result = 31 * result + (folderName != null ? folderName.hashCode() : 0)
        result = 31 * result + (actionItemDesc != null ? actionItemDesc.hashCode() : 0)
        result = 31 * result + (actionItemStatus != null ? actionItemStatus.hashCode() : 0)
        result = 31 * result + (actionItemActivityDate != null ? actionItemActivityDate.hashCode() : 0)
        result = 31 * result + (actionItemUserId != null ? actionItemUserId.hashCode() : 0)
        result = 31 * result + (actionItemCreatorId != null ? actionItemCreatorId.hashCode() : 0)
        result = 31 * result + (actionItemCreateDate != null ? actionItemCreateDate.hashCode() : 0)
        result = 31 * result + (actionItemVersion != null ? actionItemVersion.hashCode() : 0)
        return result
    }


    public static def fetchActionItemRO() {
        ActionItemReadOnly.withSession { session ->
            List actionItemReadOnly = session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemRO' ).list()
            return actionItemReadOnly
        }
    }


    public static def fetchActionItemROByFolder( Long folderId ) {
        ActionItemReadOnly.withSession { session ->
            List actionItemReadOnly = session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemROByFolder' ).setLong( 'myFolder', folderId ).list()
            return actionItemReadOnly
        }
    }

    public static def fetchActionItemROCount() {
        ActionItemReadOnly.withSession { session ->
            List actionItemReadOnlyCount = session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemROCount' ).list()
            return actionItemReadOnlyCount
        }
    }

    public static fetchWithPagingAndSortParams(filterData, pagingAndSortParams) {

        def ascdir = pagingAndSortParams?.sortDirection?.toLowerCase() == 'asc'
        def searchStatus = filterData?.params?.status

        def queryCriteria = ActionItemReadOnly.createCriteria()

        def results = queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {

            ilike("actionItemName", CommunicationCommonUtility.getScrubbedInput(filterData?.params?.name))

            order((ascdir ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }

        return results
    }


}
