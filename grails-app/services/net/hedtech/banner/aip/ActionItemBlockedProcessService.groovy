/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip

import grails.converters.JSON
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action item Blocked Process
 */
class ActionItemBlockedProcessService extends ServiceBase {
    /**
     * Lists blocked process by Type
     * @return
     */
    def listBlockedProcessesByType() {
        [[id   : 1,
          name : 'registerForClasses',
          type : 'json/aipBlock',
          value: '{"aipBlock": {"processNamei18n":"aip.blocked.process.name.register.for.classes","urls":["/ssb/term/termSelection?mode=registration" ] }}']]
    }

    /**
     *
     * @param myConfigName
     * @return
     */
    def listBlockedProcessesByNameAndType( String myConfigName ) {
        def configData = [id   : 1,
                          name : 'registerForClasses',
                          type : 'json/aipBlock',
                          value: '{"aipBlock": {"processNamei18n":"aip.blocked.process.name.register.for.classes","urls":["/ssb/term/termSelection?mode=registration" ] }}']
        def configDataJson = JSON.parse( configData.value.toString() ) as ConfigObject
        def configProps = configDataJson.toProperties()
        [url: configProps["aipBlock.urls"], processNamei18n: configProps["aipBlock.processNamei18n"]]
    }

    /**
     *
     * @return
     */
    def listBlockedActionItems() {
        ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
    }

    /**
     *
     * @param myId
     * @return
     */
    def listBlockedProcessById( Long myId ) {
        ActionItemBlockedProcess.fetchActionItemBlockProcessById( myId )
    }

    /**
     *
     * @param myId
     * @return
     */
    def listBlockedProcessByActionItemId( Long myId ) {
        ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId( myId )
    }
}
