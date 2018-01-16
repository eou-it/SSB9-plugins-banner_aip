package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersion
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

class ActionItemPostWorkProcessorServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemPostWorkProcessorService
    def actionItemPostCompositeService
    def actionItemProcessingCommonService
    def springSecurityService
    def actionItemPostService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        loginSSB( 'AIPADM001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    // TODO: try posting to bad pidm
    // TODO: try posting to date range overlap
    // TODO: try posting with null actionitemid
    // TODO: post a set of valid items and verify logged info
    // TODO: post a set of items, some violating date range rule, and verify logged info

    // TODO: try posting to date range overlap
    @Test
    // FIXME: not working
    void testPerformGroupSendItem() {
        def user = springSecurityService.getAuthentication()?.user
        ActionItemPostWork actionItemPostWork = new ActionItemPostWork()
        actionItemPostWork.referenceId = 'somestringfortestinglswefhihvciewranc'
        actionItemPostWork.recipientPidm = user.pidm
        actionItemPostWork.currentExecutionState = ActionItemPostWorkExecutionState.Ready
        actionItemPostWork.creationDateTime = new Date()
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
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
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postingActionItemGroupId = actionItemGroup.id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = new Date() + 1
        requestMap.actionItemIds = actionItemIds
        def actionItemPost = actionItemPostCompositeService.getActionPostInstance( requestMap, springSecurityService.getAuthentication()?.user )
        actionItemPost.populationCalculationId = populationVersion.id
        actionItemPost.populationVersionId = populationVersion.id
        actionItemPost
    }
}
