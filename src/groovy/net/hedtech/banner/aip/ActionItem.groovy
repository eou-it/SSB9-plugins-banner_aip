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
        @NamedQuery(name = "ActionItem.existsSameTitleInFolder",
                query = """ FROM ActionItem a
                    WHERE upper(a.title) = upper(:title)
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
     * Status (Pending, Complete, other...)
     */
    @Column(name = "GCBACTM_STATUS")
    String status

    /***
     * Related ID of the action item
     */

    @Column(name = "GCBACTM_FOLDER_ID")
    Long folderId

    /**
     * User action item pertains to
     */
    @Column(name = "GCBACTM_USER_ID", length = 30)
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
    @Column(name = "GCBACTM_CREATOR_ID", length = 30)
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "GCBACTM_CREATE_DATE", length = 30)
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCBACTM_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for GCBACTM
     */
    @Column(name = "GCBACTM_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        folderId( blank: false, nullable: false, maxSize: 19 )
        //title(blank: false, nullable: false, maxSize: 2048, unique: 'folderId') // This works but logs an error. Using existsSameTitleInFolder
        title( blank: false, nullable: false, maxSize: 2048 )
        status( blank: false, nullable: false, maxSize: 30 )
        userId( blank: false, nullable: false, maxSize: 30 )
        activityDate( blank: false, nullable: false, maxSize: 30 )
        description( nullable: true ) //summary length only for now
        creatorId( nullable: true, maxSize: 30 )
        createDate( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 19 )
    }


    public static def fetchActionItems() {
        ActionItem.withSession {session ->
            List actionItem = session.getNamedQuery( 'ActionItem.fetchActionItems' ).list()
            return actionItem
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    // ReadOnly View?
    public static def fetchActionItemById( Long myId ) {
        ActionItem.withSession {session ->
            ActionItem actionItem = session.getNamedQuery( 'ActionItem.fetchActionItemById' ).setLong( 'myId', myId )?.list()[0]
            return actionItem
        }
    }

    /**
     * Checks if specified tile already present in specified folder
     * @param folderId
     * @param title
     * @return
     */
    // Check constraint requirement that a title in a folder must be unique
    public static Boolean existsSameTitleInFolder( Long folderId, String title ) {
        def query
        ActionItem.withSession {session ->
            session.setFlushMode( FlushMode.MANUAL );
            try {
                query = session.getNamedQuery( 'ActionItem.existsSameTitleInFolder' )
                        .setString( 'title', title )
                        .setLong( 'folderId', folderId )
                        .list()[0]
            } finally {
                session.setFlushMode( FlushMode.AUTO )
            }
        }
        return (query != null)
    }
}
