/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException

/**
 *
 */
class ActionItemPostMonitor implements DisposableBean {
    private Log log = LogFactory.getLog(this.getClass())
    private ActionItemPostMonitorThread monitorThread
    private ActionItemPostService actionItemPostService
    private ActionItemPostWorkService actionItemPostWorkService
    private ActionItemPostCompositeService actionItemPostCompositeService
    private AsynchronousBannerAuthenticationSpoofer asynchronousBannerAuthenticationSpoofer
    private int monitorIntervalInSeconds = 10

    @Required
    void setMonitorIntervalInSeconds(int monitorIntervalInSeconds) {
        this.monitorIntervalInSeconds = monitorIntervalInSeconds
    }

    int getMonitorIntervalInSeconds() {
        return monitorIntervalInSeconds
    }

    @Required
    void setAsynchronousBannerAuthenticationSpoofer(AsynchronousBannerAuthenticationSpoofer asynchronousBannerAuthenticationSpoofer) {
        this.asynchronousBannerAuthenticationSpoofer = asynchronousBannerAuthenticationSpoofer
    }


    @Required
    public void setActionItemPostService(ActionItemPostService actionItemPostService) {
        this.actionItemPostService = actionItemPostService
    }


    @Required
    public void setActionItemPostWorkService(ActionItemPostWorkService actionItemPostWorkService) {
        this.actionItemPostWorkService = actionItemPostWorkService
    }


    @Required
    public void setActionItemPostCompositeService(ActionItemPostCompositeService actionItemPostCompositeService) {
        this.actionItemPostCompositeService = actionItemPostCompositeService
    }


    public void init() {
        log.info("Initialized.")

    }


    @Override
    void destroy() throws Exception {
        log.info("Calling disposable bean method.")
        if (monitorThread) {
            monitorThread.stopRunning()
        }
    }


    public void startMonitoring() {
        log.info("Monitor thread started.")
        if (!monitorThread) {
            monitorThread = new ActionItemPostMonitorThread(this)
        }
        monitorThread.start()
    }


    public void shutdown() {
        log.debug("Shutting down.")
        if (monitorThread) {
            monitorThread.stopRunning()
            try {
                this.monitorThread.join()
            } catch (InterruptedException e) {
            }
        }
        monitorThread = null
    }


    public void monitorPosts() {
        if (log.isDebugEnabled()) log.debug("Checking posts for status updates.")
        // begin setup
        asynchronousBannerAuthenticationSpoofer.authenticateAndSetFormContextForExecute()
        try {
            List<ActionItemPost> groupSendList = ActionItemPost.findRunning()
            if (log.isDebugEnabled()) log.debug("Running group send count = " + groupSendList.size() + ".")

            for (ActionItemPost groupSend : groupSendList) {
                if (log.isDebugEnabled()) log.debug("group send id = " + groupSend.id + ".")
                if (groupSend.postingCurrentState.equals(ActionItemPostExecutionState.Processing)) {
                    int runningCount = ActionItemPostWork.fetchRunningGroupSendItemCount(groupSend)
                    if (runningCount == 0) {
                        completeGroupSend( groupSend.id )
                    }
                }
            }
        } catch (Throwable t) {
            t.printStackTrace()
            log.error(t)
        }
    }

    /**
     * Calls the compete group send method of service class.
     * @param groupSendId the id of the group send.
     * @return the updated group send
     */
    private ActionItemPost completeGroupSend( Long groupSendId ) {
        if (log.isDebugEnabled()) log.debug( "Completing group send with id = " + groupSendId + "." )

        int retries = 2
        while(retries > 0) {
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
