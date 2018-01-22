/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import grails.util.Holders
import net.hedtech.banner.aip.blocking.process.BlockingProcess
import net.hedtech.banner.aip.blocking.process.BlockingProcessUrls
import net.hedtech.banner.i18n.MessageHelper

/**
 * Composite Service for blocking process
 */
class BlockingProcessCompositeService {
    def blockingProcessService
    def blockingProcessUrlService


    def loadBlockingProcessLov() {
        def result = [:]
        def configuredPersona = Holders.config.BANNER_AIP_BLOCK_PROCESS_PERSONA
        Map personaMap = [:]
        List processMap = []
        List processList = blockingProcessService.fetchNonGlobalBlockingProcess()
        List allProcessUrlList = blockingProcessUrlService.fetchUrls()
        configuredPersona.each {
            personaMap.put( it, MessageHelper.message( 'aip.blocking.process.persona.' + it ) )
        }
        processList.each {BlockingProcess it ->
            List processUrlList = allProcessUrlList.findAll {BlockingProcessUrls x ->
                x.processId == it.id
            }
            processUrlList = processUrlList.collect {
                it.processUrl
            }
            processMap.add( [id: it.id, name: it.processName, personAllowed: it.personaAllowedIndicator, ownerCode: it.processOwnerCode, urls: processUrlList] )
        }
        result.persona = personaMap
        result.process = processMap
        result
    }
}
