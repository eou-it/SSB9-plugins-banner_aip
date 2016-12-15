/*******************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip


import grails.converters.JSON
import org.apache.log4j.Logger
import net.hedtech.banner.general.ConfigurationData


class ActionItemBlockedProcessService {

    def listBlockedProcesses() {
        return ConfigurationData.fetchConfigurationData()
    }

    def listBlockedProcessesByName( String myConfigName ) {
        return ConfigurationData.fetchConfigurationDataByName( myConfigName )
    }

}
