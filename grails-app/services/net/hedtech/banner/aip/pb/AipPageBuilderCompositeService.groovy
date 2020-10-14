/*******************************************************************************
 Copyright 2018-2020 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.pb

import grails.gorm.transactions.Transactional
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j


/**
 * Service class for Action item Processing Page Builder
 */
@Transactional
@Slf4j
class AipPageBuilderCompositeService {
    def pageService
    def jsonSlurper = new JsonSlurper()
    def compileService
    def groovyPagesTemplateEngine
    /**
     * Fetch Page Script
     * @return
     */
    def pageScript( pageId ) {
        def compiledJSCode
        def data = pageService.get( pageId )
        def pageName = jsonSlurper.parseText( data.modelView ).name
        def validateResult = compileService.preparePage( data.modelView )
        if (validateResult.valid) {
            compiledJSCode = compileService.compileController( validateResult.pageComponent )
        }
        "var pageId = '${pageName.toString()} ',\n controllerId = 'CustomPageController_${pageName.toString()}',\n CustomPageController_${pageName.toString()} =${compiledJSCode.toString()} ;"
    }

    /**
     * Fetch Page
     * @return
     */
    def page( pageId ) {
        def html
        def data = pageService.get( pageId )
        pageService.compilePage( data )
        def validateResult = compileService.preparePage( data.modelView )
        def pageName = jsonSlurper.parseText( data.modelView ).name
        if (validateResult.valid) {
            def compiledView = compileService.compile2page( validateResult.pageComponent )
            def compiledJSCode = compileService.compileController( validateResult.pageComponent )
            if (data && compiledView && compiledJSCode) {
                html = compileService.assembleFinalPage( compiledView, compiledJSCode )
            }
            def output = new StringWriter()
            try{
                groovyPagesTemplateEngine.clearPageCache()
                groovyPagesTemplateEngine.createTemplate( compiledView, 'test' ).make().writeTo( output )
            }catch(Exception exp){
                log.debug("Error while creating the template compiledView >>>>"+compiledView)
                log.debug("Error while creating the template Exception >>>>" +exp)
            }
            return ['html': output.toString(), 'pageName': pageName, 'script': compiledJSCode.toString(), 'compiled': html]
        }
    }
}
