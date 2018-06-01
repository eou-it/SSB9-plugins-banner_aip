/********************************************************************************
  Copyright 2018 Ellucian Company L.P. and its affiliates.
********************************************************************************/
class BannerAipGrailsPlugin {

    String groupId = "net.hedtech"
    // the plugin version
    def version = "1.0"
    // the version or versions of Grails the plugin is designed for
    def grailsVersion = "2.2.1 > *"
    // resources that are excluded from plugin packaging
    def pluginExcludes = [
        "grails-app/views/error.gsp"
    ]

    def title = "Ellucian AIP Plugin" // Headline display name of the plugin
    def author = "Your name"
    def authorEmail = ""
    def description = '''\
Brief summary/description of the plugin.
'''

    // URL to the plugin's documentation
    def documentation = "http://grails.org/plugin/@plugin.short.name@"

    // Extra (optional) plugin metadata

    // License: one of 'APACHE', 'GPL2', 'GPL3'
//    def license = "APACHE"

    // Details of company behind the plugin (if there is one)
//    def organization = [ name: "My Company", url: "http://www.my-company.com/" ]

    // Any additional developers beyond the author specified above.
//    def developers = [ [ name: "Joe Bloggs", email: "joe@bloggs.net" ]]

    // Location of the plugin's issue tracker.
//    def issueManagement = [ system: "JIRA", url: "http://jira.grails.org/browse/GPMYPLUGIN" ]

    // Online location of the plugin's browseable source code.
//    def scm = [ url: "http://svn.codehaus.org/grails-plugins/" ]

    def doWithWebDescriptor = { xml ->
        // Implement additions to web.xml (optional), this event occurs before
    }

    def doWithSpring = {
        // Implement runtime spring config (optional)
    }

    def doWithDynamicMethods = { ctx ->
        // Implement registering dynamic methods to classes (optional)
    }

    def doWithApplicationContext = { ctx ->
        def pbConfig = grails.util.Holders.getConfig().pageBuilder
        if (pbConfig && pbConfig.locations && pbConfig.locations.page) {
            ctx.cssUtilService.importInitially(ctx.cssUtilService.loadOverwriteExisting)
            ctx.pageUtilService.importInitially(ctx.pageUtilService.loadOverwriteExisting)
            ctx.virtualDomainUtilService.importInitially(ctx.virtualDomainUtilService.loadOverwriteExisting)

            // Install metadata from configured directories
            ctx.pageUtilService.importAllFromDir(pbConfig.locations.page, ctx.pageUtilService.loadIfNew)
            ctx.virtualDomainUtilService.importAllFromDir(pbConfig.locations.virtualDomain, ctx.virtualDomainUtilService.loadIfNew)
            ctx.cssUtilService.importAllFromDir(pbConfig.locations.css, ctx.cssUtilService.loadIfNew)

            //Initialize the request map (page security)
            ctx.pageSecurityService.init()
        }
    }

    def onChange = { event ->
        // Implement code that is executed when any artefact that this plugin is
        // watching is modified and reloaded. The event contains: event.source,
        // event.application, event.manager, event.ctx, and event.plugin.
    }

    def onConfigChange = { event ->
        // Implement code that is executed when the project configuration changes.
        // The event is the same as for 'onChange'.
    }

    def onShutdown = { event ->
        // Implement code that is executed when the application shuts down (optional)
    }
}
