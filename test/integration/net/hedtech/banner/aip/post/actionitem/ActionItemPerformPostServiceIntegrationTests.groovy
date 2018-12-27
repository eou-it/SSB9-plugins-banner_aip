/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.actionitem

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.grouppost.ActionItemPost
import net.hedtech.banner.aip.post.grouppost.ActionItemPostDetail
import net.hedtech.banner.aip.post.grouppost.ActionItemPostWork
import net.hedtech.banner.aip.post.grouppost.ActionItemPostWorkExecutionState
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersion
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

class ActionItemPerformPostServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostCompositeService
    def actionItemPostService
    def actionItemPostWorkService
    def actionItemProcessingCommonService
    def springSecurityService
    def actionItemPerformPostService
    def userActionItemService
    def actionItemPostDetailService

    private static final String USERNAME = 'AIPADM001'


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        loginSSB( USERNAME, '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void createPostItems() {
        ActionItemPost aip = getInstance()
        ActionItemPost newAip = actionItemPostService.create( aip )
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        actionItemIds.each {
            ActionItemPostDetail groupDetail = new ActionItemPostDetail(
                    actionItemPostId: aip.id,
                    actionItemId: it
            )
            actionItemPostDetailService.create( groupDetail )
        }

        println newAip
        actionItemPostCompositeService.createPostItems( newAip )

        ActionItemPostWork actionItemPostWork2 = ActionItemPostWork.fetchByGroupSend( newAip, 1 )[0]

        println "post 2"
        println actionItemPostWork2

        ActionItemPostWork actionItemPostWork = actionItemPostWorkService.list( [max: 1] ).get( 0 )

        println "post work"
        println actionItemPostWork
        def result = actionItemPerformPostService.postActionItems( actionItemPostWork )
        assert userActionItemService.list( [max: Integer.MAX_VALUE] ).size() > 0
        assertEquals result.id, actionItemPostWork.id
        assertEquals result.version, actionItemPostWork.version
        assertNotEquals( actionItemPostWork.currentExecutionState, ActionItemPostWorkExecutionState.Ready.name() )

    }


    private getInstance() {
        CommunicationPopulation population = CommunicationPopulation.findAllByPopulationName( 'AIP Student Population 1' )[0]
        CommunicationPopulationVersion populationVersion = CommunicationPopulationVersion.findLatestByPopulationId( population.id )
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postingActionItemGroupId = ActionItemGroup.fetchActionItemGroups()[0].id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.displayDatetimeZone = "06/21/2018 0330 (GMT+5:30) Asia/Kolkata"
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = new Date()
        def actionItemPost = actionItemPostCompositeService.getActionPostInstance( requestMap, springSecurityService.getAuthentication()?.user )
        actionItemPost.populationCalculationId = populationVersion.id
        actionItemPost.populationVersionId = populationVersion.id
        actionItemPost
    }
}
