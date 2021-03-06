/*******************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.general.asynchronous.task.AsynchronousTask

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Lob
import javax.persistence.ManyToOne
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Version



@Entity
@ToString
@EqualsAndHashCode
@Table(name = "GCRAIIM")
@NamedQueries(value = [
        @NamedQuery(name = "ActionItemPostWork.fetchByExecutionState",
                query = """ FROM ActionItemPostWork gsi
                     WHERE gsi.currentExecutionState = :executionState
                     ORDER by gsi.creationDateTime asc"""
        ),
        @NamedQuery(name = "ActionItemPostWork.fetchByGroupSend",
                query = """ FROM ActionItemPostWork gsi
                                 WHERE gsi.actionItemGroupSend = :groupSend                         
                                 ORDER by gsi.creationDateTime asc"""
        ),
        @NamedQuery(name = "ActionItemPostWork.fetchByExecutionStateAndGroupSend",
                query = """ FROM ActionItemPostWork gsi
                     WHERE gsi.actionItemGroupSend = :groupSend
                     and gsi.currentExecutionState = :executionState
                     ORDER by gsi.creationDateTime asc"""
        ),
        @NamedQuery(name = "ActionItemPostWork.countByExecutionStateAndGroupSend",
                query = """ SELECT COUNT(*) FROM ActionItemPostWork gsi
                            WHERE gsi.actionItemGroupSend = :groupSend
                            and gsi.currentExecutionState = :executionState
                            ORDER by gsi.creationDateTime asc"""
        ),
        @NamedQuery(name = "ActionItemPostWork.updateStateToStop",
                query = """UPDATE ActionItemPostWork aiw SET aiw.currentExecutionState = :updateExecutionState,
                                  aiw.lastModified = current_date, stopDate=current_date  WHERE  aiw.currentExecutionState = :executionState and aiw.actionItemGroupSend = :groupSend""")
])

class ActionItemPostWork implements AsynchronousTask {
    /**
     * KEY: Generated unique key.
     */

    @Id
    @Column(name = "GCRAIIM_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAIIM_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAIIM_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAIIM_SEQ_GEN")
    Long id

    /**
     *  Optimistic lock token.
     */

    @Version
    @Column(name = "GCRAIIM_VERSION")
    Long version

    /**
     *  The user ID of the person who inserted or last updated this record.
     */

    @Column(name = "GCRAIIM_USER_ID")
    String lastModifiedBy

    @Column(name = "GCRAIIM_VPDI_CODE")
    String mepCode

    /**
     *  Date that record was created or last updated.
     */

    @Column(name = "GCRAIIM_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModified

    /**
     *  Source system that created or updated the data.
     */

    @Column(name = "GCRAIIM_DATA_ORIGIN")
    String dataOrigin

    /**
     * Parent Action Item job
     */

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "GCRAIIM_GCBAPST_ID", referencedColumnName = "GCBAPST_SURROGATE_ID")
    ActionItemPost actionItemGroupSend

    /** The target of the send item */

    @Column(name = "GCRAIIM_PIDM")
    Long recipientPidm

    @Column(name = "GCRAIIM_CURRENT_STATE")
    @Enumerated(EnumType.STRING)
    ActionItemPostWorkExecutionState currentExecutionState

    @Column(name = "GCRAIIM_INSERT_COUNT")
    Long insertedCount

    @Column(name = "GCRAIIM_GCBACTM_ID_SET")
    String insertedItemIds

    @Column(name = "GCRAIIM_ERROR_TEXT")
    @Lob
    String errorText

    @Column(name = "GCRAIIM_STARTED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date startedDate

    @Column(name = "GCRAIIM_STOP_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date stopDate

    /** Correlation ID linking the request to the recipient data to the job to the final item. **/

    @Column(name = "GCRAIIM_REFERENCE_ID")
    String referenceId

    @Column(name = "GCRAIIM_CREATIONDATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    Date creationDateTime

    /**
     * Error Code: The error code for the error scenario that failed the Action Item Job
     */

    @Column(name = "GCRAIIM_ERROR_CODE")
    @Enumerated(EnumType.STRING)
    ActionItemErrorCode errorCode

    static constraints = {
        recipientPidm( nullable: false )
        currentExecutionState( nullable: false )
        lastModified( nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 30 )
        insertedCount( nullable: true )
        insertedItemIds( nullable: true )
        startedDate( nullable: true )
        stopDate( nullable: true )
        errorText( nullable: true )
        mepCode( nullable: true )
        errorCode( nullable: true )
    }

    /**
     *
     * @param executionState
     * @param max
     * @return
     */
    static List fetchByExecutionState( ActionItemPostWorkExecutionState executionState, Integer max = Integer.MAX_VALUE ) {
        ActionItemPostWork.withSession {session ->
            session.getNamedQuery( 'ActionItemPostWork.fetchByExecutionState' )
                    .setParameter( 'executionState', executionState )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    .list()
        }
    }

    /**
     *
     * @param groupSend
     * @param max
     * @return
     */
    static List fetchByGroupSend(ActionItemPost groupSend, Integer max = Integer.MAX_VALUE ) {
        ActionItemPostWork.withSession {session ->
            session.getNamedQuery( 'ActionItemPostWork.fetchByGroupSend' )
                    .setParameter( 'groupSend', groupSend )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    .list()
        }
    }

    /**
     *
     * @param executionState
     * @param groupSend
     * @param max
     * @return
     */
    static List fetchByExecutionStateAndGroupSend(ActionItemPostWorkExecutionState executionState, ActionItemPost groupSend, Integer max = Integer.MAX_VALUE ) {
        ActionItemPostWork.withSession {session ->
            session.getNamedQuery( 'ActionItemPostWork.fetchByExecutionStateAndGroupSend' )
                    .setParameter( 'executionState', executionState )
                    .setParameter( 'groupSend', groupSend )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    .list()
        }
    }

    /**
     *
     * @param groupSend
     * @return
     */
    static def fetchRunningGroupSendItemCount(ActionItemPost groupSend ) {
        ActionItemPostWork.withSession {session ->
            session.getNamedQuery( 'ActionItemPostWork.countByExecutionStateAndGroupSend' )
                    .setParameter( 'executionState', ActionItemPostWorkExecutionState.Ready )
                    .setParameter( 'groupSend', groupSend )
                    .uniqueResult()
        }
    }

    /**
     *
     * @param groupSend
     * @return
     */
    static def updateStateToStop(ActionItemPost groupSend ) {
        ActionItemPostWork.withSession {session ->
            session.getNamedQuery( 'ActionItemPostWork.updateStateToStop' )
                    .setParameter( 'executionState', ActionItemPostWorkExecutionState.Ready )
                    .setParameter( 'updateExecutionState', ActionItemPostWorkExecutionState.Stopped )
                    .setParameter( 'groupSend', groupSend )
                    .executeUpdate()
        }
    }
}
