/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */
package net.hedtech.banner.aip.post.job

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase



/**
 *  DAO service interface for actionItem group send item objects.
 */
@Slf4j
@Transactional
class ActionItemJobService extends ServiceBase {



    public List fetchPending( Integer max = Integer.MAX_VALUE ) {
        List pendingList = ActionItemJob.fetchPending( max )
        log.debug( "Found ${pendingList.size()} pending actionItem jobs." )
        pendingList
    }

    /**
     * Returns true if the actionItem job was acquired for the current thread.
     */
    public boolean acquire( Long jobId ) {
        log.debug( "Attempting to acquire actionItem job id = ${jobId}." )
        try {
            ActionItemJob jobToAcquire = get( jobId )
            log.debug( "ActionItem job withid = ${jobId} acquired" )
            jobToAcquire.status = ActionItemJobStatus.DISPATCHED.toString()
            update( jobToAcquire )
            return true
        } catch (ApplicationException e) {
            log.debug( "ActionItem job withid = ${jobId} not available." )
        }
        false
    }

    /**
     * Marks the actionItem job completed for the current thread.
     */
    public void markCompleted( Long jobId ) {
        log.debug( "Attempting to mark actionItem job id = ${jobId} as completed." )
        try {
            ActionItemJob jobToMarkComplete = get( jobId )
            log.debug( "ActionItem job withid = ${jobId} acquired" )
            jobToMarkComplete.status = ActionItemJobStatus.COMPLETED.toString()
            update( jobToMarkComplete )
            log.debug( "ActionItem job with id = ${jobId} marked as completed." )
        } catch (ApplicationException e) {
            log.debug( "No actionItem job with id = ${jobId} available to mark completed" )
        }
    }

    /**
     *
     * @param groupSendId
     * @return
     */
    void stopPendingAndDispatchedJobs( Long groupSendId ) {
        List<ActionItemJob> list = ActionItemJob.findRecordsToStopPendingAndDispatchedJobs( groupSendId )
        list?.each {
            it.status = ActionItemJobStatus.STOPPED
            it.lastModified = new Date()
            update( it, true )
        }
        log.debug( 'Number of updates ' + list?.size() )
    }

    /**
     *
     * @param groupSendId
     * @return
     */
    void deleteJobForAPostingId( Long groupSendId ) {
        def count = ActionItemJob.deleteJobForAPostingId( groupSendId )
        log.debug( 'Number of delete ' + count )
    }
}
