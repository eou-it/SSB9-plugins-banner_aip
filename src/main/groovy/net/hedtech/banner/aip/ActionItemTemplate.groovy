/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

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
class ActionItemTemplate implements Serializable {

    /**
     * Surrogate ID for GCBPBTR
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
     * Description of the action item template
     */
    @Column(name = "GCBPBTR_DESCRIPTION")
    String description

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
    static def fetchActionItemTemplates() {
        ActionItemTemplate.withSession { session ->
            List actionItemTemplates = session.getNamedQuery('ActionItemTemplate.fetchActionItemTemplates').list()
            return actionItemTemplates
        }
    }

}
