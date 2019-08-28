/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates .
 **********************************************************************************/

// ******************************************************************************
//
//                       +++ EXTERNALIZED CONFIGURATION +++
//
// ******************************************************************************
//
// Config locations should be added to the map used below. They will be loaded based upon this search order:
// 1. Load the configuration file if its location was specified on the command line using -DmyEnvName=myConfigLocation
// 2. Load the configuration file if it exists within the user's .grails directory (i.e., convenient for developers)
// 3. Load the configuration file if its location was specified as a system environment variable
//
grails.config.locations = [
        BANNER_APP_CONFIG:           "banner_configuration.groovy",
]

grails.project.groupId = "net.hedtech" // used when deploying to a maven repo

grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [html         : ['text/html', 'application/xhtml+xml'],
                     xml          : ['text/xml', 'application/xml', 'application/vnd.sungardhe.student.v0.01+xml'],
                     text         : 'text/plain',
                     js           : 'text/javascript',
                     rss          : 'application/rss+xml',
                     atom         : 'application/atom+xml',
                     css          : 'text/css',
                     csv          : 'text/csv',
                     all          : '*/*',
                     json         : ['application/json', 'text/json'],
                     form         : 'application/x-www-form-urlencoded',
                     multipartForm: 'multipart/form-data'
]

grails.views.default.codec = "html" // none, html, base64  **** Charlie note: Setting this to html will ensure html is escaped, to prevent XSS attack ****
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
grails.databinding.useSpringBinder = true
grails.converters.domain.include.version = true
grails.converters.json.date = "javascript"
grails.converters.json.pretty.print = true

banner {
    sso {
        authenticationProvider = 'default'
        authenticationAssertionAttribute = 'udcId'
    }
}

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = false

// enable GSP preprocessing: replace head -> g:captureHead, title -> g:captureTitle, meta -> g:captureMeta, body -> g:captureBody
grails.views.gsp.sitemesh.preprocess = true

dataSource {
    dialect = "org.hibernate.dialect.Oracle10gDialect"
    loggingSql = false
}

hibernate {
    dialect = "org.hibernate.dialect.Oracle10gDialect"
    show_sql = false
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.region.factory_class = 'org.hibernate.cache.SingletonEhCacheRegionFactory'
    flush.mode = AUTO
    packagesToScan="net.hedtech"
    config.location = [
            "classpath:hibernate-banner-aip.cfg.xml",
            "classpath:hibernate-banner-core.cfg.xml",
            "classpath:hibernate-banner-general-utility.cfg.xml",
            "classpath:hibernate-banner-general-common.cfg.xml",
            "classpath:hibernate-banner-general-person.cfg.xml",
            "classpath:hibernate-banner-general-validation-common.cfg.xml",
            "classpath:hibernate-banner-sspb.cfg.xml"
    ]
}

// set per-environment serverURL stem for creating absolute links
environments {
    development {
        ssbEnabled = true
        ssbOracleUsersProxied = true
        commmgrDataSourceEnabled = true

    }
    test {
        ssbEnabled = true
        ssbOracleUsersProxied = true
        commmgrDataSourceEnabled = true

    }
    production {

    }
}


aip {
    weblogicDeployment = false

    actionItemPostMonitor {
        enabled = true
        monitorIntervalInSeconds = 10
    }

    actionItemPostWorkProcessingEngine {
        enabled = true
        maxThreads = 1
        maxQueueSize = 5000
        continuousPolling = true
        pollingInterval = 2000
        deleteSuccessfullyCompleted = false
    }

    actionItemJobProcessingEngine {
        enabled = true
        maxThreads = 2
        maxQueueSize = 5000
        continuousPolling = true
        pollingInterval = 2000
        deleteSuccessfullyCompleted = false
    }

    scheduler {
        enabled = true
        idleWaitTime = 30000
    }
}


