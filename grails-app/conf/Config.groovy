// configuration for plugin testing - will not be included in the plugin zip

log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
           'org.codehaus.groovy.grails.web.pages', //  GSP
           'org.codehaus.groovy.grails.web.sitemesh', //  layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping', // URL mapping
           'org.codehaus.groovy.grails.commons', // core / classloading
           'org.codehaus.groovy.grails.plugins', // plugins
           'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}


grails.plugin.springsecurity.logout.afterLogoutUrl = "/"
grails.plugin.springsecurity.logout.mepErrorLogoutUrl = '/logout/logoutPage'
grails.plugin.springsecurity.useRequestMapDomainClass = false
grails.plugin.springsecurity.cas.active = false
grails.plugin.springsecurity.saml.active = false

security = {
       active = true
       ajaxLoginFormUrl = "/login/auth"
       loginFormUrl = "/login/auth"
       afterLogoutUrl = "/"
}

grails.plugin.springsecurity.filterChain.chainMap = [
        '/api/**': 'authenticationProcessingFilter,basicAuthenticationFilter,securityContextHolderAwareRequestFilter,anonymousProcessingFilter,basicExceptionTranslationFilter,filterInvocationInterceptor',
        '/**'    : 'securityContextPersistenceFilter,logoutFilter,authenticationProcessingFilter,securityContextHolderAwareRequestFilter,anonymousProcessingFilter,exceptionTranslationFilter,filterInvocationInterceptor'
]

grails.databinding.useSpringBinder = true
grails.plugin.springsecurity.useRequestMapDomainClass = false
grails.plugin.springsecurity.securityConfigType = SecurityConfigType.InterceptUrlMap

