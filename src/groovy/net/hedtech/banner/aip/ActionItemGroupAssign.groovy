/*********************************************************************************
 Copyright 2017-2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "ActionItemGroupAssign.fetchByGroupId",
                query = """
            FROM ActionItemGroupAssign a
            WHERE a.groupId = :myId
        """),
        @NamedQuery(name = "ActionItemGroupAssign.fetchByActionItemGroup",
                query = """
            FROM ActionItemGroupAssign a
            WHERE a.actionItemId = :actionItemId
            AND a.groupId = :groupId
        """),
        @NamedQuery(name = "ActionItemGroupAssign.checkIfAssignedGroupPresentForSpecifiedActionItem",
                query = """ SELECT COUNT (actionItemId)
                   FROM ActionItemGroupAssign a
                   WHERE a.actionItemId = :actionItemId
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
        actionItemId( blank: false, nullable: false, maxSize: 19 )
        seqNo( blank: false, nullable: false, maxSize: 19 )
        lastModifiedBy( nullable: true, maxSize: 30 )
        lastModified( nullable: true )
        dataOrigin( nullable: true, maxSize: 19 )
    }

    /**
     *
     * @param myId
     * @return
     */
    static def fetchByGroupId( Long myId ) {
        ActionItemGroupAssign.withSession {session ->
            session.getNamedQuery( 'ActionItemGroupAssign.fetchByGroupId' ).setLong( 'myId', myId )?.list()
        }
    }

    /**
     *
     * @param actionItemId
     * @param groupId
     * @return
     */
    static def fetchByActionItemIdAndGroupId( Long actionItemId, Long groupId ) {
        ActionItemGroupAssign.withSession {session ->
            session.getNamedQuery( 'ActionItemGroupAssign.fetchByActionItemGroup' ).setLong( 'actionItemId', actionItemId ).setLong( 'groupId', groupId )?.list()[0]
        }
    }

    /**
     *
     * @param actionItemId
     * @return
     */
    static def checkIfAssignedGroupPresentForSpecifiedActionItem( Long actionItemId ) {
        ActionItemGroupAssign.withSession {session ->
            session.getNamedQuery( 'ActionItemGroupAssign.checkIfAssignedGroupPresentForSpecifiedActionItem' )
                    .setLong( 'actionItemId', actionItemId )
                    .uniqueResult() > 0
        }
    }
}
