/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "ActionItemTemplate.fetchActionItemTemplates",
            query = """
            FROM ActionItemTemplate a
            """),
        @NamedQuery(name = "ActionItemTemplate.fetchActionItemTemplateById",
        query = """
        FROM ActionItemTemplate a
        WHERE a.id = :myId
        """)
])

@Entity
@Table(name = "GCBPBTR")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemTemplate implements Serializable{

    /**
     * Surrogate ID for GVBPBTR
     */

    @Id
    @Column(name = "GCBPBTR_SURROGATE_ID")
    @SequenceGenerator(name = "GCBPBTR_SEQ_GEN", allocationSize = 1, sequenceName = "GCBPBTR_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBPBTR_SEQ_GEN")
    Long id


    /**
     * Name of the action item template
     */
    @Column(name = "GCBPBTR_TEMPLATE_NAME")
    String title

    /**
     *
     */
    @Column(name = "GCBPBTR_PAGE_ID")
    String pageId

    /**
     *
     */
    @Column(name = "GCBPBTR_SOURCE_IND")
    String sourceInd

    /**
     *
     */
    @Column(name = "GCBPBTR_USER_ID")
    String lastModifiedBy

    /**
     *
     */
    @Column(name = "GCBPBTR_SYSTEM_REQUIRED")
    String systemRequired

    /**
     *
     */
    @Column(name = "GCBPBTR_ACTIVE_IND")
    String activeInd

    /**
     *
     */
    @Column(name = "GCBPBTR_ACTIVITY_DATE")
    Date lastModified

    /**
     *
     */
    @Version
    @Column(name = "GCBPBTR_VERSION")
    Long version

    /**
     *
     */
    @Column(name = "GCBPBTR_DATA_ORIGIN")
    String dataOrigin

    /**
     *
     */
    @Column(name = "GCBPBTR_VPDI_CODE")
    String vpdiCode

    static constraints = {
        title(blank: false, nullable: false)
        sourceInd(blank: false, nullable: false)
        systemRequired(blank: false, nullable: false)
        activeInd(nullable: false)
    }

    /**
     *
     * @return
     */
    public static def fetchActionItemTemplates() {
        ActionItemTemplate.withSession {session ->
            List actionItemTemplates = session.getNamedQuery('ActionItemTemplate.fetchActionItemTemplates').list()
            return actionItemTemplates
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    public static def fetchActionItemTemplateById( Long myId ) {
        ActionItemTemplate.withSession {session ->
            List actionItemTemplates = session.getNamedQuery('ActionItemTemplate.fetchActionItemTemplateById').setLong('myId', myId)?.list()
            return actionItemTemplates
        }
    }
}
