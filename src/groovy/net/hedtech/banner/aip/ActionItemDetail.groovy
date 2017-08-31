/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemDetail.fetchActionItemDetailById",
                query = """
           FROM ActionItemDetail a
           WHERE a.actionItemId = :myId
          """)
])


@Entity
@Table(name = "GCRACNT")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemDetail implements Serializable {

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
    @Column(name = "GCRACNT_ACTION_ITEM_ID", length = 19)
    Long actionItemId

    /**
     * Text for action item detail
     */
    @Column(name = "GCRACNT_ACTION_ITEM_TEXT", columnDefinition = "TEXT")
    /*need to figure out what to limit length to for display*/
    String text

    /***
     * Related ID of the action item template
     */
    @Column(name = "GCRACNT_TEMPLATE_REFERENCE_ID", length = 19)
    Long actionItemTemplateId

    /**
     * User action item pertains to
     */
    @Column(name = "GCRACNT_USER_ID", length = 30)
    String lastModifiedby

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
    @Column(name = "GCRACNT_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCRACNT_DATA_ORIGIN", length = 30)
    String dataOrigin

    static constraints = {
//        id(nullable: false, maxSize: 19)
        actionItemId( nullable: false, maxSize: 19 )
        text( nullable: true ) //summary length only for now
        actionItemTemplateId( nullable: true )
        lastModifiedby( nullable: false, maxSize: 30 )
        lastModified( nullable: false, maxSize: 30 )
        version( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 19 )
    }

    /**
     *
     * @param id
     * @return
     */
    public static def fetchActionItemDetailById( Long id ) {
        ActionItemDetail.withSession {session ->
            List actionItemDetail = session.getNamedQuery( 'ActionItemDetail.fetchActionItemDetailById' ).setLong( 'myId', id ).list()
            return actionItemDetail[0] ? actionItemDetail[0] : []
        }
    }
}
