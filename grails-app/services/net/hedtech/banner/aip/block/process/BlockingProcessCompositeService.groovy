/*******************************************************************************
 Copyright 2018-2020 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import grails.util.Holders
import net.hedtech.banner.general.ConfigurationData
import net.hedtech.banner.i18n.MessageHelper

/**
 * Composite Service for blocking process
 */
class BlockingProcessCompositeService {
    def blockingProcessService
    def blockingProcessUrlService


    def loadBlockingProcessLov() {
        def result = [:]
        def configData =  Holders.config.BANNER_AIP_BLOCK_PROCESS_PERSONA
        String personaConfigStr = configData
        if(personaConfigStr){
            personaConfigStr = personaConfigStr?.substring(1,personaConfigStr?.length()-1)
        }
        def configuredPersona = personaConfigStr?.split(",")
        Map personaMap = [:]
        List processMap = []
        List processList = blockingProcessService.fetchNonGlobalBlockingProcess()
        List allProcessUrlList = blockingProcessUrlService.fetchUrls()


        configuredPersona.each {
            personaMap.put( it.trim(), MessageHelper.message( 'aip.blocking.process.persona.' + it.trim() ) )
        }
        processList.each {BlockingProcess it ->
            List processUrlList = allProcessUrlList.findAll {BlockingProcessUrls x ->
                x.processId == it.id
            }
            processUrlList = processUrlList.collect {
                it.processUrl
            }
            processMap.add( [id: it.id, name: it.processName, code: it.processCode, personAllowed: it.personaAllowedIndicator, ownerCode: it.processOwnerCode, urls: processUrlList] )
        }
        personaMap = personaMap.sort {it.value}
        result.persona = personaMap
        result.process = processMap
        result
    }
}
