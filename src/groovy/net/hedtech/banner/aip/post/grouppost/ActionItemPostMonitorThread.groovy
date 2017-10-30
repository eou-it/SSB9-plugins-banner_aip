/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import org.apache.log4j.Logger

/**
 *
 */
class ActionItemPostMonitorThread extends Thread {

    private boolean keepRunning = true
    private ActionItemPostMonitor monitor
    private static final log = Logger.getLogger(ActionItemPostMonitorThread.class)


    ActionItemPostMonitorThread( ActionItemPostMonitor monitor ) {
        this.monitor = monitor
    }

    @Override
    public void run() {
        while (keepRunning) {
            monitorPosts()
            long nextMonitorTime = System.currentTimeMillis() + monitor.monitorIntervalInSeconds * 1000
            synchronized (this) {
                try {
                    while (System.currentTimeMillis() < nextMonitorTime) wait( monitor.monitorIntervalInSeconds * 1000)
                } catch (InterruptedException e) {
                }
            }
        }
    }

    void stopRunning() {
        keepRunning = false
        synchronized (this) {
            notify()
        }
    }

    private void monitorPosts() {
        log.trace( "monitorPosts() begin" )
        try {
            if (keepRunning) {
                try {
                    monitor.monitorPosts()
                } catch (Throwable t) {
                    log.error( "Exception monitoring posts", t )
                }
            }
        } finally {
            log.trace( "monitorPosts() end")
        }
    }


}
