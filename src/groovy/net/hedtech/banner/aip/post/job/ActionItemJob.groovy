/*******************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.aip.post.job

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.general.asynchronous.task.AsynchronousTask
import net.hedtech.banner.service.DatabaseModifiesState

import javax.persistence.*

/**
 * ActionItem Log. Record of individual actionItem group final status. entity.
 */
@Entity
@ToString
@EqualsAndHashCode
@Table(name = "GCBAJOB")
@DatabaseModifiesState
@NamedQueries(value = [
    @NamedQuery( name = "ActionItemJob.fetchPending",
        query = """ FROM ActionItemJob job
                    WHERE job.status = :status_
                    ORDER BY job.id ASC """
    ),
    @NamedQuery( name = "ActionItemJob.fetchByStatusAndReferenceId",
            query = """ FROM ActionItemJob job
        WHERE job.status = :status_
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
    @Column(name = "GCBAJOB_REFERENCE_ID")
    String referenceId

    /**
     * STATUS: The final disposition of the communication send operation. SENT, ERROR.
     */
    @Column(name = "GCBAJOB_STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActionItemJobStatus status = ActionItemJobStatus.PENDING

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

    @Column(name="GCBAJOB_CREATION_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    Date creationDateTime;

    @Column(name="GCBAJOB_ERROR_TEXT")
    @Lob
    String errorText;

    /**
     * Error Code: The error code for the error scenario that failed the ActionItem Job
     */
    @Column(name = "GCBAJOB_ERROR_CODE")
    @Enumerated(EnumType.STRING)
    ActionItemErrorCode errorCode

    static constraints = {
        lastModified(nullable: true)
        lastModifiedBy(nullable: true, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 30)
        referenceId(nullable: false, maxSize: 255)
        status(nullable: false, maxSize: 30)
        errorText(nullable: true)
        errorCode(nullable: true)
    }

    // Read Only fields that should be protected against update
    public static readonlyProperties = [ 'id' ]


    ActionItemJobStatus getStatus() {
        return status
    }

    void setStatus( ActionItemJobStatus status) {
        this.status = status
    }

    public static List fetchPending( Integer max = Integer.MAX_VALUE ) {
        def results
        ActionItemJob.withSession { session ->
            results = session.getNamedQuery( 'ActionItemJob.fetchByStatusAndReferenceId' )
                    .setParameter( 'status_', ActionItemJobStatus.PENDING )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    .list()
        }
        return results
    }

    public static List fetchCompleted() {
        def results
        ActionItemJob.withSession { session ->
            results = session.getNamedQuery( 'ActionItemJob.fetchByStatusAndReferenceId' )
                .setParameter( 'status_', ActionItemJobStatus.COMPLETED )
                .setFirstResult( 0 )
                .setMaxResults( Integer.MAX_VALUE )
                .list()
        }
        return results
    }

}