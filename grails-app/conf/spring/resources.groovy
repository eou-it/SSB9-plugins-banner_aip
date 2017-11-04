/*********************************************************************************
  Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

import grails.util.Holders
import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import net.hedtech.banner.aip.post.engine.ActionItemAsynchronousTaskProcessingEngineImpl
import net.hedtech.banner.aip.post.grouppost.ActionItemPostMonitor

/**
 * Spring bean configuration using Groovy DSL, versus normal Spring XML.
 */
beans = {
    asynchronousBannerAuthenticationSpoofer(AsynchronousBannerAuthenticationSpoofer) {
        dataSource = ref('dataSource')
    }

    // Manage the execution state of the post as a whole
    // This object will scan the post records at regular intervals to determine
    // if the post has completed.
    actionItemPostMonitor(ActionItemPostMonitor) { bean ->
        bean.autowire = 'byName'
        bean.initMethod = 'init'
        asynchronousBannerAuthenticationSpoofer = ref('asynchronousBannerAuthenticationSpoofer')
        monitorIntervalInSeconds =  Holders.config.aip?.aipPostMonitor?.monitorIntervalInSeconds ?: 10
    }

    actionItemPostWorkProcessingEngine (ActionItemAsynchronousTaskProcessingEngineImpl) { bean ->
        bean.autowire = 'byName'
        bean.initMethod = 'init'
        jobManager = ref('actionItemPostWorkTaskManagerService')
        asynchronousBannerAuthenticationSpoofer = ref('asynchronousBannerAuthenticationSpoofer')
        maxThreads = Holders.config.aip?.actionItemPostWorkProcessingEngine?.maxThreads ?: 10
        maxQueueSize = Holders.config.aip?.actionItemPostWorkProcessingEngine?.maxQueueSize ?: 5000
        continuousPolling = Holders.config.aip?.actionItemPostWorkProcessingEngine?.continuousPolling ?: true
        enabled = Holders.config.aip?.actionItemPostWorkProcessingEngine?.enabled ?: true
        pollingInterval = Holders.config.aip?.actionItemPostWorkProcessingEngine?.pollingInterval ?: 2000
        deleteSuccessfullyCompleted = Holders.config.aip?.actionItemPostWorkProcessingEngine?.deleteSuccessfullyCompleted ?: false
    }

    actionItemJobProcessingEngine (ActionItemAsynchronousTaskProcessingEngineImpl) { bean ->
        bean.autowire = 'byName'
        bean.initMethod = 'init'
        jobManager = ref('actionItemJobTaskManagerService')
        asynchronousBannerAuthenticationSpoofer = ref('asynchronousBannerAuthenticationSpoofer')
        maxThreads = Holders.config.aip?.actionItemJobProcessingEngine?.maxThreads ?: 10
        maxQueueSize = Holders.config.aip?.actionItemJobProcessingEngine?.maxQueueSize ?: 5000
        continuousPolling = Holders.config.aip?.actionItemJobProcessingEngine?.continuousPolling ?: true
        enabled = Holders.config.aip?.actionItemJobProcessingEngine?.enabled ?: true
        pollingInterval = Holders.config.aip?.actionItemJobProcessingEngine?.pollingInterval ?: 2000
        deleteSuccessfullyCompleted = Holders.config.aip?.actionItemJobProcessingEngine?.deleteSuccessfullyCompleted ?: false
    }
}