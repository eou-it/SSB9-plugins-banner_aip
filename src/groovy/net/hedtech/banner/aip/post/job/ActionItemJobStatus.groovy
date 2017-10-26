/*******************************************************************************
Copyright 2017 Ellucian Company L.P. and its affiliates.
*******************************************************************************/
package net.hedtech.banner.aip.post.job

/**
 * An enumeration of status codes for actionItem jobs, used for tracking whether
 * the job is pending processing or has been dispatched to a processing engine.
 */
public enum ActionItemJobStatus {
    PENDING,   // The job has not yet been processed.
    DISPATCHED,  // The job has been given to a thread for execution
    STOPPED, // The job has been requested to stop
    FAILED,    // The job has been processed, but failed
    COMPLETED; // The job has been processed successfully


    /**
     * Returns a set of all ActionITemJobStatus enum values.
     * @return Set<ActionItemJobStatus> the set of ActionItemJobStatus
     */
    public Set<ActionItemJobStatus> set() {
        return EnumSet.range( ActionItemJobStatus.PENDING, ActionItemJobStatus.COMPLETED );
    }
}
