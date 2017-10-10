/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.FlushMode

import javax.persistence.*
import java.rmi.activation.ActivationGroup_Stub


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemGroupAssignReadOnly.fetchActionItemGroupAssign",
                query = """
           FROM ActionItemGroupAssignReadOnly a
          """),
        @NamedQuery(name = "ActionItemGroupAssignReadOnly.fetchById",
                query = """
            FROM ActionItemGroupAssignReadOnly a 
            WHERE a.actionItemGroupAssignGroupId = :myId 
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
    Long actionItemGroupAssignId

    /**
     * Sequence Number of Assigned Action Item in group
     */
    @Column(name = "GCRAGRA_SEQ_NO")
    Long actionItemGroupAssignSeqNo

    /**
     * ID of group folder ID of ActionItem
     */
    @Column(name = "ACTION_ITEM_GCRFLDR_ID")
    Long actionItemGroupAssignActionItemFolderId

    /**
     * Name of Action Item
     */
    @Column(name = "ACTION_ITEM_NAME", length = 60)
    String actionItemGroupAssignActionItemName

    /***
     * Title of Action Item
     */

    @Column(name = "ACTION_ITEM_TITLE", length = 2048)
    String actionItemGroupAssignActionItemTitle

    /***
     * Status code of Action Item
     */
    @Column(name = "ACTION_ITEM_STATUS_CODE")
    String actonItemGroupAssignActionItemStatusCode

    /***
     * Indicator for Action Item post
     */
    @Column (name = "ACTION_ITEM_POSTED_IND")
    String actionItemGroupAssignActionItemPostInd

    /***
     * Description of Action Item
     */
    @Column (name = "ACTION_ITEM_DESCRIPTION")
    String actionItemGroupAssigneActionItemDesc

    /***
     * User ID who laste updated the ActionItem
     */
    @Column (name = "ACTION_ITEM_CREATOR_ID")
    Long actionITemGroupAssignActionItemCreatorId

    /***
     * Last date of Action Item Update
     */
    @Column (name = "ACTION_ITEM_CREATE_DATE")
    String actionItemGroupAssignActionItemCreateDate

    /***
     * Group Folder ID
     */
    @Column (name = "ACTION_ITEM_GROUP_GCRFLDR_ID")
    Long actionItemGroupAssignGroupFolderId

    /***
     * Group ID
     */
    @Column (name = "ACTION_ITEM_GROUP_ID")
    Long actionItemGroupAssignGroupId

    /***
     * Group Name
     */
    @Column (name = "ACTION_ITEM_GROUP_NAME", length = 60)
    String actionItemGroupAssignGroupName

    /***
     * Group Title
     */
    @Column (name = "ACTION_ITEM_GROUP_TITLE", length = 60)
    String actionItemGroupAssignGroupTitle

    /***
     * Status code of Group
     */
    @Column (name = "ACTION_ITEM_GROUP_STATUS_CODE")
    String actionItemGroupAssignGroupStatusCode

    /***
     * Post indicator for the group
     */
    @Column (name = "ACTION_ITEM_GROUP_POSTED_IND")
    String actionItemGroupAssignGroupPostInd


    static fetchActionItemGroupAssignROByGroupId(Long myId) {
        ActionItemGroupAssignReadOnly.withSession {session ->
            List actionItemGroupAssingROs =session.getNamedQuery ('ActionItemGroupAssignReadOnly.fetchById').setLong('myId', myId).list()
            return actionItemGroupAssingROs
        }
    }

    static def fetchActionItemGroupAssignRO() {
        ActionItemGroupAssignReadOnly.withSession {session ->
            List actionItemGroupAssinnROs = session.getNamedQuery( 'ActionItemGroupAssignReadOnly.fetchActionItemGroupAssign' ).list()
            return actionItemGroupAssinnROs
        }
    }

}
