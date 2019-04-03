/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.post.engine.ActionItemAsynchronousTaskProcessingEngineImpl
import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostMonitorThreadIntegrationTests extends BaseIntegrationTestCase {

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void monitorPosts() {
        ActionItemPostCompositeService actionItemPostCompositeService = new ActionItemPostCompositeService()
        ActionItemPostMonitor actionItemPostMonitor = new ActionItemPostMonitor()
        actionItemPostMonitor.setActionItemPostCompositeService( actionItemPostCompositeService )
        ActionItemPostService actionItemPostService = new ActionItemPostService()
        actionItemPostMonitor.setActionItemPostService( actionItemPostService )
        ActionItemPostWorkService actionItemPostWorkService = new ActionItemPostWorkService()
        actionItemPostMonitor.setActionItemPostWorkService( actionItemPostWorkService )
        AsynchronousBannerAuthenticationSpoofer asynchronousBannerAuthenticationSpoofer = new AsynchronousBannerAuthenticationSpoofer()
        actionItemPostMonitor.setAsynchronousBannerAuthenticationSpoofer( asynchronousBannerAuthenticationSpoofer )
        actionItemPostMonitor.setMonitorIntervalInSeconds( 100 )
        assert actionItemPostMonitor.getMonitorIntervalInSeconds() == 100
        actionItemPostMonitor.init()
        actionItemPostMonitor.destroy()
        actionItemPostMonitor.startMonitoring()
        actionItemPostMonitor.shutdown()
        actionItemPostMonitor.monitorPosts()
        ActionItemPostMonitorThread actionItemPostMonitorThread = new ActionItemPostMonitorThread( actionItemPostMonitor )
        actionItemPostMonitorThread.start()
        actionItemPostMonitorThread.stopRunning()
        actionItemPostMonitorThread.monitorPosts()
        actionItemPostMonitor.monitorThread = actionItemPostMonitorThread
    }


    @Test
    void monitorPosts1() {
        ActionItemAsynchronousTaskProcessingEngineImpl actionItemAsynchronousTaskProcessingEngineImpl = new ActionItemAsynchronousTaskProcessingEngineImpl()
        AsynchronousBannerAuthenticationSpoofer asynchronousBannerAuthenticationSpoofer = new AsynchronousBannerAuthenticationSpoofer()
        actionItemAsynchronousTaskProcessingEngineImpl.setAsynchronousBannerAuthenticationSpoofer( asynchronousBannerAuthenticationSpoofer )
        ActionItemPostWorkTaskManagerService actionItemPostWorkTaskManagerService = new ActionItemPostWorkTaskManagerService()
        actionItemAsynchronousTaskProcessingEngineImpl.setJobManager( actionItemPostWorkTaskManagerService )
        actionItemAsynchronousTaskProcessingEngineImpl.toString()
        actionItemAsynchronousTaskProcessingEngineImpl.setContinuousPolling( true )
        actionItemAsynchronousTaskProcessingEngineImpl.setMaxThreads( 1000 )
        actionItemAsynchronousTaskProcessingEngineImpl.setMaxQueueSize( 1000 )
        actionItemAsynchronousTaskProcessingEngineImpl.setPollingInterval( 100 )
        actionItemAsynchronousTaskProcessingEngineImpl.setDeleteSuccessfullyCompleted( true )
        assert actionItemAsynchronousTaskProcessingEngineImpl.maxThreads == 1000


    }
}
