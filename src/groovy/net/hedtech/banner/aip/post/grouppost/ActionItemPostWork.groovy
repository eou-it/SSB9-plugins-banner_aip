package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.general.asynchronous.task.AsynchronousTask

import javax.persistence.*


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
    ActionItemPost actionItemGroupSend;

    /** The target of the send item */

    @Column(name = "GCRAIIM_PIDM")
    Long recipientPidm;

    @Column(name = "GCRAIIM_CURRENT_STATE")
    @Enumerated(EnumType.STRING)
    ActionItemPostWorkExecutionState currentExecutionState;

    @Column(name = "GCRAIIM_ERROR_TEXT")
    @Lob
    String errorText;

    @Column(name = "GCRAIIM_STARTED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date startedDate;

    @Column(name = "GCRAIIM_STOP_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date stopDate;

    /** Correlation ID linking the request to the recipient data to the job to the final item. **/

    @Column(name = "GCRAIIM_REFERENCE_ID")
    String referenceId

    @Column(name = "GCRAIIM_CREATIONDATETIME")
    @Temporal(TemporalType.TIMESTAMP)
    Date creationDateTime;

    /**
     * Error Code: The error code for the error scenario that failed the Action Item Job
     */

    @Column(name = "GCRAIIM_ERROR_CODE")
    @Enumerated(EnumType.STRING)
    ActionItemErrorCode errorCode

    static constraints = {
        lastModified( nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 30 )
        stopDate( nullable: true )
        errorText( nullable: true )
        mepCode( nullable: true )
        errorCode( nullable: true )
    }


    public static List fetchByExecutionState( ActionItemPostWorkExecutionState executionState, Integer max = Integer.MAX_VALUE ) {
        def results
        ActionItemPostWork.withSession { session ->
            results = session.getNamedQuery( 'ActionItemPostWork.fetchByExecutionState' )
                    .setParameter( 'executionState', executionState )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    ?.list()
        }
        return results
    }


    public static List fetchByGroupSend( ActionItemPost groupSend, Integer max = Integer.MAX_VALUE ) {
        def results
        ActionItemPostWork.withSession { session ->
            results = session.getNamedQuery( 'ActionItemPostWork.fetchByGroupSend' )
                    .setParameter( 'groupSend', groupSend )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    ?.list()
        }
        return results
    }


    public static List fetchByExecutionStateAndGroupSend( ActionItemPostWorkExecutionState executionState, ActionItemPost groupSend, Integer max = Integer.MAX_VALUE ) {
        def results
        ActionItemPostWork.withSession { session ->
            results = session.getNamedQuery( 'ActionItemPostWork.fetchByExecutionStateAndGroupSend' )
                    .setParameter( 'executionState', executionState )
                    .setParameter( 'groupSend', groupSend )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    ?.list()
        }
        return results
    }
}
