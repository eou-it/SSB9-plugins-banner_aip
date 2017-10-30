/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip

import grails.converters.JSON
import net.hedtech.banner.service.ServiceBase



class ActionItemBlockedProcessService extends ServiceBase {

    private static BLOCKING_CONFIG_NAME = 'json/aipBlock'

    def listBlockedProcessesByType() {

        def ConfigurationData = [:]

        ConfigurationData.id = '1'
        ConfigurationData.name = 'registerForClasses'
        ConfigurationData.type = 'json/aipBlock'
        ConfigurationData.value = '{"aipBlock": {"processNamei18n":"aip.blocked.process.name.register.for.classes","urls":["/ssb/term/termSelection?mode=registration" ] }}'


        return [ConfigurationData]
    }

    def listBlockedProcessesByNameAndType(  String myConfigName ) {

        def ConfigurationData = [:]

        ConfigurationData.id = '1'
        ConfigurationData.name = 'registerForClasses'
        ConfigurationData.type = 'json/aipBlock'
        ConfigurationData.value = '{"aipBlock": {"processNamei18n":"aip.blocked.process.name.register.for.classes","urls":["/ssb/term/termSelection?mode=registration" ] }}'


        def configData = ConfigurationData
        def configDataJson = JSON.parse( configData.value.toString() ) as ConfigObject
        def configProps = configDataJson.toProperties()

        String urlProp = "aipBlock.urls"
        String i18nProp = "aipBlock.processNamei18n"

        def processMap = [ url: configProps[urlProp], processNamei18n:configProps[i18nProp] ]

        return processMap
    }


    def listBlockedActionItems() {
        return ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
    }

    def listBlockedProcessById(Long myId) {
        return ActionItemBlockedProcess.fetchActionItemBlockProcessById( myId )
    }

    def listBlockedProcessByActionItemId(Long myId) {
        return ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId( myId )
    }

    /*todo: future work - add methods for exclude/include configs and url redirect destination for action item list*/

}
