/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.block.process

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Version



@NamedQueries(value = [
        @NamedQuery(name = "ActionItemBlockedProcess.fetchActionItemBlockedProcessList",
                query = """
           FROM ActionItemBlockedProcess a
          """),
        @NamedQuery(name = "ActionItemBlockedProcess.fetchActionItemBlockProcessById",
                query = """
           FROM ActionItemBlockedProcess a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId",
                query = """
           FROM ActionItemBlockedProcess a
           WHERE a.blockActionItemId = :myId
          """)
])

@Entity
@Table(name = "GCRABLK")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Action Item Block: Defines the process that can be blocked by an action item.
 */
class ActionItemBlockedProcess implements Serializable {

    /**
     * Surrogate ID for GCRABLK
     */

    @Id
    @Column(name = "GCRABLK_SURROGATE_ID")
    @SequenceGenerator(name = "GCRABLK_SEQ_GEN", allocationSize = 1, sequenceName = "GCRABLK_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRABLK_SEQ_GEN")
    Long id

    /**
     * ID of the action item being blocked
     */
    @Column(name = "GCRABLK_GCBACTM_ID")
    Long blockActionItemId

    /**
     * Blocked process Id
     */
    @Column(name = "GCRABLK_GCBBPRC_ID")
    Long blockedProcessId

    /**
     * Blocked Process Role
     */
    @Column(name = "GCRABLK_PERS_CODE")
    String blockedProcessRole

    /**
     * User action item pertains to
     */
    @Column(name = "GCRABLK_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRABLK_ACTIVITY_DATE")
    Date lastModified

    /**
     * Version of the action item: required for app
     */
    @Version
    @Column(name = "GCRABLK_VERSION")
    Long version

    /**
     * Data Origin column
     */
    @Column(name = "GCRABLK_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        blockActionItemId( nullable: false )
        blockedProcessId( nullable: false )
        blockedProcessRole( blank: true, nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        lastModified( nullable: true )
        dataOrigin( nullable: true, maxSize: 30 )
    }

    /**
     *
     * @return
     */
    static def fetchActionItemBlockedProcessList() {
        ActionItemBlockedProcess.withSession {session ->
            session.getNamedQuery( 'ActionItemBlockedProcess.fetchActionItemBlockedProcessList' ).list()
        }
    }

    /**
     *
     * @param actionItemBlockId
     * @return
     */
    static def fetchActionItemBlockProcessById( Long actionItemBlockId ) {
        ActionItemBlockedProcess.withSession {session ->
            session.getNamedQuery( 'ActionItemBlockedProcess.fetchActionItemBlockProcessById' )
                    .setLong( 'myId', actionItemBlockId )
                    .list()
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    static def fetchActionItemBlockProcessByActionId( Long myId ) {
        ActionItemBlockedProcess.withSession {session ->
            session.getNamedQuery( 'ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId' ).setLong( 'myId', myId ).list()
        }
    }
}
