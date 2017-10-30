/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.FlushMode

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemGroupAssign.fetchActionItemGroupAssign",
                query = """
           FROM ActionItemGroupAssign a
          """),
        @NamedQuery(name = "ActionItemGroupAssign.fetchById",
                query = """
            FROM ActionItemGroupAssign a 
            WHERE a.id = :myId 
        """),
        @NamedQuery(name = "ActionItemGroupAssign.fetchByGroupId",
                query = """
            FROM ActionItemGroupAssign a
            WHERE a.groupId = :myId
        """),
        @NamedQuery(name = "ActionItemGroupAssign.fetchByActionId",
                query = """
            FROM ActionItemGroupAssign a
            WHERE a.actionItemId = :myId
        """),
        @NamedQuery(name = "ActionItemGroupAssign.fetchByActionItemGroup",
                query = """
            FROM ActionItemGroupAssign a
            WHERE a.actionItemId = :actionItemId
            AND a.groupId = :groupId
        """),
        @NamedQuery(name = "ActionItemGroupAssign.existsSameSeqNoInActionItem",
                query = """ select a.actionItemId, a.seqNo, COUNT(*) FROM ActionItemGroupAssign a
                    WHERE a.groupId = :groupId
                    GROUP BY a.actionItemId, a.seqNo
                    HAVING COUNT(*) > 1
        """)
])

@Entity
@Table(name = "GCRAGRA")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemGroupAssign implements Serializable {

    /**
     * Surrogate ID for GCRAGRA
     */

    @Id
    @Column(name = "GCRAGRA_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAGRA_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAGRA_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAGRA_SEQ_GEN")
    Long id

    /**
     * ID of the Group
     */
    @Column(name = "GCRAGRA_GCBAGRP_ID")
    Long groupId

    /**
     * ID of the ActionItem
     */
    @Column(name = "GCRAGRA_GCBACTM_ID")
    Long actionItemId

    /**
     * Sequence number of ActionItem in group
     */
    @Column(name = "GCRAGRA_SEQ_NO")
    Long seqNo

    /***
     * Date of record created or last updated
     */

    @Column(name = "GCRAGRA_ACTIVITY_DATE")
    Date lastModified

    /**
     * User ID of the person who inserted or last updated
     */
    @Column(name = "GCRAGRA_USER_ID")
    String lastModifiedBy

    /**
     * Version
     */
    @Version
    @Column(name = "GCRAGRA_VERSION")
    Long version

    /**
     * Data Origin column for GCRAGRA
     */
    @Column(name = "GCRAGRA_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        groupId( blank: false, nullable: false, maxSize: 19 )
        actionItemId( blank: false, nullable: false, maxSize: 19)
        seqNo(blank: false, nullable: false, maxSize: 19)
        lastModifiedBy( blank: false, nullable: true, maxSize: 30 )
        lastModified( blank: false, nullable: true )
        dataOrigin( nullable: true, maxSize: 19 )
    }


    static def fetchActionItemGroupAssign() {
        ActionItem.withSession {session ->
            session.getNamedQuery( 'ActionItemGroupAssign.fetchActionItemGroupAssign' ).list()
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    // ReadOnly View?
    static def fetchById( Long myId ) {
        ActionItemGroupAssign.withSession {session ->
            def actionItemGroupAssign = session.getNamedQuery( 'ActionItemGroupAssign.fetchById' ).setLong( 'myId', myId )?.list()[0]
            return actionItemGroupAssign
        }
    }

    static def fetchByGroupId ( Long myId) {
        ActionItemGroupAssign.withSession {session ->
            def actionItemGroupAssign = session.getNamedQuery('ActionItemGroupAssign.fetchByGroupId').setLong('myId', myId)?.list()
            return actionItemGroupAssign
        }
    }

    static def fetchByActionItemId( Long myId ) {
        ActionItemGroupAssign.withSession {session ->
            List actionItemGroupAssign = session.getNamedQuery( 'ActionItemGroupAssign.fetchByActionId').setLong( 'myId', myId)?.list()
            return actionItemGroupAssign
        }
    }

    static def fetchByActionItemIdAndGroupId (Long actionItemId, Long groupId) {
        ActionItemGroupAssign.withSession {session ->
            def actionItemGroupAssign = session.getNamedQuery( 'ActionItemGroupAssign.fetchByActionItemGroup').setLong('actionItemId', actionItemId).setLong('groupId', groupId)?.list()[0]
            return actionItemGroupAssign
        }
    }

    /**
     * Checks if ActionItems in same group have same seq Number
     * @return
     */
    // Check constraint requirement that a Name in a folder must be unique
    static Boolean existsSameSeqNoInActionItem( Long groupId ) {
        def count
        ActionItem.withSession {session ->
            session.setFlushMode( FlushMode.MANUAL )
            try {
                count = session.getNamedQuery( 'ActionItemGroupAssign.existsSameSeqNoInActionItem' )
                        .setLong( 'groupId', groupId )
                        .uniqueResult()
            } finally {
                session.setFlushMode( FlushMode.AUTO )
            }
        }
        count > 0
    }
}
