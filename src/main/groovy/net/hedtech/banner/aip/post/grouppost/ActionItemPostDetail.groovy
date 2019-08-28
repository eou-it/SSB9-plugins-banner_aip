/*******************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

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


/**
 * Action Item Post Detail: Defines the parameters of an action item to be posted.
 */
@Entity
@Table(name = "GCRAPST")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
@NamedQueries(value = [
        @NamedQuery(name = "ActionItemPostDetail.fetchByActionItemPostId",
                query = """ FROM ActionItemPostDetail gs
                WHERE gs.actionItemPostId = :actionItemPostId_ """
        )
])
class ActionItemPostDetail implements Serializable {

    /**
     * SURROGATE ID: Generated unique numeric identifier for this entity.
     */
    @Id
    @Column(name = "GCRAPST_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAPST_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAPST_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAPST_SEQ_GEN")
    Long id

    /**
     * VERSION: Optimistic lock token.
     */
    @Version
    @Column(name = "GCRAPST_VERSION")
    Long version

    /**
     * ACTION ITEM POST ID: Action Item post  ID.
     */
    @Column(name = "GCRAPST_GCBAPST_ID")
    Long actionItemPostId

    /**
     * ACTION ITEM ID: Action Item ID of the action item to post.
     */
    @Column(name = "GCRAPST_GCBACTM_ID")
    Long actionItemId

    /**
     * ACTIVITY DATE: Date that record was created or last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GCRAPST_ACTIVITY_DATE")
    Date lastModified

    /**
     * USER ID: The user ID of the person who inserted or last updated this record.
     */
    @Column(name = "GCRAPST_USER_ID")
    String lastModifiedBy

    /**
     * DATA ORIGIN: Source system that created or updated the data.
     */
    @Column(name = "GCRAPST_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        version( nullable: true, maxSize: 19 )
        actionItemPostId( nullable: false, maxSize: 19 )
        actionItemId( nullable: false, maxSize: 19 )
        lastModified( nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 30 )

    }

    public static List fetchByActionItemPostId(Long postId) {
            def results
            ActionItemPostWork.withSession {session ->
                results = session.getNamedQuery( 'ActionItemPostDetail.fetchByActionItemPostId' )
                        .setParameter( 'actionItemPostId_', postId )
                        .list()
            }
            return results
        }
}
