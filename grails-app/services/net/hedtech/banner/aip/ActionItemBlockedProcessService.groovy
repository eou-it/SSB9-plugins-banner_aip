/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip

import grails.converters.JSON
import org.apache.log4j.Logger
import net.hedtech.banner.configuration.ConfigurationData
import net.hedtech.banner.aip.ActionItemBlockedProcess



class ActionItemBlockedProcessService {

    def listBlockedProcesses() {
        return ConfigurationData.fetchConfigurationData()
    }

    def listBlockedProcessesByName( String myConfigName ) {
        return ConfigurationData.fetchConfigurationDataByName( myConfigName )
    }

    def listBlockedProcessProps(String myConfigName) {

        def configData = ConfigurationData.fetchConfigurationDataByName( myConfigName )
        def configDataJson = JSON.parse( configData.value.toString() ) as ConfigObject
        def configProps = configDataJson.toProperties()

        String urlProp = "aipBlockedProcess." + myConfigName + ".urls"
        String i18nProp = "aipBlockedProcess." + myConfigName + ".i18n"

        def processMap = [ url: configProps[urlProp], i18n:configProps[i18nProp] ]

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

}
