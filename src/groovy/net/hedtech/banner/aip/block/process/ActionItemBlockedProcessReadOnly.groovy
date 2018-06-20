/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.block.process

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemBlockedProcessReadOnly.fetchByActionItemId",
                query = """
           FROM ActionItemBlockedProcessReadOnly a
           WHERE a.id.actionItemId = :actionItemId
          """),
        @NamedQuery(name = "ActionItemBlockedProcessReadOnly.fetchByListOfActionItemIds",
                query = """
           FROM ActionItemBlockedProcessReadOnly a
           WHERE a.id.actionItemId IN :actionItemIds
          """)
])

@Entity
@Table(name = "GVQ_ACTION_ITEM_BLOCK_PROCESS")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Action Item Block process and URL: Defines the process that can be blocked by an action item.
 */
class ActionItemBlockedProcessReadOnly implements Serializable {

    /**
     * Combined ID for GVQ_ACTION_ITEM_BLOCK_PROCESS
     */

    @EmbeddedId
    ActonItemBlockProcessURLCompositeKey id

    /**
     * Action Item Name
     */
    @Column(name = "action_item_name")
    String actionItemName

    /**
     * Action item folder id
     */
    @Column(name = "action_item_folder_id")
    Long actionItemFolderId

    /**
     * Blocked Process Applicable Role
     */
    @Column(name = "blocked_process_app_role")
    String blockedProcessAppRole

    /**
     * Process global indicator
     */
    @Column(name = "process_global_proc_ind")
    String processGlobalProcInd

    /**
     * Process Owner Code
     */
    @Column(name = "process_global_proc_owner_code")
    String processOwnerCode

    /**
     * Process persona block Allowed Indicator
     */
    @Column(name = "process_persona_block_allowed")
    String processPersonaBlockAllowedInd

    /**
     * Blocked Process code
     */
    @Column(name = "process_code")
    String processCode

    /**
     * Blocked Process Name
     */
    @Column(name = "process_name")
    String processName

    /**
     * Process System Required Indicator
     */
    @Column(name = "process_system_req_ind")
    String processSystemReqInd

    /**
     * Process Url
     */
    @Column(name = "process_url")
    String processUrl

    /**
     * Process user id
     */
    @Column(name = "blocked_process_user_id")
    String lastModifiedBy

    /**
     * Last activity date for the action item
     */
    @Column(name = "blocked_process_activity_date")
    Date lastModified

    /**
     *  Version of the action item: required for app
     */
    @Column(name = "blocked_process_version")
    Long version

    /**
     *  VPDI Code of record
     */
    @Column(name = "blocked_process_vpdi_code")
    String vpdiCode

    /**
     *
     * @param actionItemId
     * @return
     */
    static def fetchByActionItemId( Long actionItemId ) {
        ActionItemBlockedProcessReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemBlockedProcessReadOnly.fetchByActionItemId' )
                    .setLong( 'actionItemId', actionItemId )
                    .list()
        }
    }
    /**
     *
     * @param actionItemId
     * @return
     */
    static def fetchByListOfActionItemIds( List actionItemIds ) {
        ActionItemBlockedProcessReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemBlockedProcessReadOnly.fetchByListOfActionItemIds' )
                    .setParameterList('actionItemIds', actionItemIds )
                    .list()
        }
    }
}

@Embeddable
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActonItemBlockProcessURLCompositeKey implements Serializable {
    @Column(name = 'action_item_id')
    Long actionItemId
    @Column(name = "action_item_blocked_process_id")
    Long blockedProcessId
    @Column(name = "blocked_process_id")
    Long blockingProcessId
    @Column(name = "process_url_id")
    Long processUrlId
}
