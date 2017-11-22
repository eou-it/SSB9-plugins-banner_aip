/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.ActionItemErrorCode
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
    void createPostItems() {
        ActionItemPost aip = newAIP()
        aip.save()
        // lookup. save should have worked. Use "stopped" to avoid job getting processed?
        List<ActionItemPost> aipToUse = ActionItemPost.findAll(  )
        actionItemPostCompositeService.createPostItems( aipToUse[0] )
        assert actionItemPostWorkService.list( [max: Integer.MAX_VALUE] ).size() > 0
    }

    private def newAIP() {
            new ActionItemPost(
                    populationListId: 1L,
                    populationVersionId: 1L,
                    postingName: "some name",
                    postingActionItemGroupId: ActionItemGroup.findByName('Enrollment').id,
                    postingDeleteIndicator: false,
                    postingScheduleType: "some type",
                    postingCreationDateTime: new Date(),
                    postingDisplayStartDate: new Date(),
                    postingDisplayEndDate: new Date(),
                    postingCreatorId: 'me',
                    postingScheduleDateTime: new Date(),
                    populationRegenerateIndicator: false,
                    postingCurrentState: ActionItemPostExecutionState.New,
                    postingStartedDate: null,
                    postingStopDate: null,
                    aSyncJobId: "la43j45h546k56g6f6r77a7kjfn",
                    populationCalculationId: 1L,
                    postingErrorCode: ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                    postingErrorText: null,
                    aSyncGroupId: null,
                    postingParameterValues: null,
                    lastModified: new Date(),
                    lastModifiedBy: 'testUser',
                    dataOrigin: 'BANNER'
                    )
        }

    private getInstance() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postActionItemGroupId = actionItemGroup.id
        requestMap.postNow = false
        requestMap.recalculateOnPost = false
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = testingDateFormat.format( new Date() )
        requestMap.actionItemIds = actionItemIds
        actionItemPostCompositeService.getActionPostInstance( requestMap )
    }
}
