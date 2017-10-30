/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.ActionItemBaseConcurrentTestCase
import net.hedtech.banner.aip.post.job.ActionItemJob
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculationStatus
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersionQueryAssociation
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
    SimpleDateFormat testingFormat = new SimpleDateFormat(TESTING_DATE_FORMAT)

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

        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}

        def requestMap = [:]
        requestMap.name = 'testPostByPopulationSendImmediately'
        requestMap.populationId = population.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postGroupId = actionItemGroup.id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.scheduledStartDate = null
        requestMap.displayStartDate = testingFormat.format(new Date())
        requestMap.displayEndDate = testingFormat.format(new Date() + 50)
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
        TimeUnit.SECONDS.sleep( 5 );
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
        requestMap.displayEndDate = testingFormat.format( new Date() + 40 )
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
        TimeUnit.SECONDS.sleep( 5 );
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
        requestMap.displayStartDate = testingFormat.format( new Date() + 60 )
        requestMap.displayEndDate = testingFormat.format( new Date() + 70 )
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
        TimeUnit.SECONDS.sleep( 5 );
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

    /*
    @Test
    void testDeletePopulationWithGroupSend() {
        println "testDeletePopulationWithGroupSend"

        CommunicationGroupSend groupSend
        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( newPopulationQuery("testDeletePopulationWithGroupSend Query") )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery, "testDeletePopulationWithGroupSend Population" )
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, 'BCMADMIN' )
        assertEquals( populationCalculation.status, CommunicationPopulationCalculationStatus.PENDING_EXECUTION )
        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( it )
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }
        assertTrueWithRetry( isAvailable, populationCalculation.id, 15, 5 )

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testDeleteGroupSend",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                recalculateOnSend: false
        )

        groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication( request )
        assertNotNull(groupSend)
        assertEquals( 1, fetchGroupSendCount( groupSend.id ) )

        try {
            communicationPopulationCompositeService.deletePopulation( population )
            fail( "Expected cannotDeletePopulationWithExistingGroupSends" )
        } catch (ApplicationException e) {
            assertEquals( "@@r1:cannotDeletePopulationWithExistingGroupSends@@", e.getMessage() )
        }

        sleepUntilGroupSendComplete( groupSend, 120 )

        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        communicationPopulationCompositeService.deletePopulation( population )

        assertEquals( 0, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 0, fetchGroupSendItemCount( groupSend.id ) )
    }

    @Test
    void testDeletePopulationWithGroupSendWaitingRecalculation() {
        println "testDeletePopulationWithGroupSendWaitingRecalculation"

        CommunicationGroupSend groupSend
        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( newPopulationQuery("testDeletePopulationWithGroupSendWaitingRecalculation Query") )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery, "testDeletePopulationWithGroupSendWaitingRecalculation Population" )
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, 'BCMADMIN' )
        assertEquals( populationCalculation.status, CommunicationPopulationCalculationStatus.PENDING_EXECUTION )
        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( it )
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }
        assertTrueWithRetry( isAvailable, populationCalculation.id, 15, 5 )

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testDeleteGroupSend",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                recalculateOnSend: true
        )

        groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication( request )
        assertNotNull(groupSend)
        assertEquals( 1, fetchGroupSendCount( groupSend.id ) )

        try {
            communicationPopulationCompositeService.deletePopulation( population )
            fail( "Expected cannotDeletePopulationWithExistingGroupSends" )
        } catch (ApplicationException e) {
            assertEquals( "@@r1:cannotDeletePopulationWithExistingGroupSends@@", e.getMessage() )
        }

        sleepUntilGroupSendComplete( groupSend, 120 )

        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        communicationPopulationCompositeService.deletePopulation( population )

        assertEquals( 0, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 0, fetchGroupSendItemCount( groupSend.id ) )
    }

    @Test
    void testRecalculatePopulationAfterScheduledGroupSend() {
        println "testRecalculatePopulationAfterScheduledGroupSend"

        CommunicationGroupSend groupSend
        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( newPopulationQuery("testRecalculatePopulationAfterScheduledGroupSend Query") )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery, "testRecalculatePopulationAfterScheduledGroupSend Population" )
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, 'BCMADMIN' )
        assertEquals( populationCalculation.status, CommunicationPopulationCalculationStatus.PENDING_EXECUTION )
        Long populationCalculationId = populationCalculation.id
        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( populationCalculationId )
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }
        assertTrueWithRetry( isAvailable, null, 15, 5 )

        Calendar now = Calendar.getInstance()
        now.add(Calendar.HOUR, 1)

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testRecalculatePopulationAfterScheduledGroupSend",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                scheduledStartDate: now.getTime(),
                recalculateOnSend: true
        )

        groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication( request )
        assertNotNull(groupSend)
        assertEquals( 1, fetchGroupSendCount( groupSend.id ) )

        populationCalculation = communicationPopulationCompositeService.calculatePopulationVersionForUser( populationCalculation.populationVersion )
        populationCalculationId = populationCalculation.id
        assertTrueWithRetry( isAvailable, null, 15, 5 )

        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        communicationPopulationCompositeService.deletePopulation( population )

        assertEquals( 0, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 0, fetchGroupSendItemCount( groupSend.id ) )
    }

    @Test
    void testFindRunning() {
        println "testFindRunning"

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(createGroupSendRequest( "testFindRunning1" ))
        assertNotNull(groupSend)
        CommunicationGroupSend groupSendB = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(createGroupSendRequest( "testFindRunning2" ))
        assertNotNull(groupSendB)
        CommunicationGroupSend groupSendC = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(createGroupSendRequest( "testFindRunning3" ))
        assertNotNull(groupSendC)

        List runningList = CommunicationGroupSend.findRunning()
        assertEquals( 3, runningList.size() )

        def allDone = {
            return CommunicationGroupSend.findRunning().size() == 0
        }
        assertTrueWithRetry( allDone, null, 10, 10 )
    }

    @Test
    void testStopStoppedGroupSend() {
        println "testStopStoppedGroupSend"

        CommunicationGroupSendRequest request = createGroupSendRequest( "testStopStoppedGroupSend" )
        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        groupSend = communicationGroupSendCompositeService.stopGroupSend( groupSend.id )
        assertTrue( groupSend.currentExecutionState.equals( CommunicationGroupSendExecutionState.Stopped ) )
        assertTrue( groupSend.currentExecutionState.isTerminal() )

        List runningList = CommunicationGroupSend.findRunning()
        assertEquals( 0, runningList.size() )

        try{
            communicationGroupSendCompositeService.stopGroupSend( groupSend.id )
            fail( "Shouldn't be able to stop a group send that has already concluded." )
        } catch( ApplicationException e ) {
            assertEquals( "@@r1:cannotStopConcludedGroupSend@@", e.getWrappedException().getMessage() )
        }
    }

    @Test
    void testStopCompletedGroupSend() {
        println "testStopCompletedGroupSend"

        CommunicationGroupSendRequest request = createGroupSendRequest( "testStopCompletedGroupSend" )
        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        sleepUntilGroupSendItemsComplete( groupSend, 30 )

        groupSend = completeGroupSend(groupSend.id)
        assertTrue( groupSend.currentExecutionState.equals( CommunicationGroupSendExecutionState.Complete ) )

        List runningList = CommunicationGroupSend.findRunning()
        assertEquals(0, runningList.size())

        try {
            communicationGroupSendCompositeService.stopGroupSend(groupSend.id)
            fail("Shouldn't be able to stop a group send that has already concluded.")
        } catch (ApplicationException e) {
            assertEquals( "@@r1:cannotStopConcludedGroupSend@@", e.getWrappedException().getMessage() )
        }
    }

    @Test
    void testScheduledPopulationGroupSend() {
        println "testScheduledPopulationGroupSend"

        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery(newPopulationQuery("testPop"))
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
        assertNotNull(selectionListEntryList)
        assertEquals(5, selectionListEntryList.size())

        Calendar now = Calendar.getInstance()
        now.add(Calendar.SECOND, 10)

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testScheduledRecalculatePopulationGroupSend",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                scheduledStartDate: now.getTime(),
                recalculateOnSend: false
        )

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        def checkExpectedGroupSendItemsCreated = {
            CommunicationGroupSend each = CommunicationGroupSend.get( it )
            return CommunicationGroupSendItem.fetchByGroupSend( each ).size() == 5
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, groupSend.id, 15, 5 )

        // Confirm group send view returns the correct results
        def sendViewDetails = CommunicationGroupSendDetailView.findAll()
        assertEquals(1, sendViewDetails.size())

        def sendListView = CommunicationGroupSendListView.findAll()
        assertEquals(1, sendListView.size())


        // Confirm group send item view returns the correct results
        def sendItemViewDetails = CommunicationGroupSendItemView.findAll()
        assertEquals(5, sendItemViewDetails.size())

        sleepUntilGroupSendItemsComplete( groupSend, 60 )

        int countCompleted = CommunicationGroupSendItem.fetchByCompleteExecutionStateAndGroupSend( groupSend ).size()
        assertEquals( 5, countCompleted )

        sleepUntilCommunicationJobsComplete( 10 * 60 )
        countCompleted = CommunicationJob.fetchCompleted().size()
        assertEquals( 5, countCompleted )

        sleepUntilGroupSendComplete( groupSend, 3 * 60 )

        // test delete group send
        assertEquals( 1, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 5, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 5, CommunicationJob.findAll().size() )
        assertEquals( 5, CommunicationRecipientData.findAll().size() )
        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        assertEquals( 0, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 0, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 0, CommunicationJob.findAll().size() )
        assertEquals( 0, CommunicationRecipientData.findAll().size() )
    }

    @Test
    void testScheduledRecalculatePopulationGroupSend() {
        println "testScheduledRecalculatePopulationGroupSend"

        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery(newPopulationQuery("testPop"))
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

        assertEquals( 1, CommunicationPopulationVersion.count() )
        List queryAssociations = CommunicationPopulationVersionQueryAssociation.findByPopulationVersion( populationCalculation.populationVersion )
        assertEquals( 1, queryAssociations.size() )

        def selectionListEntryList = CommunicationPopulationSelectionListEntry.fetchBySelectionListId( populationCalculation.selectionList.id )
        assertNotNull(selectionListEntryList)
        assertEquals(5, selectionListEntryList.size())

        Calendar now = Calendar.getInstance()
        now.add(Calendar.SECOND, 10)

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testScheduledRecalculatePopulationGroupSend",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                scheduledStartDate: now.getTime(),
                recalculateOnSend: true
        )

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        Long groupSendId = groupSend.id
        def checkExpectedGroupSendItemsCreated = {
            CommunicationGroupSend each = CommunicationGroupSend.get( groupSendId )
            List groupSendItemList = CommunicationGroupSendItem.fetchByGroupSend( each )
            return groupSendItemList.size() == 5
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, null, 15, 5 )

        // Confirm group send view returns the correct results
        def sendViewDetails = CommunicationGroupSendDetailView.findAll()
        assertEquals(1, sendViewDetails.size())

        def sendListView = CommunicationGroupSendListView.findAll()
        assertEquals(1, sendListView.size())

        // Confirm group send item view returns the correct results
        def sendItemViewDetails = CommunicationGroupSendItemView.findAll()
        assertEquals(5, sendItemViewDetails.size())

        sleepUntilGroupSendItemsComplete( groupSend, 60 )

        int countCompleted = CommunicationGroupSendItem.fetchByCompleteExecutionStateAndGroupSend( groupSend ).size()
        assertEquals( 5, countCompleted )

        sleepUntilCommunicationJobsComplete( 10 * 60 )
        countCompleted = CommunicationJob.fetchCompleted().size()
        assertEquals( 5, countCompleted )

        sleepUntilGroupSendComplete( groupSend, 3 * 60 )

        // test delete group send
        assertEquals( 1, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 5, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 5, CommunicationJob.findAll().size() )
        assertEquals( 5, CommunicationRecipientData.findAll().size() )
        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        assertEquals( 0, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 0, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 0, CommunicationJob.findAll().size() )
        assertEquals( 0, CommunicationRecipientData.findAll().size() )
    }

/*    @Test
    void testMediumPopulationAndDelete() {
        println "testMediumPopulationAndDelete"

        // 0) Test parameters
        String testName = "testMediumPopulationAndDelete"
        String testUserId = 'BCMADMIN'
        final int targetSize = 500

        // 1)Generate such a population which will fetch large number of people from the system (in the range 2000-4000 etc.)
        CommunicationPopulationQuery populationQuery = new CommunicationPopulationQuery(
            folder: defaultFolder,
            name: testName,
            queryString: "SELECT spriden_pidm FROM spriden, spbpers WHERE spriden_change_ind IS NULL AND spriden_pidm = spbpers_pidm AND rownum <= ${targetSize}"
        )
        populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( populationQuery )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )
        populationQuery = queryVersion.query

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery, testName )
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, testUserId )
        assertNotNull( populationCalculation )

        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( it )
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }
        assertTrueWithRetry( isAvailable, populationCalculation.id, 15, 5 )

        populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, testUserId )
        assertEquals( targetSize, populationCalculation.calculatedCount )

        // 2)Using this population send any Email template.
        // Wait on the Open com job page till status becomes "Processing" and at least few items got completed.
        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
            name: testName,
            populationId: population.id,
            templateId: defaultEmailTemplate.id,
            organizationId: defaultOrganization.id,
            referenceId: UUID.randomUUID().toString(),
            recalculateOnSend: false
        )

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication( request )
        assertNotNull( groupSend )

        Map pagingAndSortParams = [sortColumn: "lastName", sortDirection: "desc", max: 10, offset: 0]
        Map filterData = [params: ["jobId": groupSend.id, "name": '%', "status": CommunicationGroupSendItemExecutionState.Complete.toString()]]
        def isProcessingAndAFewItemsCompleted = {
            PagedResultList results = CommunicationGroupSendItemView.findByNameWithPagingAndSortParams(filterData, pagingAndSortParams)
//            println "total count = ${results.totalCount} + return = " + (results.totalCount >= 3)
            return results.totalCount >= 3
        }
        assertTrueWithRetry( isProcessingAndAFewItemsCompleted, null, 30, 2 )
        // 3)When few items are completed and rest of them are still processing,
        // navigate to populations list page and Open large population used in above mentioned steps.
        pagingAndSortParams = [sortColumn: "lastCalculatedTime", sortDirection: "desc", max: 10, offset: 0]
        filterData = [params: ["populationName": '%', "createdBy": testUserId]]
        PagedResultList results = communicationPopulationCompositeService.findByNameWithPagingAndSortParams(filterData, pagingAndSortParams)
        assertEquals( 1, results.size() )
        CommunicationPopulationListView populationListView = results.get( 0 )
        groupSend.refresh()
        assertEquals( groupSend.populationId, populationListView.id )
        assertEquals( groupSend.populationCalculationId, populationListView.populationCalculationId )

        // 4)Now try to "Delete" this population for which com job is still processing.
        try {
            communicationPopulationCompositeService.deletePopulation( populationListView.id, populationListView.version )
            fail( "Expected cannotDeletePopulationWithExistingGroupSends" )
        } catch( ApplicationException e ) {
            // 5)Error message displays. Ignore or acknowledge that message so that it will disappear.
            assertEquals( e.message, "@@r1:cannotDeletePopulationWithExistingGroupSends@@" )
        }

        // 6)Then refresh the page using browser control and click on "Regenerate" immediately.
        // Some technical error messages may display ignore them and close them. Again attempt to "Delete", refresh page using browser control. Repeat it 3-4 times doing quick actions.
        populationListView = CommunicationPopulationListView.fetchLatestByPopulationIdAndUserId( populationListView.id, testUserId )
        populationCalculation = communicationPopulationCompositeService.calculatePopulationForUser( populationListView.id, populationListView.version, testUserId )

        try {
            communicationPopulationCompositeService.deletePopulation( populationListView.id, populationListView.version )
            fail( "Expected cannotDeletePopulationWithExistingGroupSends" )
        } catch( ApplicationException e ) {
            // 5)Error message displays. Ignore or acknowledge that message so that it will disappear.
            assertEquals( "@@r1:cannotDeletePopulationWithExistingGroupSends@@", e.message )
        }
    }
*/
    /*
    private CommunicationGroupSendRequest createGroupSendRequest( String defaultName ) {
        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery(newPopulationQuery( defaultName ))
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )
        populationQuery = queryVersion.query

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery, defaultName )
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
        assertNotNull(selectionListEntryList)
        assertEquals(5, selectionListEntryList.size())

        Calendar now = Calendar.getInstance()
        now.add(Calendar.SECOND, 3)

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
            name: defaultName,
            populationId: population.id,
            templateId: defaultEmailTemplate.id,
            organizationId: defaultOrganization.id,
            referenceId: UUID.randomUUID().toString(),
            scheduledStartDate: now.getTime(),
            recalculateOnSend: false
        )

        return request
    }

    @Test
    void testDeleteGroupSend() {
        println "testDeleteGroupSend"

        testDeleteGroupSend( defaultEmailTemplate )
    }

    @Test
    void testEmptyPopulationSentImmediately() {
        println "testEmptyPopulationSentImmediately"

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulation( defaultFolder, "testPopulation", "testPopulation description" )
        CommunicationPopulationDetail populationDetail = communicationPopulationCompositeService.fetchPopulationDetail( population.id )
        assertEquals( 0, populationDetail.totalCount )

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testGroupSendRequestByTemplateByPopulationSendImmediately",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                recalculateOnSend: false
        )

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)
        sleepUntilGroupSendComplete( groupSend, 3 * 60 )
        assertEquals( 0, CommunicationGroupSendItem.count() )
    }

    @Test
    void testGroupSendWithManualIncludeSentImmediately() {
        println "testGroupSendWithManualIncludeSentImmediately"

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulation( defaultFolder, "testPopulation", "testPopulation description" )
        communicationPopulationCompositeService.addPersonsToIncludeList( population, ['BCMADMIN', 'BCMUSER', 'BCMAUTHOR'] )
        CommunicationPopulationDetail populationDetail = communicationPopulationCompositeService.fetchPopulationDetail( population.id )
        assertNotNull( populationDetail.populationListView )
        assertEquals( population.id, populationDetail.populationListView.id )
        assertEquals( 3, populationDetail.totalCount )

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testGroupSendRequestByTemplateByPopulationSendImmediately",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                recalculateOnSend: false
        )

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        def checkExpectedGroupSendItemsCreated = {
            CommunicationGroupSend each = CommunicationGroupSend.get( it )
            return CommunicationGroupSendItem.fetchByGroupSend( each ).size() == 3
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, groupSend.id, 15, 5 )

        // Confirm group send view returns the correct results
        def sendViewDetails = CommunicationGroupSendDetailView.findAll()
        assertEquals(1, sendViewDetails.size())

        def sendListView = CommunicationGroupSendListView.findAll()
        assertEquals(1, sendListView.size())

        // Confirm group send item view returns the correct results
        def sendItemViewDetails = CommunicationGroupSendItemView.findAll()
        assertEquals(3, sendItemViewDetails.size())

        sleepUntilGroupSendItemsComplete( groupSend, 60 )

        int countCompleted = CommunicationGroupSendItem.fetchByCompleteExecutionStateAndGroupSend( groupSend ).size()
        assertEquals( 3, countCompleted )

        sleepUntilCommunicationJobsComplete( 10 * 60 )
        countCompleted = CommunicationJob.fetchCompleted().size()
        assertEquals( 3, countCompleted )

        sleepUntilGroupSendComplete( groupSend, 3 * 60 )

        // test delete group send
        assertEquals( 1, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 3, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 3, CommunicationJob.findAll().size() )
        assertEquals( 3, CommunicationRecipientData.findAll().size() )
        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        assertEquals( 0, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 0, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 0, CommunicationJob.findAll().size() )
        assertEquals( 0, CommunicationRecipientData.findAll().size() )
    }

    @Test
    void testGroupSendWithBothManualIncludeAndQuerySentImmediately() {
        println "testGroupSendWithBothManualIncludeAndQuerySentImmediately"

        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery(newPopulationQuery("testPop"))
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

        communicationPopulationCompositeService.addPersonsToIncludeList( population, ['BCMADMIN', 'BCMUSER', 'BCMAUTHOR'] )
        CommunicationPopulationDetail populationDetail = communicationPopulationCompositeService.fetchPopulationDetail( population.id )
        assertNotNull( populationDetail.populationListView )
        assertEquals( population.id, populationDetail.populationListView.id )
        assertEquals( 8, populationDetail.totalCount )

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testGroupSendRequestByTemplateByPopulationSendImmediately",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                recalculateOnSend: false
        )

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        def checkExpectedGroupSendItemsCreated = {
            CommunicationGroupSend each = CommunicationGroupSend.get( it )
            return CommunicationGroupSendItem.fetchByGroupSend( each ).size() == 8
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, groupSend.id, 15, 5 )

        // Confirm group send view returns the correct results
        def sendViewDetails = CommunicationGroupSendDetailView.findAll()
        assertEquals(1, sendViewDetails.size())

        def sendListView = CommunicationGroupSendListView.findAll()
        assertEquals(1, sendListView.size())

        // Confirm group send item view returns the correct results
        def sendItemViewDetails = CommunicationGroupSendItemView.findAll()
        assertEquals(8, sendItemViewDetails.size())


        sleepUntilGroupSendItemsComplete( groupSend, 60 )

        int countCompleted = CommunicationGroupSendItem.fetchByCompleteExecutionStateAndGroupSend( groupSend ).size()
        assertEquals( 8, countCompleted )

        sleepUntilCommunicationJobsComplete( 10 * 60 )
        countCompleted = CommunicationJob.fetchCompleted().size()
        assertEquals( 8, countCompleted )

        sleepUntilGroupSendComplete( groupSend, 8 * 60 )

        assertEquals( 8, CommunicationRecipientData.findAll().size() )
        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        assertEquals( 0, CommunicationRecipientData.findAll().size() )
    }


    @Test
    void testGroupSendWithBothManualIncludeAndQueryScheduledRecalculate() {
        println "testGroupSendWithBothManualIncludeAndQueryScheduledRecalculate"

        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery(newPopulationQuery("testPop"))
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

        communicationPopulationCompositeService.addPersonsToIncludeList( population, ['BCMADMIN', 'BCMUSER', 'BCMAUTHOR'] )
        CommunicationPopulationDetail populationDetail = communicationPopulationCompositeService.fetchPopulationDetail( population.id )
        assertNotNull( populationDetail.populationListView )
        assertEquals( population.id, populationDetail.populationListView.id )
        assertEquals( 8, populationDetail.totalCount )
        population.refresh()
        assertTrue( population.changesPending )

        Calendar now = Calendar.getInstance()
        now.add(Calendar.SECOND, 10)

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testGroupSendRequestByTemplateByPopulationSendImmediately",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                scheduledStartDate: now.getTime(),
                recalculateOnSend: true
        )
        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        // Wait for items to be created
        Long groupSendId = groupSend.id
        def checkExpectedGroupSendItemsCreated = {
            CommunicationGroupSend each = CommunicationGroupSend.get( groupSendId )
            return CommunicationGroupSendItem.countByCommunicationGroupSend( each ) == 8
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, null, 15, 5 )

        // Wait for items to be done
        sleepUntilGroupSendItemsComplete( groupSend, 60 )
        assertEquals( 8, CommunicationGroupSendItem.fetchByCompleteExecutionState().size() )
        // Wait for jobs to be done
        sleepUntilCommunicationJobsComplete( 10 * 60 )
        assertEquals( 8, CommunicationJob.fetchCompleted().size() )
        // Wait for overall group send status to be complete
        sleepUntilGroupSendComplete( groupSend, 8 * 60 )

        population.refresh()
        assertFalse( population.changesPending )
    }

    @Test
    void testGroupSendWithManualIncludeAndQueryScheduledRecalculate() {
        println "testGroupSendWithManualIncludeAndQueryScheduledRecalculate"

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulation( defaultFolder, "testPopulation", "testPopulation description" )
        communicationPopulationCompositeService.addPersonsToIncludeList( population, ['BCMADMIN', 'BCMUSER', 'BCMAUTHOR'] )
        CommunicationPopulationDetail populationDetail = communicationPopulationCompositeService.fetchPopulationDetail( population.id )
        assertNotNull( populationDetail.populationListView )
        assertEquals( population.id, populationDetail.populationListView.id )
        assertEquals( 3, populationDetail.totalCount )

        Calendar now = Calendar.getInstance()
        now.add(Calendar.SECOND, 10)

        CommunicationGroupSendRequest request = new CommunicationGroupSendRequest(
                name: "testGroupSendRequestByTemplateByPopulationSendImmediately",
                populationId: population.id,
                templateId: defaultEmailTemplate.id,
                organizationId: defaultOrganization.id,
                referenceId: UUID.randomUUID().toString(),
                scheduledStartDate: now.getTime(),
                recalculateOnSend: true
        )

        CommunicationGroupSend groupSend = communicationGroupSendCompositeService.sendAsynchronousGroupCommunication(request)
        assertNotNull(groupSend)

        def checkExpectedGroupSendItemsCreated = {
            CommunicationGroupSend each = CommunicationGroupSend.get( it )
            return CommunicationGroupSendItem.fetchByGroupSend( each ).size() == 3
        }
        assertTrueWithRetry( checkExpectedGroupSendItemsCreated, groupSend.id, 15, 5 )

        // Confirm group send view returns the correct results
        def sendViewDetails = CommunicationGroupSendDetailView.findAll()
        assertEquals(1, sendViewDetails.size())

        def sendListView = CommunicationGroupSendListView.findAll()
        assertEquals(1, sendListView.size())

        // Confirm group send item view returns the correct results
        def sendItemViewDetails = CommunicationGroupSendItemView.findAll()
        assertEquals(3, sendItemViewDetails.size())

        sleepUntilGroupSendItemsComplete( groupSend, 60 )

        int countCompleted = CommunicationGroupSendItem.fetchByCompleteExecutionStateAndGroupSend( groupSend ).size()
        assertEquals( 3, countCompleted )

        sleepUntilCommunicationJobsComplete( 10 * 60 )
        countCompleted = CommunicationJob.fetchCompleted().size()
        assertEquals( 3, countCompleted )

        sleepUntilGroupSendComplete( groupSend, 3 * 60 )

        // test delete group send
        assertEquals( 1, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 3, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 3, CommunicationJob.findAll().size() )
        assertEquals( 3, CommunicationRecipientData.findAll().size() )
        communicationGroupSendCompositeService.deleteGroupSend( groupSend.id )
        assertEquals( 0, fetchGroupSendCount( groupSend.id ) )
        assertEquals( 0, fetchGroupSendItemCount( groupSend.id ) )
        assertEquals( 0, CommunicationJob.findAll().size() )
        assertEquals( 0, CommunicationRecipientData.findAll().size() )
    }
   */
}
