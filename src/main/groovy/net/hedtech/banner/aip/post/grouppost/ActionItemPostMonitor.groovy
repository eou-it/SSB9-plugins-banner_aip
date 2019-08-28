/*******************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.annotation.Required
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException

/**
 * Action Item Monitor class
 */
@Slf4j
@Transactional
class ActionItemPostMonitor implements DisposableBean {
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
        log.info( "Initialized." )

    }


    @Override
    void destroy() throws Exception {
        log.info( "Calling disposable bean method." )
        if (monitorThread) {
            monitorThread.stopRunning()
        }
    }


    void startMonitoring() {
        log.info( "Monitor thread started." )
        if (!monitorThread) {
            monitorThread = new ActionItemPostMonitorThread( this )
        }
        monitorThread.start()
    }


    void shutdown() {
        log.debug( "Shutting down." )
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
        log.debug( "Checking posts for status updates." )
        // begin setup
        asynchronousBannerAuthenticationSpoofer.authenticateAndSetFormContextForExecute()
        try {
            List<ActionItemPost> groupSendList = ActionItemPost.findRunning()
            log.debug( "Running group send count = " + groupSendList.size() + "." )

            for (ActionItemPost groupSend : groupSendList) {
                log.debug( "group send id = " + groupSend.id + "." )
                if (groupSend.postingCurrentState.equals( ActionItemPostExecutionState.Processing )) {
                    int runningCount = ActionItemPostWork.fetchRunningGroupSendItemCount( groupSend )
                    if (runningCount == 0) {
                        completeGroupSend( groupSend.id )
                    }
                }
            }
        } catch (Throwable t) {
            log.error( t.toString() )
        }
    }

    /**
     * Calls the compete group send method of service class.
     * @param groupSendId the id of the group send.
     * @return the updated group send
     */
    private ActionItemPost completeGroupSend(Long groupSendId ) {
        log.debug( "Completing group send with id = " + groupSendId + "." )

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
