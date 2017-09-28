/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.FlushMode

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItem.fetchActionItems",
                query = """
           FROM ActionItem a
          """),
        @NamedQuery(name = "ActionItem.fetchActionItemById",
                query = """
           FROM ActionItem a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "ActionItem.existsSameNameInFolder",
                query = """ select count(a.id) FROM ActionItem a
                    WHERE upper(a.name) = upper(:name)
                    AND   a.folderId = :folderId""")
])

@Entity
@Table(name = "GCBACTM")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItem implements Serializable {

    /**
     * Surrogate ID for GCBACTM
     */

    @Id
    @Column(name = "GCBACTM_SURROGATE_ID")
    @SequenceGenerator(name = "GCBACTM_SEQ_GEN", allocationSize = 1, sequenceName = "GCBACTM_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBACTM_SEQ_GEN")
    Long id

    /**
     * Title of the action item
     */
    @Column(name = "GCBACTM_TITLE")
    String title

    /**
     * Name of the action item
     */
    @Column(name = "GCBACTM_NAME")
    String name

    /**
     * Status (Draft, Active, Inactive)
     */
    @Column(name = "GCBACTM_STATUS_CODE")
    String status

    /**
     * Status (Draft, Active, Inactive)
     */
    @Column(name = "GCBACTM_POSTED_IND")
    String postedStatus

    /***
     * Related folder ID of the action item
     */

    @Column(name = "GCBACTM_GCRAFLDR_ID")
    Long folderId

    /**
     * User action item pertains to
     */
    @Column(name = "GCBACTM_USER_ID")
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCBACTM_ACTIVITY_DATE")
    Date activityDate

    /**
     * Description for action item
     */
    @Column(name = "GCBACTM_DESCRIPTION", columnDefinition = "TEXT")
    /*need to figure out what to limit length to for display*/
    String description

    /**
     * UserID that created the action item
     */
    @Column(name = "GCBACTM_CREATOR_ID")
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "GCBACTM_CREATE_DATE")
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCBACTM_VERSION")
    Long version

    /**
     * Data Origin column for GCBACTM
     */
    @Column(name = "GCBACTM_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        folderId( blank: false, nullable: false, maxSize: 19 )
        title( blank: false, nullable: false, maxSize: 2048 )
        name( blank: false, nullable: false, maxSize: 60 )
        status( blank: false, nullable: false, maxSize: 1 )
        postedStatus( nullable: false, maxSize: 1 )
        userId( blank: false, nullable: false, maxSize: 30 )
        activityDate( blank: false, nullable: false, maxSize: 30 )
        description( nullable: true ) //summary length only for now
        creatorId( nullable: true, maxSize: 30 )
        createDate( nullable: true )
        dataOrigin( nullable: true, maxSize: 19 )
    }


    static def fetchActionItems() {
        ActionItem.withSession {session ->
            session.getNamedQuery( 'ActionItem.fetchActionItems' ).list()
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    // ReadOnly View?
    static def fetchActionItemById( Long myId ) {
        ActionItem.withSession {session ->
            session.getNamedQuery( 'ActionItem.fetchActionItemById' ).setLong( 'myId', myId )?.list()[0]
        }
    }

    /**
     * Checks if specified name already present in specified folder
     * @param folderId
     * @param name
     * @return
     */
    // Check constraint requirement that a Name in a folder must be unique
    static Boolean existsSameNameInFolder( Long folderId, String name ) {
        def count
        ActionItem.withSession {session ->
            session.setFlushMode( FlushMode.MANUAL );
            try {
                count = session.getNamedQuery( 'ActionItem.existsSameNameInFolder' )
                        .setString( 'name', name )
                        .setLong( 'folderId', folderId )
                        .uniqueResult()
            } finally {
                session.setFlushMode( FlushMode.AUTO )
            }
        }
        count > 0
    }
}
