/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.asynchronous.task.AsynchronousTask
import net.hedtech.banner.general.asynchronous.task.AsynchronousTaskManager
import net.hedtech.banner.general.asynchronous.task.AsynchronousTaskMonitorRecord
import net.hedtech.banner.general.communication.groupsend.automation.StringHelper
import org.apache.commons.lang.NotImplementedException
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * ActionItemGroupSendItemTaskManagerService implements asynchronous job engine life cycle
 * methods for manipulating group send item tasks.
 */
class ActionItemPostWorkTaskManagerService implements AsynchronousTaskManager {
    private static final LOGGER = Logger.getLogger( 'net.hedtech.banner.aip.post.grouppost.ActionItemPostWorkTaskManagerService' )

    def actionItemPostWorkProcessorService
    def actionItemPostWorkService

    /**
     * Used for testing purposes only.  If this is not null when a job is being
     * processed, that processing will throw this exception.
     */
    private Exception simulatedFailureException


    public Class<ActionItemPostWork> getJobType() {
        ActionItemPostWork.class
    }


    public AsynchronousTask create( AsynchronousTask job ) throws ApplicationException {
        throw new NotImplementedException()
    }


    public void init() {
        LoggerUtility.debug( LOGGER, "${this.getClass().getSimpleName()} initialized." )
    }

    /**
     * Deletes an existing ActionItem job from the persistent store.
     * @param job the ActionItem job to remove
     */
    @Transactional(rollbackFor = Throwable.class)
    public void delete( AsynchronousTask jobItem ) throws ApplicationException {
        ActionItemPostWork groupSendItem = jobItem as ActionItemPostWork
        actionItemPostWorkService.delete( groupSendItem )
        LoggerUtility.debug( LOGGER, "GroupSendItemManagerImpl deleted group send item " + groupSendItem.getId() )
    }

    /**
     * Returns jobs that have failed.
     * @return List < ActionItemJob >  the failed jobs
     */
    @Transactional(readOnly = true, rollbackFor = Throwable.class)
    public List getFailedJobs() {
        throw new NotImplementedException()
    }

    // This method is called often from a polling thread, so it is imperative that
    // it be kept as performant as possible -- e.g., be careful about logging at debug level or higher
    /**
     * Returns pending jobs, sorting by oldest first.
     * @param max maximum number of jobs to return.
     * @return
     */
    @Transactional(readOnly = true, rollbackFor = Throwable.class)
    public List<ActionItemPostWork> getPendingJobs( int max ) throws ApplicationException {
        LoggerUtility.debug( LOGGER, "Getting pending jobs" )
        List<ActionItemPostWork> result = ActionItemPostWork.fetchByExecutionState( ActionItemPostWorkExecutionState.Ready, max )
        LoggerUtility.debug( LOGGER, "Found " + result.size() + " jobs." )
        result
    }


    public boolean acquire( AsynchronousTask task ) throws ApplicationException {
        ActionItemPostWork groupSendItem = task as ActionItemPostWork
        LoggerUtility.info( LOGGER, "Acquired group send item id = " + groupSendItem.getId() + ", pidm = " + groupSendItem.recipientPidm + "." )
        true
    }


    public void markComplete( AsynchronousTask task ) throws ApplicationException {
        ActionItemPostWork groupSendItem = task as ActionItemPostWork
        LoggerUtility.info( LOGGER, "Marking completed group send item id = " + groupSendItem.getId() + ", pidm = " + groupSendItem.recipientPidm + "." )
    }

    /**
     * Performs work for the specified job.
     * @return a boolean indicating whether the job could be processed or not.  If true, the service was
     *         able to obtain an exclusive lock on the job and process it.  If false, the job was either
     *         already processed, or locked by another thread, and the call returned without doing any work
     */
    public void process( AsynchronousTask task ) throws ApplicationException {
        ActionItemPostWork actionItemWorkTask = task as ActionItemPostWork
        LoggerUtility.info( LOGGER, "Processing group send item id = " + actionItemWorkTask.getId() + ", pidm = " + actionItemWorkTask.recipientPidm + "." )
        try {
            if (simulatedFailureException != null) {
                throw simulatedFailureException
            }
            actionItemPostWorkProcessorService.performPostItem( actionItemWorkTask )
        } catch (Exception e) {
            LoggerUtility.debug( LOGGER, "GroupSendItemManagerImpl.process caught exception " + e.getMessage())
            // we MUST re-throw as the thread which invoked this method must
            // mark the job as failed by using another thread (as the
            // thread associated with this thread will likely be rolled back)
            if (e instanceof ApplicationException) {
                throw (ApplicationException) e
            } else {
                throw new RuntimeException( e )
            }
        }
    }

    /**
     * Marks a job as having failed.
     * @param job the job that failed
     * @param cause the cause of the failure
     */
    public void markFailed( AsynchronousTask task, String errorCode, Throwable cause ) throws ApplicationException {
        ActionItemPostWork groupSendItem = (ActionItemPostWork) task
        actionItemPostWorkProcessorService.failGroupSendItem( groupSendItem.getId(), errorCode, StringHelper.stackTraceToString( cause ) )
        LoggerUtility.debug( LOGGER, "GroupSendItemManager.markFailed(task=" + groupSendItem.getId() + ") has marked the task as failed " )
    }


    @Transactional(rollbackFor = Throwable.class)
    public AsynchronousTaskMonitorRecord updateMonitorRecord( AsynchronousTaskMonitorRecord monitorRecord ) {
        null
    }

    /**
     * Sets the manager to fail processing a job with a simulated exception.
     * Used only for unit testing scenarios.
     * Should never be invoked in a production environment
     * @param cause the exception to throw when processing a job
     */
    public void setSimulatedFailureException( Exception cause ) {
        this.simulatedFailureException = cause
    }

}
