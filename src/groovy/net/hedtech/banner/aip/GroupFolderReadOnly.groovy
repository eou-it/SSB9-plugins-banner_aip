/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.general.CommunicationCommonUtility
import org.hibernate.criterion.Order

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
          """),
        @NamedQuery(name = "GroupFolderReadOnly.fetchGroupFolderROCount",
                query = """SELECT COUNT(a.groupId) FROM GroupFolderReadOnly a
            """
        )
])

@Entity
@Table(name = "GVQ_GCBAGRP")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)

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
     * action item group name
     */

    @Column(name = "ACTION_ITEM_GROUP_NAME")
    String groupName

    /**
     * Indicator that the action item is active
     */

    @Column(name = "ACTION_ITEM_GROUP_DESCRIPTION", columnDefinition = "TEXT")
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
     * Folder ID
     */

    @Column(name = "ACTION_ITEM_FOLDER_NAME")
    String folderName

    /**
     * Folder name
     */

    @Column(name = "ACTION_ITEM_FOLDER_ID")
    String folderId

    /**
     * Folder Description
     */
    @Column(name = "ACTION_ITEM_FOLDER_DESCRIPTION")
    String folderDesc

    /**
     *
     * @return
     */
    public static def fetchGroupFolders() {
        GroupFolderReadOnly.withSession {session ->
            List groupFolderList = session.getNamedQuery( 'GroupFolderReadOnly.fetchGroupFolders' ).list().sort {
                it.groupTitle
            }
            return groupFolderList
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public static def fetchGroupFoldersById( Long id ) {
        GroupFolderReadOnly.withSession {session ->
            List groupFolderListById = session.getNamedQuery( 'GroupFolderReadOnly.fetchGroupFoldersById' ).setLong( 'myId', id ).list()
            return groupFolderListById
        }
    }

    /**
     *
     * @return
     */
    public static def fetchGroupFolderROCount() {
        GroupFolderReadOnly.withSession {session ->
            List groupFolderReadOnlyCount = session.getNamedQuery( 'GroupFolderReadOnly.fetchGroupFolderROCount' ).list()
            return groupFolderReadOnlyCount
        }
    }

    /**
     *
     * @param filterData
     * @param pagingAndSortParams
     * @return
     */
    public static fetchWithPagingAndSortParams( filterData, pagingAndSortParams ) {
        def searchStatus = filterData?.params?.status
        def queryCriteria = GroupFolderReadOnly.createCriteria()
        def results = queryCriteria.list( max: pagingAndSortParams.max, offset: pagingAndSortParams.offset ) {
            ilike( "groupTitle", CommunicationCommonUtility.getScrubbedInput( filterData?.params?.name ) )
            order( (pagingAndSortParams.sortAscending ? Order.asc( pagingAndSortParams?.sortColumn ) : Order.desc( pagingAndSortParams?.sortColumn )).ignoreCase() )
            if (!pagingAndSortParams?.sortColumn.equals( "groupTitle" )) {
                order( Order.asc( 'groupTitle' ).ignoreCase() )
            }
        }
        return results
    }
}
