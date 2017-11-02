/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import org.apache.commons.logging.Log
import org.apache.log4j.Logger
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException

/**
 * Action Item Monitor class
 */
class ActionItemPostMonitor implements DisposableBean {
    private Log log = Logger.getLogger( this.getClass() )
    private ActionItemPostMonitorThread monitorThread
    private ActionItemPostService actionItemPostService
    private ActionItemPostWorkService actionItemPostWorkService
    private ActionItemPostCompositeService actionItemPostCompositeService
    private AsynchronousBannerAuthenticationSpoofer asynchronousBannerAuthenticationSpoofer
    private int monitorIntervalInSeconds = 10


    @Required
    void setMonitorIntervalInSeconds( int monitorIntervalInSeconds ) {
        this.monitorIntervalInSeconds = monitorIntervalInSeconds
    }


    int getMonitorIntervalInSeconds() {
        return monitorIntervalInSeconds
    }


    @Required
    void setAsynchronousBannerAuthenticationSpoofer( AsynchronousBannerAuthenticationSpoofer asynchronousBannerAuthenticationSpoofer ) {
        this.asynchronousBannerAuthenticationSpoofer = asynchronousBannerAuthenticationSpoofer
    }


    @Required
    void setActionItemPostService( ActionItemPostService actionItemPostService ) {
        this.actionItemPostService = actionItemPostService
    }


    @Required
    void setActionItemPostWorkService( ActionItemPostWorkService actionItemPostWorkService ) {
        this.actionItemPostWorkService = actionItemPostWorkService
    }


    @Required
    void setActionItemPostCompositeService( ActionItemPostCompositeService actionItemPostCompositeService ) {
        this.actionItemPostCompositeService = actionItemPostCompositeService
    }


    void init() {
        LoggerUtility.info( log, "Initialized." )

    }


    @Override
    void destroy() throws Exception {
        LoggerUtility.info( log, "Calling disposable bean method." )
        if (monitorThread) {
            monitorThread.stopRunning()
        }
    }


    void startMonitoring() {
        LoggerUtility.info( log, "Monitor thread started." )
        if (!monitorThread) {
            monitorThread = new ActionItemPostMonitorThread( this )
        }
        monitorThread.start()
    }


    void shutdown() {
        LoggerUtility.debug( log, "Shutting down." )
        if (monitorThread) {
            monitorThread.stopRunning()
            try {
                this.monitorThread.join()
            } catch (InterruptedException e) {
            }
        }
        monitorThread = null
    }


    void monitorPosts() {
        LoggerUtility.debug( log, "Checking posts for status updates." )
        // begin setup
        asynchronousBannerAuthenticationSpoofer.authenticateAndSetFormContextForExecute()
        try {
            List<ActionItemPost> groupSendList = ActionItemPost.findRunning()
            LoggerUtility.debug( log, "Running group send count = " + groupSendList.size() + "." )

            for (ActionItemPost groupSend : groupSendList) {
                LoggerUtility.debug( log, "group send id = " + groupSend.id + "." )
                if (groupSend.postingCurrentState.equals( ActionItemPostExecutionState.Processing )) {
                    int runningCount = ActionItemPostWork.fetchRunningGroupSendItemCount( groupSend )
                    if (runningCount == 0) {
                        completeGroupSend( groupSend.id )
                    }
                }
            }
        } catch (Throwable t) {
            LoggerUtility.error( log, t )
        }
    }

    /**
     * Calls the compete group send method of service class.
     * @param groupSendId the id of the group send.
     * @return the updated group send
     */
    private ActionItemPost completeGroupSend( Long groupSendId ) {
        LoggerUtility.debug( log, "Completing group send with id = " + groupSendId + "." )

        int retries = 2
        while (retries > 0) {
            retries--
            try {
                return actionItemPostCompositeService.completePost( groupSendId )
            } catch (HibernateOptimisticLockingFailureException e) {
                if (retries == 0) {
                    throw e
                }
            }
        }
    }
}
