/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.block.process

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "BlockingProcess.fetchNonGlobalBlockingProcess",
                query = """
                    FROM BlockingProcess a where a.globalProcessIndicator = 'N' order by processName
          """),
        @NamedQuery(name = "BlockingProcess.fetchGlobalBlockingProcess",
                query = """
                            FROM BlockingProcess a where a.globalProcessIndicator = 'Y'
                  """)
])

@Entity
@Table(name = "GCBBPRC")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Process that are subject to be blocked
 */
class BlockingProcess implements Serializable {

    /**
     * Surrogate ID for GCBBPRC
     */

    @Id
    @Column(name = "GCBBPRC_SURROGATE_ID")
    @SequenceGenerator(name = "GCBBPRC_SEQ_GEN", allocationSize = 1, sequenceName = "GCBBPRC_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBBPRC_SEQ_GEN")
    Long id

    /**
     * Code of blocking process
     */
    @Column(name = "GCBBPRC_PROCESS_CODE")
    String processCode

    /**
     * Name of blocking process
     */
    @Column(name = "GCBBPRC_PROCESS_NAME")
    String processName

    /**
     * The process Owner Code
     */
    @Column(name = "GCBBPRC_GTVSYSI_CODE")
    String processOwnerCode

    /**
     * Global Indicator for Blocking process
     */
    @Column(name = "GCBBPRC_GLOBAL_PROC_IND")
    String globalProcessIndicator

    /**
     * Persona Allowed indicator for blocking process
     */
    @Column(name = "GCBBPRC_PERSONA_BLKD_ALLOWED")
    String personaAllowedIndicator

    /**
     * System Required indicator for blocking process
     */
    @Column(name = "GCBBPRC_SYSTEM_REQUIRED_IND")
    String systemRequiredIndicator

    /**
     * Blocking process pertains to
     */
    @Column(name = "GCBBPRC_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the Blocking process
     */
    @Column(name = "GCBBPRC_ACTIVITY_DATE")
    Date lastModified

    /**
     * Version of the Blocking process
     */
    @Version
    @Column(name = "GCBBPRC_VERSION")
    Long version

    /**
     * Data Origin column
     */
    @Column(name = "GCBBPRC_DATA_ORIGIN")
    String dataOrigin

    /**
     * Fetch Non Global Blocking Process
     * @return
     */
    static def fetchNonGlobalBlockingProcess() {
        BlockingProcess.withSession {session ->
            session.getNamedQuery( 'BlockingProcess.fetchNonGlobalBlockingProcess' ).list()
        }
    }

    /**
     * Fetch Global Blocking Process
     * @return
     */
    static def fetchGlobalBlockingProcess() {
        BlockingProcess.withSession {session ->
            session.getNamedQuery( 'BlockingProcess.fetchGlobalBlockingProcess' ).list()
        }
    }
}
