/********************************************************************************
 Copyright 2018-2021 Ellucian Company L.P. and its affiliates.
 ********************************************************************************/
import grails.util.Holders

posting {
    if(Holders.getConfig()?.general?.aip?.enabled) {
        autoStartup = true
        waitForJobsToCompleteOnShutdown = true
        exposeSchedulerInRepository = true
        props {
            scheduler.skipUpdateCheck = true
            scheduler.instanceName = 'Action Item Quartz Scheduler'
            scheduler.instanceId = 'AIP' // new instance ID for AIP?


            if (grails.util.Holders.getConfig()?.aip?.scheduler?.idleWaitTime) {
                scheduler.idleWaitTime = grails.util.Holders.getConfig().aip.scheduler.idleWaitTime
            }

            boolean isWebLogic = grails.util.Holders.getConfig()?.aip?.weblogicDeployment == true
            if (isWebLogic) {
                println("Setting driverDelegateClass to org.quartz.impl.jdbcjobstore.oracle.weblogic.WebLogicOracleDelegate")
                jobStore.driverDelegateClass = 'org.quartz.impl.jdbcjobstore.oracle.weblogic.WebLogicOracleDelegate'
            } else {
                println("Setting driverDelegateClass to org.quartz.impl.jdbcjobstore.oracle.OracleDelegate")
                jobStore.driverDelegateClass = 'org.quartz.impl.jdbcjobstore.oracle.OracleDelegate'
            }
            jobStore.class = 'net.hedtech.banner.general.scheduler.quartz.BannerDataSourceJobStoreCMT'


            jobStore.tablePrefix = 'GCRQRTZ_' // Share tables. AIP has own instance
            jobStore.isClustered = true
            if (grails.util.Holders.getConfig()?.aip?.scheduler?.clusterCheckinInterval) {
                // Default from Quartz: jobStore.clusterCheckinInterval = 15000
                jobStore.clusterCheckinInterval = grails.util.Holders.getConfig().aip.scheduler.clusterCheckinInterval
            }

            jobStore.useProperties = false

        }
    }
}
