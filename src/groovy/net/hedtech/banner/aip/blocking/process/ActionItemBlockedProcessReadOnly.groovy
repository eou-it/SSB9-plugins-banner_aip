/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.blocking.process

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemBlockedProcessReadOnly.fetchByActionItemId",
                query = """
           FROM ActionItemBlockedProcessReadOnly a
           WHERE a.id.actionItemId = :actionItemId
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
    @Column(name = "process_user_id")
    String lastModifiedBy

    /**
     * Last activity date for the action item
     */
    @Column(name = "process_activity_date")
    Date lastModified

    /**
     *  Version of the action item: required for app
     */
    @Column(name = "process_version")
    Long version

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
