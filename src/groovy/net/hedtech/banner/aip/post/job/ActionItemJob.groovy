/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.job

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.grouppost.ActionItemPostWorkExecutionState
import net.hedtech.banner.general.asynchronous.task.AsynchronousTask

import javax.persistence.*

/**
 * ActionItem Log. Record of individual actionItem group final status. entity.
 */
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "GCBAJOB")
@NamedQueries(value = [
        @NamedQuery(name = "ActionItemJob.fetchByStatus",
                query = """ FROM ActionItemJob job
                            WHERE job.status = :status_
                            ORDER BY job.id ASC """
        ),
        @NamedQuery(name = "ActionItemJob.findRecordsToStopPendingAndDispatchedJobs",
                query = """from ActionItemJob aij
                                   WHERE  aij.status IN (:checkExecutionState)
                                          and aij.referenceId in (select aiw.referenceId from ActionItemPostWork aiw
                                          WHERE aiw.actionItemGroupSend.id = :groupSendId and aiw.currentExecutionState = :checkPostWorkExecutionState)"""
        ),
        @NamedQuery(name = "ActionItemJob.deleteJobForAPostingId",
                query = """DELETE FROM ActionItemJob aij WHERE  EXISTS  (select aiw.id from ActionItemPostWork aiw, ActionItemPost  aip
                                                  WHERE aij.referenceId = aiw.referenceId and aiw.actionItemGroupSend.id = aip.id and aip.id = :groupSendId)"""
        ),
        @NamedQuery(name = "ActionItemJob.fetchByStatusAndReferenceId",
                query = """ FROM ActionItemJob job
                            WHERE job.status = :status_
                            AND job.referenceId = :referenceId_
                            ORDER BY job.id ASC """
        )
])
class ActionItemJob implements AsynchronousTask {

    /**
     * SURROGATE ID: Generated unique numeric identifier for this entity.
     */

    @Id
    @Column(name = "GCBAJOB_SURROGATE_ID")
    @SequenceGenerator(name = "GCBAJOB_SEQ_GEN", allocationSize = 1, sequenceName = "GCBAJOB_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBAJOB_SEQ_GEN")
    Long id

    /**
     * SEND REFERENCE ID: The reference id of the group send item that initiated this communication.
     */

    @Column(name = "GCBAJOB_AIIM_REFERENCE_ID")
    String referenceId

    /**
     * STATUS: The final disposition of the communication send operation. SENT, ERROR.
     */

    @Column(name = "GCBAJOB_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    ActionItemJobStatus status

    /**
     * VERSION: Optimistic lock token.
     */

    @Version
    @Column(name = "GCBAJOB_VERSION")
    Integer version

    /**
     * ACTIVITY DATE: Date that record was created or last updated.
     */

    @Column(name = "GCBAJOB_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModified

    /**
     * USER ID: The user ID of the person who inserted or last updated this record.
     */

    @Column(name = "GCBAJOB_USER_ID")
    String lastModifiedBy

    /**
     * DATA ORIGIN: Source s dystem that created or updated the data.
     */

    @Column(name = "GCBAJOB_DATA_ORIGIN")
    String dataOrigin

    @Column(name = "GCBAJOB_CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date creationDateTime

    @Column(name = "GCBAJOB_ERROR_TEXT")
    @Lob
    String errorText

    /**
     * Error Code: The error code for the error scenario that failed the ActionItem Job
     */

    @Column(name = "GCBAJOB_ERROR_CODE")
    @Enumerated(EnumType.STRING)
    ActionItemErrorCode errorCode

    static constraints = {
        lastModified( nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 30 )
        referenceId( nullable: false, maxSize: 255 )
        status( nullable: false, maxSize: 30 )
        creationDateTime( nullable: false )
        errorText( nullable: true )
        errorCode( nullable: true )
    }

    // Read Only fields that should be protected against update
    public static readonlyProperties = ['id']

    /**
     * Lists Pending Jobs
     * @param max
     * @return
     */
    static List fetchPending( Integer max = Integer.MAX_VALUE ) {
        ActionItemJob.withSession { session ->
            session.getNamedQuery( 'ActionItemJob.fetchByStatus' )
                    .setParameter( 'status_', ActionItemJobStatus.PENDING )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    .list()
        }
    }

    /**
     * Lists Completed jobs
     * @return
     */
    static List fetchCompleted() {
        ActionItemJob.withSession { session ->
            session.getNamedQuery( 'ActionItemJob.fetchByStatus' )
                    .setParameter( 'status_', ActionItemJobStatus.COMPLETED )
                    .setFirstResult( 0 )
                    .setMaxResults( Integer.MAX_VALUE )
                    .list()
        }
    }

    /**
     *
     * @param groupSendId
     * @return
     */
    static def findRecordsToStopPendingAndDispatchedJobs( Long groupSendId ) {
        ActionItemJob.withSession { session ->
            session.getNamedQuery( 'ActionItemJob.findRecordsToStopPendingAndDispatchedJobs' )
                    .setLong( 'groupSendId', groupSendId )
                    .setParameter( 'checkPostWorkExecutionState', ActionItemPostWorkExecutionState.Complete )
                    .setParameterList( 'checkExecutionState', [ActionItemJobStatus.PENDING, ActionItemJobStatus.DISPATCHED] )
                    .list()
        }
    }

    /**
     *
     * @param groupSendId
     * @return
     */
    static def deleteJobForAPostingId( Long groupSendId ) {
        ActionItemJob.withSession { session ->
            session.getNamedQuery( 'ActionItemJob.deleteJobForAPostingId' )
                    .setLong( 'groupSendId', groupSendId )
                    .executeUpdate()
        }
    }
}
