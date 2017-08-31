/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.FlushMode

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemBlockedProcess.fetchActionItemBlockedProcessList",
                query = """
           FROM ActionItemBlockedProcess a
          """),
        @NamedQuery(name = "ActionItemBlockedProcess.fetchActionItemBlockProcessById",
                query = """
           FROM ActionItemBlockedProcess a
           WHERE a.blockId = :myId
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
class ActionItemBlockedProcess implements Serializable {

    /**
     * Surrogate ID for GCRABLK
     */

    @Id
    @Column(name = "GCRABLK_SURROGATE_ID")
    @SequenceGenerator(name = "GCRABLK_SEQ_GEN", allocationSize = 1, sequenceName = "GCRABLK_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRABLK_SEQ_GEN")
    Long blockId

    /**
     * ID of the action item being blocked
     */
    @Column(name = "GCRABLK_ACTION_ITEM_ID")
    Long blockActionItemId

    /**
     * Name of the action item blocked process config record
     */
    @Column(name = "GCRABLK_CONFIG_NAME")
    String blockConfigName

    /**
     * Type of the action item block
     */
    @Column(name = "GCRABLK_CONFIG_TYPE", columnDefinition = "default 'json/aipBlock'")
    String blockConfigType

    /**
     * User action item pertains to
     */
    @Column(name = "GCRABLK_USER_ID", length = 30)
    String blockUserId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRABLK_ACTIVITY_DATE")
    Date blockActivityDate

    /**
     * Version of the action item: required for app
     */
    @Version
    @Column(name = "GCRABLK_VERSION", length = 19)
    Long version

    /**
     * Data Origin column
     */
    @Column(name = "GCRABLK_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        blockActionItemId( blank: false, nullable: false, maxSize: 19 )
        blockConfigName( blank: false, nullable: false, maxSize: 50 )
        blockConfigType( blank: false, nullable: false, maxSize: 30 )
        blockUserId( blank: false, nullable: false, maxSize: 30 )
        blockActivityDate( blank: false, nullable: false, maxSize: 30 )
        version( nullable: true, maxSize: 19 )
        dataOrigin( nullable: true, maxSize: 30 )
    }

    /**
     *
     * @return
     */
    public static def fetchActionItemBlockedProcessList() {
        ActionItemBlockedProcess.withSession {session ->
            List<ActionItemBlockedProcess> actionItemBlockedProcessList = session.getNamedQuery( 'ActionItemBlockedProcess.fetchActionItemBlockedProcessList' ).list()
            return actionItemBlockedProcessList
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    public static def fetchActionItemBlockProcessById( Long myId ) {
        ActionItemBlockedProcess.withSession {session ->
            List<ActionItemBlockedProcess> actionItemBlockedProcess = session.getNamedQuery( 'ActionItemBlockedProcess.fetchActionItemBlockProcessById' ).setLong( 'myId', myId ).list()
            return actionItemBlockedProcess
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    public static def fetchActionItemBlockProcessByActionId( Long myId ) {
        ActionItemBlockedProcess.withSession {session ->
            List<ActionItemBlockedProcess> actionItemBlockedProcess = session.getNamedQuery( 'ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId' ).setLong( 'myId', myId ).list()
            return actionItemBlockedProcess
        }
    }


}
