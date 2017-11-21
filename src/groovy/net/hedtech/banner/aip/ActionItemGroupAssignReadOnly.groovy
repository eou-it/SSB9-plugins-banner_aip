/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "ActionItemGroupAssignReadOnly.fetchActionItemGroupAssign",
                query = """
           FROM ActionItemGroupAssignReadOnly a
          """),
        @NamedQuery(name = "ActionItemGroupAssignReadOnly.fetchById",
                query = """
            FROM ActionItemGroupAssignReadOnly a 
            WHERE a.actionItemGroupId = :myId 
        """),
        @NamedQuery(name = "ActionItemGroupAssignReadOnly.fetchGroupLookup",
                query = """
                           select distinct actionGroupFolderId, 
                                  actionItemGroupFolderName, 
                                  actionItemGroupId, 
                                  groupName, 
                                  groupTitle 
                            FROM ActionItemGroupAssignReadOnly a
                            where a.groupStatus = 'A'
                                  and  a.actionItemStatus = 'A'
                             order by a.actionItemGroupFolderName, a.groupName
                  """),
        @NamedQuery(name = "ActionItemGroupAssignReadOnly.fetchActiveActionItemByGroupId",
                query = """ select actionItemId,
                                   actionItemName,  
                                   actionItemTitle,
                                   actionItemFolderName 
                            FROM ActionItemGroupAssignReadOnly a 
                            WHERE a.actionItemGroupId = :groupId 
                            AND a.actionItemStatus = 'A' order by a.sequenceNumber
                """)
])

@Entity
@Table(name = "GVQ_GCRAGRA")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemGroupAssignReadOnly implements Serializable {

    /**
     * ID for Action Item Group Assign
     */
    @Id
    @Column(name = "GCRAGRA_SURROGATE_ID")
    Long id

    /**
     * SEQUENCE NUMBER: Sequence Number of the Action Item within the Group association.
     */
    @Column(name = "GCRAGRA_SEQ_NO")
    Long sequenceNumber

    /**
     * ACTION ITEM ID: ID of Action Item
     */
    @Column(name = "ACTION_ITEM_ID")
    Long actionItemId

    /**
     * ACTION ITEM FOLDER ID: Foreign key reference to the folder under which this action item is organized.
     */
    @Column(name = "ACTION_ITEM_GCRFLDR_ID")
    Long actionItemFolderId

    /**
     * ACTION ITEM FOLDER NAME: Name of the folder under which this action item is organized.
     */
    @Column(name = "ACTION_ITEM_FOLDER_NAME")
    String actionItemFolderName

    /**
     * ACTION ITEM NAME: Name of the Action Item for Action Item management control.
     */
    @Column(name = "ACTION_ITEM_NAME")
    String actionItemName

    /**
     * ACTION ITEM TITLE: Title of the Action Item. This displays for the user assigned the Action Item.
     */
    @Column(name = "ACTION_ITEM_TITLE")
    String actionItemTitle

    /**
     * ACTION ITEM STATUS: Status of the Action Item. Valid values are (D)raft, (A)ctive and (I)nactive.
     */
    @Column(name = "ACTION_ITEM_STATUS_CODE")
    String actionItemStatus

    /**
     * ACTION ITEM POSTING INDICATOR: Posting status of Action Item. Valid values are (Y)es and (N)o. Default is (N)o.
     */
    @Type(type = "yes_no")
    @Column(name = "ACTION_ITEM_POSTED_IND")
    Boolean actionItemPostingIndicator

    /**
     * Action Item DESCRIPTION: Description of the Action Item.
     */
    @Column(name = "ACTION_ITEM_DESCRIPTION")
    String actionItemDescription

    /**
     * CREATOR: The Oracle user name that first created this template record.
     */
    @Column(name = "ACTION_ITEM_CREATOR_ID")
    String creator

    /**
     * CREATE DATE: The date when this template record was created.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "ACTION_ITEM_CREATE_DATE")
    Date createDate

    /**
     * ACTION ITEM GROUP ID: ID of related action item group id.
     */
    @Column(name = "ACTION_ITEM_GROUP_ID")
    Long actionItemGroupId

    /**
     * PROCESS GROUP FOLDER ID: folder id selected for the Action Item Process Group.
     */
    @Column(name = "ACTION_ITEM_GROUP_GCRFLDR_ID")
    Long actionGroupFolderId

    /**
     * ACTION ITEM GROUP FOLDER NAME: Name of the folder under which this action item grooup is organized.
     */
    @Column(name = "ACTION_ITEM_GROUP_FOLDER_NAME")
    String actionItemGroupFolderName

    /**
     * GROUP NAME: Name for the action Item Group for Group management control.
     */
    @Column(name = "ACTION_ITEM_GROUP_NAME")
    String groupName

    /**
     * GROUP TITLE: Title for the action Item Group. The title displays for the user assigned the Group.
     */
    @Column(name = "ACTION_ITEM_GROUP_TITLE")
    String groupTitle

    /**
     * GROUP STATUS: Status of the Group. Possible values (D)raft, (A)ctive and (I)nActive.
     */
    @Column(name = "ACTION_ITEM_GROUP_STATUS_CODE")
    String groupStatus

    /**
     * GROUP POSTED INDICATOR: Posting Status of Group. Possible values (N)o and (Y)es.
     */
    @Type(type = "yes_no")
    @Column(name = "ACTION_ITEM_GROUP_POSTED_IND")
    Boolean groupPostedIndicator

    /**
     *
     * @param myId
     * @return
     */
    static fetchActionItemGroupAssignROByGroupId( Long myId ) {
        ActionItemGroupAssignReadOnly.withSession {session ->
            List groupAssigned = session.getNamedQuery( 'ActionItemGroupAssignReadOnly.fetchById' ).setLong( 'myId', myId ).list()
            return groupAssigned
        }
    }

    /**
     *
     * @return
     */
    static def fetchActionItemGroupAssignRO() {
        ActionItemGroupAssignReadOnly.withSession {session ->
            List groupAssignedAll = session.getNamedQuery( 'ActionItemGroupAssignReadOnly.fetchActionItemGroupAssign' ).list()
            return groupAssignedAll
        }
    }

    /**
     * @param groupId
     * @return
     */
    static def fetchActiveActionItemByGroupId( Long groupId ) {
        ActionItemGroupAssignReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemGroupAssignReadOnly.fetchActiveActionItemByGroupId' )
                    .setLong( 'groupId', groupId ?: -1L )
                    .list()
        }
    }

    /**
     * Fetches action Groups and its active action items
     * @return
     */
    static def fetchGroupLookup() {
        GroupFolderReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemGroupAssignReadOnly.fetchGroupLookup' )
                    .list()
        }
    }
}
