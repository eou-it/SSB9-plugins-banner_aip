/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *********************************************************************************/
package net.hedtech.banner.aip.post.job

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.grouppost.ActionItemPostWork
import net.hedtech.banner.general.asynchronous.task.AsynchronousTask
import net.hedtech.banner.general.asynchronous.task.AsynchronousTaskManager
import net.hedtech.banner.general.asynchronous.task.AsynchronousTaskMonitorRecord
import net.hedtech.banner.general.communication.groupsend.automation.StringHelper
import org.apache.commons.lang.NotImplementedException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * ActionItemJobTaskManagerService implements asynchronous job engine life cycle
 * methods for manipulating group send item tasks.
 *
 */
class ActionItemJobTaskManagerService implements AsynchronousTaskManager {
    private final Log log = LogFactory.getLog(this.getClass());

    def actionItemJobService
    def actionItemJobProcessorService

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
     * Deletes an existing actionItem job from the persistent store.
     * @param job the actionItem job to remove
     */
    @Transactional(rollbackFor = Throwable.class )
    public void delete( AsynchronousTask jobItem )  throws ApplicationException {
        ActionItemJob actionItemJob = (ActionItemJob) jobItem
        actionItemJobService.delete( actionItemJob );
        log.debug( "${this.getClass().getSimpleName()} deleted actionItem job with id = ${actionItemJob.id}." );
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
    public List<ActionItemJob> getPendingJobs( int max ) throws ApplicationException {
        log.debug( "Get pending actionItem jobs" )
        //FIXME:
        println "CRR: Get pending actionItem jobs: JobTaskManager"
        List<ActionItemJob> result = actionItemJobService.fetchPending( max )
        log.debug( "Found ${result.size()} actionItem jobs." )
        return result;
    }

    public boolean acquire( AsynchronousTask task ) throws ApplicationException {
        ActionItemJob job = task as ActionItemJob
        log.info( "Acquiring actionItem job with id = ${job.id}." )
        println "CRR: Acquire actionItem jobs: JobTaskManager"
        return actionItemJobService.acquire( job.id )
    }


    /* (non-Javadoc)
     * @see com.sungardhe.common.services.commsupport.jobs.JobManager#markComplete(com.sungardhe.common.services.commsupport.jobs.Job)
     */
    public void markComplete( AsynchronousTask task ) throws ApplicationException {
        ActionItemJob job = task as ActionItemJob
        log.info( "Marking completed actionItem job id = ${job.id}." )
        actionItemJobService.markCompleted( job.id )
    }


    /**
     * Performs work for the specified job.
     * @return a boolean indicating whether the job could be processed or not.  If true, the service was
     *         able to obtain an exclusive lock on the job and process it.  If false, the job was either
     *         already processed, or locked by another thread, and the call returned without doing any work
     */
    @Transactional(propagation=Propagation.REQUIRES_NEW, rollbackFor = Throwable.class )
    public void process( AsynchronousTask task) throws ApplicationException {
        ActionItemJob job = task as ActionItemJob
        log.info( "Processing actionItem job id = ${job.id}." )
        println "CRR: Processing actionItem jobs: JobTaskManager"
        try {
            if (simulatedFailureException != null) {
                throw simulatedFailureException;
            }

            actionItemJobProcessorService.performActionItemJob( task.getId() );

        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug( "${this.getClass().getSimpleName()}.process caught exception " + e.getMessage(), e );
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
    public void markFailed( AsynchronousTask task, String errorCode, Throwable cause  ) throws ApplicationException {
        ActionItemJob job = (ActionItemJob) task
        job.refresh()
        job.setStatus( ActionItemJobStatus.FAILED )
        job.setErrorText( StringHelper.stackTraceToString( cause ) )
        job.setErrorCode(ActionItemErrorCode.valueOf(errorCode))
        actionItemJobService.update( job )
        if (cause) {
            log.info( "Marked job with id = ${job.id} as failed; cause = ${cause.toString()}." )
        } else {
            log.info( "Marked job with id = ${job.id} as failed." )
        }
    }


    @Transactional(rollbackFor = Throwable.class )

    public AsynchronousTaskMonitorRecord updateMonitorRecord( AsynchronousTaskMonitorRecord monitorRecord ) {
        /*
        if (log.isDebugEnabled()) {
            ActionItemJob job = (ActionItemJob) task
            log.debug( "Continuing to process actionItem job id = ${job.id}." )
        }

        // Not implemented for CR1. The purpose of this service method is for debug monitoring active thread
        // processing.
        */
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

