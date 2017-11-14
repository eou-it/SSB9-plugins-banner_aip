/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */
package net.hedtech.banner.aip.post.job

import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger

/**
 *  DAO service interface for actionItem group send item objects.
 */
class ActionItemJobService extends ServiceBase {
    private static final LOGGER = Logger.getLogger( this.class )


    public List fetchPending( Integer max = Integer.MAX_VALUE ) {
        List pendingList = ActionItemJob.fetchPending( max )
        LoggerUtility.debug( LOGGER, "Found ${pendingList.size()} pending actionItem jobs." )
        pendingList
    }

    /**
     * Returns true if the actionItem job was acquired for the current thread.
     */
    public boolean acquire( Long jobId ) {
        LoggerUtility.debug( LOGGER, "Attempting to acquire actionItem job id = ${jobId}." )
        try {
            ActionItemJob jobToAcquire = get( jobId )
            LoggerUtility.debug( LOGGER, "ActionItem job withid = ${jobId} acquired" )
            jobToAcquire.status = ActionItemJobStatus.DISPATCHED.toString()
            update( jobToAcquire )
            return true
        } catch (ApplicationException e) {
            LoggerUtility.debug( LOGGER, "ActionItem job withid = ${jobId} not available." )
        }
        false
    }

    /**
     * Marks the actionItem job completed for the current thread.
     */
    public void markCompleted( Long jobId ) {
        LoggerUtility.debug( LOGGER, "Attempting to mark actionItem job id = ${jobId} as completed." )
        try {
            ActionItemJob jobToMarkComplete = get( jobId )
            LoggerUtility.debug( LOGGER, "ActionItem job withid = ${jobId} acquired" )
            jobToMarkComplete.status = ActionItemJobStatus.COMPLETED.toString()
            update( jobToMarkComplete )
            LoggerUtility.debug( LOGGER, "ActionItem job with id = ${jobId} marked as completed." )
        } catch (ApplicationException e) {
            LoggerUtility.debug( LOGGER, "No actionItem job with id = ${jobId} available to mark completed" )
        }
    }

}
