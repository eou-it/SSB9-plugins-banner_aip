/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.time.TimeCategory
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.ActionItemBaseConcurrentTestCase
import net.hedtech.banner.aip.post.job.ActionItemJob
import net.hedtech.banner.general.communication.population.*
import net.hedtech.banner.general.communication.population.query.CommunicationPopulationQuery
import net.hedtech.banner.general.communication.population.query.CommunicationPopulationQueryVersion
import net.hedtech.banner.general.communication.population.selectionlist.CommunicationPopulationSelectionListEntry
import org.apache.commons.logging.LogFactory
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull


class ActionItemGroupSendCompositeServiceConcurrentTests extends ActionItemBaseConcurrentTestCase {

    public final static String TESTING_DATE_FORMAT = 'MM/dd/yyyy'
    public final static String TESTING_DATETIME_FORMAT = 'MM/dd/yyyy HH:mm:ss'

    SimpleDateFormat testingDateFormat = new SimpleDateFormat( TESTING_DATE_FORMAT )
    SimpleDateFormat testingDateTimeFormat = new SimpleDateFormat( TESTING_DATETIME_FORMAT )

    def log = LogFactory.getLog( this.class )

    def selfServiceBannerAuthenticationProvider


    @Before
    void setUp() {
        formContext = ['GUAGMNU', 'SELFSERVICE']
        def auth = selfServiceBannerAuthenticationProvider.authenticate( new UsernamePasswordAuthenticationToken( 'BCMADMIN', '111111' ) )
        SecurityContextHolder.getContext().setAuthentication( auth )
        super.setUp()
        actionItemPostMonitor.startMonitoring()
        actionItemPostWorkProcessingEngine.startRunning()
        actionItemJobProcessingEngine.startRunning()
    }


    @After
    void tearDown() {
        actionItemPostMonitor.shutdown()
        actionItemPostWorkProcessingEngine.stopRunning()
        actionItemJobProcessingEngine.stopRunning()

        super.tearDown()
//        sessionFactory.currentSession?.close()
        logout()
    }


    @Test
    void testPostByPopulationSchedule() {
        println "testPostByPopulationSchedule"
        ActionItemPost groupSend
        //ActionItemGroup aig = actionItemGroupService
        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( newPopulationQuery(
                "testPopForSchedTest" ) )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )
        populationQuery = queryVersion.query

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery,
                "testPopulationForSchedTest" )
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, 'BCMADMIN' )
        assertEquals( populationCalculation.status, CommunicationPopulationCalculationStatus.PENDING_EXECUTION )
        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( it )
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }
        assertTrueWithRetry( isAvailable, populationCalculation.id, 15, 5 )

        List queryAssociations = CommunicationPopulationVersionQueryAssociation.findByPopulationVersion( populationCalculation.populationVersion )
        assertEquals( 1, queryAssociations.size() )

        def selectionListEntryList = CommunicationPopulationSelectionListEntry.fetchBySelectionListId( populationCalculation.selectionList.id )
        assertNotNull( selectionListEntryList )
        assertEquals( 5, selectionListEntryList.size() )

        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]

        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect { it.actionItemId }

        def twoMinutesFromNow
        use( TimeCategory ) {
            twoMinutesFromNow = new Date() + 2.minutes
        }

        println "now" + new Date()
        println "future" + twoMinutesFromNow

        def requestMap = [:]
        requestMap.name = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = population.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postGroupId = actionItemGroup.id
        requestMap.postNow = false
        requestMap.recalculateOnPost = false
        //requestMap.scheduledStartDate = testingDateTimeFormat.format( twoMinutesFromNow )
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = twoMinutesFromNow
        requestMap.actionItemIds = actionItemIds
        groupSend = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap ).savedJob
        assertNotNull( groupSend )

        TimeUnit.SECONDS.sleep( 60 )
        // ActionItemPost should exist. Work should not yet exist (2 minute future schedule)
        assertTrue ActionItemPostWork.fetchByGroupSend( groupSend ).size() == 0
        assertTrue ActionItemPost.findAllByPostingName( 'testPostByPopulationSendInTwoMinutes' ).size() == 1

        // ok, this should pass with db entries in about 60 seconds
        def checkExpectedGroupSendItemsCreated = {
            ActionItemPost each = ActionItemPost.get( it )
            return ActionItemPostWork.fetchByGroupSend( each ).size() == 5
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, groupSend.id, 15, 10 )

        boolean isComplete = sleepUntilPostItemsComplete( groupSend, 60 )
        assertTrue( "items not completed", isComplete )
        // just a little more
        TimeUnit.SECONDS.sleep( 5 )
        int countCompleted = ActionItemPostWork.fetchByExecutionStateAndGroupSend( ActionItemPostWorkExecutionState.Complete, groupSend ).size()
        assertEquals( 5, countCompleted )

        sleepUntilActionItemJobsComplete( 10 * 60 )
        countCompleted = ActionItemJob.fetchCompleted().size()
        assertEquals( 5, countCompleted )

        sleepUntilPostComplete( groupSend, 3 * 60 )

        // test delete group send
        // TODO: send and assert for multiple action items in group
        assertEquals( 1, fetchPostCount( groupSend.id ) )
        assertEquals( 5, fetchPostItemCount( groupSend.id ) )
        assertEquals( 5, ActionItemJob.findAll().size() )
    }


    @Test
    void testRecalculatePopulationForScheduledGroupSend() {
        println "testRecalculatePopulationForScheduledGroupSend"
        ActionItemPost groupSend
        //ActionItemGroup aig = actionItemGroupService
        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( newPopulationQuery(
                "testPopForRegenTest" ) )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )
        populationQuery = queryVersion.query

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery,
                "testPopulationForRegenTest" )
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, 'BCMADMIN' )
        assertEquals( populationCalculation.status, CommunicationPopulationCalculationStatus.PENDING_EXECUTION )
        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( it )
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }
        assertTrueWithRetry( isAvailable, populationCalculation.id, 15, 5 )

        List queryAssociations = CommunicationPopulationVersionQueryAssociation.findByPopulationVersion( populationCalculation.populationVersion )
        assertEquals( 1, queryAssociations.size() )

        def selectionListEntryList = CommunicationPopulationSelectionListEntry.fetchBySelectionListId( populationCalculation.selectionList.id )
        assertNotNull( selectionListEntryList )
        assertEquals( 5, selectionListEntryList.size() )

        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]

        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect { it.actionItemId }

        def twoMinutesFromNow
        use( TimeCategory ) {
            twoMinutesFromNow = new Date() + 2.minutes
        }

        println "now" + new Date()
        println "future" + twoMinutesFromNow

        def requestMap = [:]
        requestMap.name = 'testRecalculatePopulationForScheduledGroupSend'
        requestMap.populationId = population.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postGroupId = actionItemGroup.id
        requestMap.postNow = false
        requestMap.recalculateOnPost = true
        //requestMap.scheduledStartDate = testingDateTimeFormat.format( twoMinutesFromNow )
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = twoMinutesFromNow
        requestMap.actionItemIds = actionItemIds
        groupSend = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap ).savedJob
        assertNotNull( groupSend )
        println "initial pop" + groupSend.populationListId
        println "initial popV" + groupSend.populationVersionId
        TimeUnit.SECONDS.sleep( 60 )
        // ActionItemPost should exist. Work should not yet exist (2 minute future schedule)
        assertTrue ActionItemPostWork.fetchByGroupSend( groupSend ).size() == 0
        assertTrue ActionItemPost.findAllByPostingName( 'testRecalculatePopulationForScheduledGroupSend' ).size() == 1

        // ok, this should pass with db entries in about 60 seconds
        def checkExpectedGroupSendItemsCreated = {
            ActionItemPost each = ActionItemPost.get( it )
            return ActionItemPostWork.fetchByGroupSend( each ).size() == 5
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, groupSend.id, 15, 10 )

        boolean isComplete = sleepUntilPostItemsComplete( groupSend, 60 )
        assertTrue( "items not completed", isComplete )
        // just a little more
        TimeUnit.SECONDS.sleep( 5 )
        // Not a great test. shows new version but should actually test data is different in populations
        println "used pop" + groupSend.populationListId
        println "used popV" + groupSend.populationVersionId
        int countCompleted = ActionItemPostWork.fetchByExecutionStateAndGroupSend( ActionItemPostWorkExecutionState.Complete, groupSend ).size()
        assertEquals( 5, countCompleted )

        sleepUntilActionItemJobsComplete( 10 * 60 )
        countCompleted = ActionItemJob.fetchCompleted().size()
        assertEquals( 5, countCompleted )

        sleepUntilPostComplete( groupSend, 3 * 60 )

        // test delete group send
        // TODO: send and assert for multiple action items in group
        assertEquals( 1, fetchPostCount( groupSend.id ) )
        assertEquals( 5, fetchPostItemCount( groupSend.id ) )
        assertEquals( 5, ActionItemJob.findAll().size() )
    }


    @Test
    void testPostByPopulationSendImmediately() {
        println "testPostByPopulationSendImmediately"
        ActionItemPost groupSend
        //ActionItemGroup aig = actionItemGroupService
        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( newPopulationQuery( "testPop" ) )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )
        populationQuery = queryVersion.query

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery, "testPopulation" )
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, 'BCMADMIN' )
        assertEquals( populationCalculation.status, CommunicationPopulationCalculationStatus.PENDING_EXECUTION )
        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( it )
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }
        assertTrueWithRetry( isAvailable, populationCalculation.id, 15, 5 )

        List queryAssociations = CommunicationPopulationVersionQueryAssociation.findByPopulationVersion( populationCalculation.populationVersion )
        assertEquals( 1, queryAssociations.size() )

        def selectionListEntryList = CommunicationPopulationSelectionListEntry.fetchBySelectionListId( populationCalculation.selectionList.id )
        assertNotNull( selectionListEntryList )
        assertEquals( 5, selectionListEntryList.size() )

        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]

        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect { it.actionItemId }


        def requestMap = [:]
        requestMap.name = 'testPostByPopulationSendImmediately'
        requestMap.populationId = population.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postGroupId = actionItemGroup.id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.scheduledStartDate = null
        requestMap.displayStartDate = testingDateFormat.format(new Date())
        requestMap.displayEndDate = testingDateFormat.format(new Date() + 50)
        requestMap.actionItemIds = actionItemIds
        groupSend = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap ).savedJob
        assertNotNull( groupSend )

        def checkExpectedGroupSendItemsCreated = {
            ActionItemPost each = ActionItemPost.get( it )
            return ActionItemPostWork.fetchByGroupSend( each ).size() == 5
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, groupSend.id, 15, 5 )
        /* TODO: implement these views
        // Confirm group send view returns the correct results
        def sendViewDetails = ActionItemGroupSendDetailView.findAll()
        assertEquals(1, sendViewDetails.size())

        def sendListView = ActionItemGroupSendListView.findAll()
        assertEquals(1, sendListView.size())

        // Confirm group send item view returns the correct results
        def sendItemViewDetails = ActionItemGroupSendItemView.findAll()
        assertEquals(5, sendItemViewDetails.size())
        */
        boolean isComplete = sleepUntilPostItemsComplete( groupSend, 60 )
        assertTrue( "items not completed", isComplete )
        // just a little more
        TimeUnit.SECONDS.sleep( 5 )
        int countCompleted = ActionItemPostWork.fetchByExecutionStateAndGroupSend( ActionItemPostWorkExecutionState.Complete, groupSend ).size()
        assertEquals( 5, countCompleted )

        sleepUntilActionItemJobsComplete( 10 * 60 )
        countCompleted = ActionItemJob.fetchCompleted().size()
        assertEquals( 5, countCompleted )

        sleepUntilPostComplete( groupSend, 3 * 60 )

        // test delete group send
        // TODO: send and assert for multiple action items in group
        assertEquals( 1, fetchPostCount( groupSend.id ) )
        assertEquals( 5, fetchPostItemCount( groupSend.id ) )
        assertEquals( 5, ActionItemJob.findAll().size() )
        // Not yet supported
        /*
        actionItemPostCompositeService.deletePost( groupSend.id )
        assertEquals( 0, fetchPostCount( groupSend.id ) )
        assertEquals( 0, fetchPostItemCount( groupSend.id ) )
        assertEquals( 0, ActionItemJob.findAll().size() )
        */

        /*
         send again. Should not get duplicates
          */
        // monitor thread starts every 15 minutes
        restartMonitor()

        requestMap.name = 'testRepostOfExistingData'
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 40 )
        def groupSend2 = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap ).savedJob
        println groupSend2 // see if id is not changed
        assertNotNull( groupSend2 )

        def checkExpectedGroupSend2ItemsCreated = {
            ActionItemPost each = ActionItemPost.get( it )
            return ActionItemPostWork.fetchByGroupSend( each ).size() == 5
        }
        assertTrueWithRetry( checkExpectedGroupSend2ItemsCreated, groupSend.id, 15, 5 )

        boolean isComplete2 = sleepUntilPostItemsComplete( groupSend, 60 )
        assertTrue( "items not completed", isComplete2 )
        // just a little more
        TimeUnit.SECONDS.sleep( 5 )
        int countAgain2Completed = ActionItemPostWork.fetchByExecutionStateAndGroupSend( ActionItemPostWorkExecutionState.Partial, groupSend2 )
                .size()
        assertEquals( 5, countAgain2Completed )

        sleepUntilActionItemJobsComplete( 10 * 60 )
        countAgain2Completed = ActionItemJob.fetchCompleted().size()
        assertEquals( 10, countAgain2Completed )

        sleepUntilPostComplete( groupSend2, 3 * 60 )

        // test delete group send
        // TODO: send and assert for multiple action items in group
        assertEquals( 1, fetchPostCount( groupSend2.id ) )
        assertEquals( 5, fetchPostItemCount( groupSend2.id ) )
        assertEquals( 10, ActionItemJob.findAll().size() )

        // send again with future dates. Should get new items

        restartMonitor()

        requestMap.name = 'testRepostOfExistingDataDatesInFuture'
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.displayStartDate = testingDateFormat.format( new Date() + 60 )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 70 )
        def groupSend3 = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap ).savedJob
        println groupSend3 // see if id is not changed
        assertNotNull( groupSend3 )

        def checkExpectedGroupSend3ItemsCreated = {
            ActionItemPost each = ActionItemPost.get( it )
            return ActionItemPostWork.fetchByGroupSend( each ).size() == 5
        }
        assertTrueWithRetry( checkExpectedGroupSend3ItemsCreated, groupSend.id, 15, 5 )

        boolean isComplete3 = sleepUntilPostItemsComplete( groupSend, 60 )
        assertTrue( "items not completed", isComplete3 )
        // just a little more
        TimeUnit.SECONDS.sleep( 5 )
        int countAgain3Completed = ActionItemPostWork.fetchByExecutionStateAndGroupSend( ActionItemPostWorkExecutionState.Complete,
                groupSend3 ).size()
        assertEquals( 5, countAgain3Completed )

        sleepUntilActionItemJobsComplete( 10 * 60 )
        countAgain3Completed = ActionItemJob.fetchCompleted().size()
        assertEquals( 15, countAgain3Completed )

        sleepUntilPostComplete( groupSend3, 3 * 60 )

        // test delete group send
        // TODO: send and assert for multiple action items in group
        assertEquals( 1, fetchPostCount( groupSend3.id ) )
        assertEquals( 5, fetchPostItemCount( groupSend3.id ) )
        assertEquals( 15, ActionItemJob.findAll().size() )

        // send again with bad data to trigger errors
    }


    @Test
    void testEmptyPopulationSentImmediately() {
        println "testEmptyPopulationSentImmediately"

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulation( defaultFolder, "testPopulation", "testPopulation description" )
        CommunicationPopulationDetail populationDetail = communicationPopulationCompositeService.fetchPopulationDetail( population.id )
        assertEquals( 0, populationDetail.totalCount )

        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]

        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect { it.actionItemId }

        def requestMap = [:]
        requestMap.name = "testEmptyPopulationSendImmediately"
        requestMap.populationId = population.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postGroupId = actionItemGroup.id
        requestMap.actionItemIds = actionItemIds
        requestMap.displayStartDate = testingDateFormat.format( new Date() + 60 )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 70 )
        requestMap.postNow = true
        requestMap.recalculateOnSend = false

        ActionItemPost groupSend = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap ).savedJob

        assertNotNull( groupSend )
        sleepUntilPostComplete( groupSend, 3 * 60 )
        assertEquals( 0, ActionItemPostWork.count() )
    }
}