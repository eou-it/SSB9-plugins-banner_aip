/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.asynchronous.task.AsynchronousTask
import net.hedtech.banner.general.asynchronous.task.AsynchronousTaskManager
import net.hedtech.banner.general.asynchronous.task.AsynchronousTaskMonitorRecord
import net.hedtech.banner.general.communication.groupsend.automation.StringHelper

// TODO: move to more logical shared location package
import org.apache.commons.lang.NotImplementedException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * ActionItemGroupSendItemTaskManagerService implements asynchronous job engine life cycle
 * methods for manipulating group send item tasks.
 */
class ActionItemPostTaskManagerService implements AsynchronousTaskManager {
    private final Log log = LogFactory.getLog(this.getClass());

    ActionItemPostProcessorService actionItemPostProcessorService
    ActionItemPostWorkProcessorService actionItemPostWorkProcessorService

    /**
     * Used for testing purposes only.  If this is not null when a job is being
     * processed, that processing will throw this exception.
     */
    private Exception simulatedFailureException;


    public Class<ActionItemPostWork> getJobType() {
        return ActionItemPostWork.class;
    }

    public AsynchronousTask create( AsynchronousTask job) throws ApplicationException {
        throw new NotImplementedException();
    }

    public void init() {
        log.debug( "${this.getClass().getSimpleName()} initialized." );
    }

    /**
     * Deletes an existing action item job from the persistent store.
     * @param action item job to remove
     */
    @Transactional(rollbackFor = Throwable.class )
    public void delete( AsynchronousTask jobItem )  throws ApplicationException {
        if (log.isInfoEnabled()) {
            log.debug( "Automatic deletion not supported" );
        }
    }


    /**
     * Returns jobs that have failed.
     * @return List<ActionItemJob> the failed jobs
     */
    @Transactional(readOnly=true, rollbackFor = Throwable.class )
    public List getFailedJobs() {
        throw new NotImplementedException();
    }

    // This method is called often from a polling thread, so it is imperative that
    // it be kept as performant as possible -- e.g., be careful about logging at debug level or higher
    /**
     * Returns pending jobs, sorting by oldest first.
     * @param max maximum number of jobs to return.
     * @return
     */
    @Transactional(readOnly=true, rollbackFor = Throwable.class )
    public List<ActionItemPostWork> getPendingJobs( int max ) throws ApplicationException {
        log.debug( "Getting pending group sends" )
        List<ActionItemPost> result = ActionItemPost.findRunning( max )
        log.debug( "Found " + result.size() + " jobs." )
        return result;
    }

    public boolean acquire( AsynchronousTask task ) throws ApplicationException {
        if (log.isInfoEnabled()) {
            log.info( "Acquired group send id = ${task.id}." )
        }
        return true;
    }

    public void markComplete( AsynchronousTask task ) throws ApplicationException {
        if (log.isInfoEnabled()) {
            ActionItemPost groupSend = task as ActionItemPost
            log.info( "Marking completed group send id = ${task.id}." )
        }
    }

    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor = Throwable.class )
    public void process( AsynchronousTask task) throws ApplicationException {
        ActionItemPost groupSend = task as ActionItemPost

        if (log.isInfoEnabled()) {
            log.info( "Processing group send id = ${task.id}." )
        }

        try {
            if (simulatedFailureException != null) {
                throw simulatedFailureException;
            }

            actionItemPostProcessorService.performGroupSendItem( groupSend );

        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug( e );
            }

            // we MUST re-throw as the thread which invoked this method must
            // mark the job as failed by using another thread (as the
            // thread associated with this thread will likely be rolled back)
            if (e instanceof ApplicationException) {
                throw (ApplicationException) e;
            } else {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Marks a job as having failed.
     * @param job the job that failed
     * @param cause the cause of the failure
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor = Throwable.class )
    public void markFailed( AsynchronousTask task, String errorCode, Throwable cause ) throws ApplicationException {
        ActionItemPost groupSend = (ActionItemPost) task
        actionItemPostWorkProcessorService.failGroupSendItem( groupSend.getId(), errorCode, StringHelper.stackTraceToString( cause ) );
        if (log.isDebugEnabled()) {
            log.debug( "Marking failed group send id = ${task.id}." );
        }
    }

    @Transactional(rollbackFor = Throwable.class )
    public AsynchronousTaskMonitorRecord updateMonitorRecord( AsynchronousTaskMonitorRecord monitorRecord ) {
        if (log.isDebugEnabled()) {
            log.debug( "Called updateMonitorRecord with monitorRecord agentID = ${monitorRecord.agentID} and jobId = ${monitorRecord.jobID}." )
        }

        // Not implemented for CR1. The purpose of this service method is for debug monitoring active thread
        // processing.
        return null
    }

    /**
     * Sets the manager to fail processing a job with a simulated exception.
     * Used only for unit testing scenarios.
     * Should never be invoked in a production environment
     * @param cause the exception to throw when processing a job
     */
    public void setSimulatedFailureException( Exception cause ) {
        this.simulatedFailureException = cause;
    }

}
