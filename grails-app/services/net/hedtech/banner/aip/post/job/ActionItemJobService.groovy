/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */
package net.hedtech.banner.aip.post.job

import net.hedtech.banner.service.ServiceBase
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 *  DAO service interface for actionItem group send item objects.
 */
class ActionItemJobService extends ServiceBase {
    private final Log log = LogFactory.getLog(this.getClass());

    def preCreate( domainModelOrMap ) {
        ActionItemJob job = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as ActionItemJob
        if (job.getCreationDateTime() == null) {
            job.setCreationDateTime(new Date())
        }
    }

    public List fetchPending( Integer max = Integer.MAX_VALUE ) {
        List found = ActionItemJob.fetchPending( max )
        log.debug( "Found ${found.size()} pending actionItem jobs." )
        return found
    }

    /**
     * Returns true if the actionItem job was acquired for the current thread.
     */
    public boolean acquire( Long jobId ) {
        log.debug( "Attempting to acquire actionItem job id = ${jobId}." )

        List<ActionItemJob> actionItemJobs = ActionItemJob.fetchPendingByJobId( jobId )
        int rows = actionItemJobs.size() // should never be more than one
        if (rows == 1) {
            log.debug( "ActionItem job withid = ${jobId} acquired" )
            ActionItemJob jobToAcquire = actionItemJobs[0]
            jobToAcquire.status = ActionItemJobStatus.DISPATCHED.toString()
            update( jobToAcquire )
            return true
        } else if (rows == 0) {
            log.debug( "ActionItem job withid = ${jobId} not available." )
            return false
        } else {
            log.error( "ActionItemJobService.acqure found more than one record with job id = ${jobId}." )
            throw new RuntimeException( "ActionItemJobService.acquire aquire found ${rows} with job id = ${jobId} and status = ${ActionItemJobStatus.PENDING.toString()}." )
        }
    }

    /**
     * Marks the actionItem job completed for the current thread.
     */
    public void markCompleted( Long jobId ) {
        log.debug( "Attempting to mark actionItem job id = ${jobId} as completed." )

        List<ActionItemJob> actionItemJobs = ActionItemJob.fetchByJobId( jobId )
        int rows = actionItemJobs.size() // should never be more than one
        if (rows == 1) {
            ActionItemJob jobToMarkComplete = actionItemJobs[0]
            jobToMarkComplete.status = ActionItemJobStatus.COMPLETED.toString()
            update( jobToMarkComplete )
            log.debug( "ActionItem job with id = ${jobId} marked as completed." )
        } else if (rows == 0) {
            log.debug( "No actionItem job with id = ${jobId} available to mark completed")
        } else {
            log.error( "ActionItemJobService.markCompleted found more than one record with job id = ${jobId}." )
            throw new RuntimeException( "ActionItemJobService.markCompleted lookup found ${rows} with job id = ${jobId}. ")
        }
    }

}
