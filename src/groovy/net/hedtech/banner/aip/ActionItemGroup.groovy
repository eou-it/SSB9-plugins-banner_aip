/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.FlushMode

import javax.persistence.*

/**
 * Domain class for Action Item Group table
 */
@NamedQueries(value = [
        @NamedQuery(name = "ActionItemGroup.fetchActionItemGroups",
                query = """
           FROM ActionItemGroup a
          """),
        @NamedQuery(name = "ActionItemGroup.fetchActionItemGroupById",
                query = """
           FROM ActionItemGroup a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "ActionItemGroup.existsSameNameInFolder",
                query = """ FROM ActionItemGroup a
                    WHERE upper(a.name) = upper(:name)
                    AND   a.folderId = :folderId""")
])

@Entity
@Table(name = "GCBAGRP")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemGroup implements Serializable {

    /**
     * Surrogate ID for GCBAGRP
     */

    @Id
    @Column(name = "GCBAGRP_SURROGATE_ID")
    @SequenceGenerator(name = "GCBAGRP_SEQ_GEN", allocationSize = 1, sequenceName = "GCBAGRP_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBAGRP_SEQ_GEN")
    Long id

    /**
     * Version of the action item
     */

    @Version
    @Column(name = "GCBAGRP_VERSION")
    Long version

    /**
     * Title of the action item
     */
    @Column(name = "GCBAGRP_TITLE")
    String title

    /**
     * Name of the action item
     */
    @Column(name = "GCBAGRP_NAME")
    String name

    /***
     * Related Folder ID of the action item folder
     */
    @Column(name = "GCBAGRP_GCRFLDR_ID")
    Long folderId

    /**
     * Description for action item
     */
    @Column(name = "GCBAGRP_INSTRUCTION", columnDefinition = "TEXT")
    /*need to figure out what to limit length to for display*/
    String description

    /**
     * Status Draft, Active, InActive ...
     */
    @Column(name = "GCBAGRP_STATUS_CODE")
    String status

    /**
     * Posting Indicator
     */
    @Column(name = "GCBAGRP_POSTED_IND")
    String postingInd

    /**
     * User action item pertains to
     */
    @Column(name = "GCBAGRP_USER_ID")
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCBAGRP_ACTIVITY_DATE")
    Date activityDate

    /**
     * Data Origin column for GCBAGRP
     */
    @Column(name = "GCBAGRP_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        title( nullable: false, maxSize: 2048 )
        name( nullabe: false, maxSize: 60 )
        description( nullable: true )
        postingInd(nullabe: true)
        folderId( nullable: false, maxSize: 30 )
        status( nullable: false, maxSize: 1 )
        userId( nullable: false, maxSize: 30 )
        activityDate( nullable: false, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 19 )
    }

    /**
     *
     * @return
     */
    public static def fetchActionItemGroups() {
        ActionItemGroup.withSession {session ->
            List<ActionItemGroup> actionItemGroup = session.getNamedQuery( 'ActionItemGroup.fetchActionItemGroups' ).list()
            return actionItemGroup
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public static def fetchActionItemGroupById( Long id ) {
        ActionItemGroup.withSession {session ->
            ActionItemGroup actionItemGroupById = session.getNamedQuery( 'ActionItemGroup.fetchActionItemGroupById' ).setLong( 'myId', id ).list()[0]
            return actionItemGroupById
        }
    }

    /**
     *
     * @param folderId
     * @param title
     * @return
     */
    // Check constraint requirement that a title in a folder must be unique
    public static Boolean existsSameNameInFolder( Long folderId, String name ) {
        def query
        ActionItem.withSession {session ->
            session.setFlushMode( FlushMode.MANUAL );
            try {
                query = session.getNamedQuery( 'ActionItemGroup.existsSameNameInFolder' )
                        .setString( 'name', name )
                        .setLong( 'folderId', folderId )
                        .list()[0]
            } finally {
                session.setFlushMode( FlushMode.AUTO )
            }
        }
        return (query != null)
    }
}
