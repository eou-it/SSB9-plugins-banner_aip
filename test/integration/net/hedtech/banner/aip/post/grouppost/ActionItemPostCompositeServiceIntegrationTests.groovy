/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

class ActionItemPostCompositeServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostCompositeService
    def actionItemPostWorkService
    def actionItemProcessingCommonService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void getSysGuId() {
        assert actionItemPostCompositeService.getSysGuId() != 'GHJGHJG'

    }


    @Test
    void createPostItemsModified() {
        ActionItemPost actionItemPost = ActionItemPost.findById( 1L )
        actionItemPostCompositeService.createPostItemsModified( actionItemPost )
        assert actionItemPostWorkService.list( [max: Integer.MAX_VALUE] ).size() > 0
    }


    private getInstance() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def requestMap = [:]
        requestMap.name = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postGroupId = actionItemGroup.id
        requestMap.postNow = false
        requestMap.recalculateOnPost = false
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = testingDateFormat.format( new Date() )
        requestMap.actionItemIds = actionItemIds
        actionItemPostCompositeService.getActionPostInstance( requestMap )
    }
}
