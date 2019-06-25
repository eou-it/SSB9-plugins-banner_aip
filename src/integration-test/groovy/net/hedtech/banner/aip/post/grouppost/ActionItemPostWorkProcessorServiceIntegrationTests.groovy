/********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

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
class ActionItemPostWorkProcessorServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemPostWorkProcessorService

    def actionItemPostCompositeService

    def actionItemProcessingCommonService

    def springSecurityService

    def actionItemPostService

    def actionItemPostDetailService



    @Before
    void setUp() {
        formContext = ['SELFSERVICE']
        super.setUp()
        loginSSB( 'AIPADM001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test

    void testPerformGroupSendItem() {
        actionItemPostWorkProcessorService.setAsynchronousBannerAuthenticationSpoofer( new AsynchronousBannerAuthenticationSpoofer() )
        def user = springSecurityService.getAuthentication()?.user
        ActionItemPostWork actionItemPostWork = new ActionItemPostWork()
        actionItemPostWork.referenceId = 'somestringfortestinglswefhihvciewranc'
        actionItemPostWork.recipientPidm = user.pidm
        actionItemPostWork.currentExecutionState = ActionItemPostWorkExecutionState.Ready
        actionItemPostWork.creationDateTime = new Date()
        Map obj = newAIP()
        ActionItemPost aip = obj.instance
        aip = actionItemPostService.create( aip )
        obj.actionItemIds.each {
            ActionItemPostDetail groupDetail = new ActionItemPostDetail(
                    actionItemPostId: aip.id,
                    actionItemId: it
            )
            actionItemPostDetailService.create( groupDetail )
        }
        actionItemPostCompositeService.createPostItems( aip )
        aip.postingCurrentState = ActionItemPostExecutionState.New
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork.save( flush: true )
        actionItemPostWorkProcessorService.performPostItem( actionItemPostWork )
    }


    private def newAIP() {
        getInstance()
    }


    private getInstance() {
        CommunicationPopulation population = CommunicationPopulation.findAllByPopulationName( 'AIP Student Population 1' )[0]
        CommunicationPopulationVersion populationVersion = CommunicationPopulationVersion.findLatestByPopulationId( population.id )

        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset:
                0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect { it.actionItemId }
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal","06/21/2018")
        correspondingServerDetails.put("timeVal","0330")
        correspondingServerDetails.put("timeZoneVal","(GMT+5:30) Asia/Kolkata")
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postingActionItemGroupId = actionItemGroup.id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.displayDatetimeZone = correspondingServerDetails
        requestMap.scheduledStartDate = new Date() + 1
        requestMap.actionItemIds = actionItemIds
        def actionItemPost = actionItemPostCompositeService.getActionPostInstance( requestMap, springSecurityService.getAuthentication()?.user )
        actionItemPost.populationCalculationId = populationVersion.id
        actionItemPost.populationVersionId = populationVersion.id
        actionItemPost
        [instance: actionItemPost, actionItemIds: actionItemIds]
    }
}
