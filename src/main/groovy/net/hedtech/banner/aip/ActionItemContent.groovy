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
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Version


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemContent.fetchActionItemContentById",
                query = """
           FROM ActionItemContent a
           WHERE a.actionItemId = :myId
          """)
])


@Entity
@Table(name = "GCRACNT")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Action Item Content
 */
class ActionItemContent implements Serializable {

    /**
     * Surrogate ID for GCRACNT
     */

    @Id
    @Column(name = "GCRACNT_SURROGATE_ID")
    @SequenceGenerator(name = "GCRACNT_SEQ_GEN", allocationSize = 1, sequenceName = "GCRACNT_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRACNT_SEQ_GEN")
    Long id

    /***
     * Related ID of the action item
     */
    @Column(name = "GCRACNT_GCBACTM_ID")
    Long actionItemId

    /**
     * Text for action item detail
     */
    @Column(name = "GCRACNT_ACTION_ITEM_TEXT", columnDefinition = "TEXT")
    /*need to figure out what to limit length to for display*/
    String text

    /***
     * Related ID of the action item page builder template
     */
    @Column(name = "GCRACNT_GCBPBTR_ID")
    Long actionItemTemplateId

    /**
     * User action item pertains to
     */
    @Column(name = "GCRACNT_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRACNT_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModified

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCRACNT_VERSION")
    Long version

    /**
     * Data Origin column for GCRACNT
     */
    @Column(name = "GCRACNT_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        actionItemId( nullable: false, maxSize: 19 )
        text( nullable: true )
        actionItemTemplateId( nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        lastModified( nullable: true )
        version( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 19 )
    }

    /**
     *
     * @param id
     * @return
     */
    static def fetchActionItemContentById( Long id ) {
        ActionItemContent.withSession {session ->
            session.getNamedQuery( 'ActionItemContent.fetchActionItemContentById' ).setLong( 'myId', id )?.uniqueResult()
        }
    }
}
