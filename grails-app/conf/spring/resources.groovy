/*********************************************************************************
  Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

import grails.util.Holders
import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import net.hedtech.banner.aip.post.engine.ActionItemAsynchronousTaskProcessingEngineImpl

/**
 * Spring bean configuration using Groovy DSL, versus normal Spring XML.
 */
beans = {
    asynchronousBannerAuthenticationSpoofer(AsynchronousBannerAuthenticationSpoofer) {
        dataSource = ref('dataSource')
    }

    // Manage the execution state of the group send as a whole
    // This object will scan the group send item records at regular intervals to determine
    // if the group send has completed.
    //actionItemPostMonitor(ActionItemPostMonitor) { bean ->
    //    bean.autowire = 'byName'
    //    bean.initMethod = 'init'
    //    asynchronousBannerAuthenticationSpoofer = ref('asynchronousBannerAuthenticationSpoofer')
    //    monitorIntervalInSeconds =  Holders.config.aip?.aipPostMonitor?.monitorIntervalInSeconds ?: 10
    //}

    actionItemPostWorkProcessingEngine (ActionItemAsynchronousTaskProcessingEngineImpl) { bean ->
        bean.autowire = 'byName'
        bean.initMethod = 'init'
        jobManager = ref('actionItemPostWorkTaskManagerService')
        asynchronousBannerAuthenticationSpoofer = ref('asynchronousBannerAuthenticationSpoofer')
        maxThreads = Holders.config.aip?.aipGroupSendItemProcessingEngine?.maxThreads ?: 10
        maxQueueSize = Holders.config.aip?.aipGroupSendItemProcessingEngine?.maxQueueSize ?: 5000
        continuousPolling = Holders.config.aip?.aipGroupSendItemProcessingEngine?.continuousPolling ?: true
        enabled = Holders.config.aip?.aipGroupSendItemProcessingEngine?.enabled ?: true
        pollingInterval = Holders.config.aip?.aipGroupSendItemProcessingEngine?.pollingInterval ?: 2000
        deleteSuccessfullyCompleted = Holders.config.aip?.aipGroupSendItemProcessingEngine?.deleteSuccessfullyCompleted ?: false
    }

    actionItemJobProcessingEngine (ActionItemAsynchronousTaskProcessingEngineImpl) { bean ->
        bean.autowire = 'byName'
        bean.initMethod = 'init'
        jobManager = ref('actionItemJobTaskManagerService')
        asynchronousBannerAuthenticationSpoofer = ref('asynchronousBannerAuthenticationSpoofer')
        maxThreads = Holders.config.aip?.aipJobProcessingEngine?.maxThreads ?: 10
        maxQueueSize = Holders.config.aip?.aipJobProcessingEngine?.maxQueueSize ?: 5000
        continuousPolling = Holders.config.aip?.aipJobProcessingEngine?.continuousPolling ?: true
        enabled = Holders.config.aip?.aipJobProcessingEngine?.enabled ?: true
        pollingInterval = Holders.config.aip?.aipJobProcessingEngine?.pollingInterval ?: 2000
        deleteSuccessfullyCompleted = Holders.config.aip?.aipJobProcessingEngine?.deleteSuccessfullyCompleted ?: false
    }
}