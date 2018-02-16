/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

/**
 * Action Item Post Selection Detail
 */
@Entity
@Table(name = "GVQ_ACTION_ITEM_POST_POPSEL")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
@NamedQueries(value = [
        @NamedQuery(name = "ActionItemPostSelectionDetailReadOnly.fetchSelectionIds",
                query = """ FROM ActionItemPostSelectionDetailReadOnly aips
                WHERE aips.id.actionItemPostId = :postingId """
        )
])
class ActionItemPostSelectionDetailReadOnly implements Serializable {

    @EmbeddedId
    PopulationSelectionKey id

    /**
     * selection pidm
     */
    @Column(name = "POST_SELECT_PIDM")
    Long actionItemPostSelectionPidm

    /**
     * posting User Id
     */
    @Column(name = "POST_USER_ID")
    String postingUserId

    /**
     * VERSION: Optimistic lock token.
     */
    @Version
    @Column(name = "POST_VERSION")
    Long version

    /**
     *
     * @return
     */
    static List<ActionItemPostSelectionDetailReadOnly> fetchSelectionIds( postingId ) {
        ActionItemPostSelectionDetailReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemPostSelectionDetailReadOnly.fetchSelectionIds' )
                    .setLong( 'postingId', postingId )
                    .list()
        }
    }
}

@Embeddable
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class PopulationSelectionKey implements Serializable {
    @Column(name = 'POST_ID')
    Long actionItemPostId
    @Column(name = "SELECTION_ID")
    Long selectionId
}
