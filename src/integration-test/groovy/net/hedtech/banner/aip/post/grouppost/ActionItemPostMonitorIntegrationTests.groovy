/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersion
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

@Integration
@Rollback
class ActionItemPostMonitorIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostService

    def actionItemPostCompositeService
    def actionItemProcessingCommonService
    def springSecurityService


    @Before
    void setUp() {
        formContext = ['GUAGMNU','SELFSERVICE']
        super.setUp()
        loginSSB( 'AIPADM001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void monitorPosts() {
        ActionItemPostMonitor actionItemPostMonitor = new ActionItemPostMonitor()
        actionItemPostMonitor.setAsynchronousBannerAuthenticationSpoofer( new AsynchronousBannerAuthenticationSpoofer() )
        ActionItemPost aip = newAIP()
        aip.postingCurrentState = ActionItemPostExecutionState.Processing
        aip = actionItemPostService.create( aip )
        ActionItemPostWork actionItemPostWork =
                new ActionItemPostWork( actionItemGroupSend: aip,
                                        currentExecutionState: ActionItemPostWorkExecutionState.Partial,
                                        recipientPidm: springSecurityService.getAuthentication()?.user.pidm )
        actionItemPostWork.save( flush: true )
        actionItemPostMonitor.monitorPosts()
    }


    private def newAIP() {
        getInstance()
    }


    private getInstance() {
        CommunicationPopulation population = CommunicationPopulation.findAllByPopulationName( 'AIP Student Population 1' )[0]
        CommunicationPopulationVersion populationVersion = CommunicationPopulationVersion.findLatestByPopulationId( population.id )
        SimpleDateFormat defaultDateFormat = new SimpleDateFormat()
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset:
                0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal",defaultDateFormat.format( new Date() ))
        correspondingServerDetails.put("timeVal","0330")
        correspondingServerDetails.put("timeZoneVal","(GMT+5:30) Asia/Kolkata")
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postingActionItemGroupId = actionItemGroup.id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.displayDatetimeZone = correspondingServerDetails
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = new Date() + 1
        requestMap.actionItemIds = actionItemIds
        requestMap.populationRegenerateIndicator=false
        def actionItemPost = actionItemPostCompositeService.getActionPostInstance( requestMap, springSecurityService.getAuthentication()?.user )
        actionItemPost.populationCalculationId = populationVersion.id
        actionItemPost.populationVersionId = populationVersion.id
        actionItemPost
    }

}
