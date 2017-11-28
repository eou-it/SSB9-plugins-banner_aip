/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.job.ActionItemJob
import net.hedtech.banner.aip.post.job.ActionItemJobStatus
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculation
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import net.hedtech.banner.general.scheduler.SchedulerErrorContext
import net.hedtech.banner.general.scheduler.SchedulerJobContext
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat

class ActionItemPostCompositeServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostCompositeService
    def actionItemPostService
    def actionItemPostWorkService
    def actionItemProcessingCommonService
    def springSecurityService
    def actionItemPostDetailService
    def actionItemJobService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        loginSSB( 'CSRADM001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void getSysGuId() {
        assert actionItemPostCompositeService.getSysGuId() != 'GHJGHJG'

    }


    @Test
    void createPostItems() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        actionItemPostCompositeService.createPostItems( aip )
        assert actionItemPostWorkService.list( [max: Integer.MAX_VALUE] ).size() > 0
    }


    @Test
    void stopPendingAndDispatchedJobs() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
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
        actionItemPostCompositeService.createPostItems( aip )
        List<ActionItemPostWork> list = ActionItemPostWork.findAllByActionItemGroupSend( aip )
        list.each {
            it.currentExecutionState = ActionItemPostWorkExecutionState.Complete
            actionItemPostWorkService.update( it )
        }
        def refList = []
        list = ActionItemPostWork.findAllByActionItemGroupSend( aip )
        list.each {
            //println 'ActionItemPostWork ' + it
            assert it.currentExecutionState == ActionItemPostWorkExecutionState.Complete
            refList.push( it.referenceId )
            ActionItemJob actionItemJob = new ActionItemJob( referenceId: it.referenceId, status: ActionItemJobStatus.PENDING, creationDateTime: new Date() )
            actionItemJob = actionItemJobService.create( actionItemJob )
            assert actionItemJob.referenceId == it.referenceId
            assert actionItemJob.status == ActionItemJobStatus.PENDING
            assert actionItemJob.id != null
            //println 'actionItemJob ' + actionItemJob
        }

        actionItemPostCompositeService.stopPendingAndDispatchedJobs( aip.id )
        actionItemJobService.list( [max: 10000] ).each {ActionItemJob it ->
            //println 'actionItemJob1 ' + it
            if (refList.contains( it.referenceId )) {
                //assert it.status == ActionItemJobStatus.STOPPED //TODO Need to check and fix
            }
        }
    }


    @Test
    void deleteJobs() {
        def oldCount = actionItemJobService.list( [max: Integer.MAX_VALUE] ).size()
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
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
        actionItemPostCompositeService.createPostItems( aip )
        List<ActionItemPostWork> list = ActionItemPostWork.findAllByActionItemGroupSend( aip )
        list.each {
            it.currentExecutionState = ActionItemPostWorkExecutionState.Complete
            actionItemPostWorkService.update( it )
        }
        def refList = []
        list = ActionItemPostWork.findAllByActionItemGroupSend( aip )
        list.each {
            //println 'ActionItemPostWork ' + it
            assert it.currentExecutionState == ActionItemPostWorkExecutionState.Complete
            refList.push( it.referenceId )
            ActionItemJob actionItemJob = new ActionItemJob( referenceId: it.referenceId, status: ActionItemJobStatus.PENDING, creationDateTime: new Date() )
            actionItemJob = actionItemJobService.create( actionItemJob )
            assert actionItemJob.referenceId == it.referenceId
            assert actionItemJob.status == ActionItemJobStatus.PENDING
            assert actionItemJob.id != null
            println 'actionItemJob ' + actionItemJob
        }
        actionItemPostCompositeService.deleteActionItemJobsByGroupSendId( aip.id )
        def newCountPostDelete = actionItemJobService.list( [max: Integer.MAX_VALUE] ).size()
        assert newCountPostDelete == oldCount
    }


    @Test
    void generatePostItemsImpl() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
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
        actionItemPostCompositeService.generatePostItemsImpl( aip )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Processing
    }


    @Test
    void schedulePost() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
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
        actionItemPostCompositeService.schedulePost( aip, 'CSRAOR001' )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Scheduled
    }


    @Test
    void schedulePostValidateInvalidDate() {
        ActionItemPost aip = newAIP()
        aip.postingScheduleDateTime = new Date() - 2
        aip = actionItemPostService.create( aip )
        try {
            actionItemPostCompositeService.schedulePost( aip, 'CSRAOR001' )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'invalidScheduledDate' )
        }
    }


    @Test
    void schedulePostImmediately() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
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
        actionItemPostCompositeService.schedulePostImmediately( aip, 'CSRAOR001' )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Queued
    }


    @Test
    void generatePostItems() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        actionItemPostCompositeService.generatePostItems( [groupSendId: aip.id] )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Processing
    }


    @Test
    void generatePostItemsPostingNotFound() {
        try {
            actionItemPostCompositeService.generatePostItems( [groupSendId: -99] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'NotFoundException:[id=null, entityClassName=null]' )
        }
    }


    @Test
    void calculatePopulationVersionForGroupSendGroupSendNotFound() {
        try {
            actionItemPostCompositeService.calculatePopulationVersionForGroupSend( [groupSendId: -99] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'NotFoundException:[id=null, entityClassName=null]' )
        }
    }


    @Test
    void scheduledPostCallbackFailedGroupSendNotFound() {
        try {
            SchedulerJobContext jobContext = new SchedulerJobContext( 'test' )
            jobContext.setParameter( 'groupSendId', -99 )
            SchedulerErrorContext context = new SchedulerErrorContext()
            context.jobContext = jobContext
            actionItemPostCompositeService.scheduledPostCallbackFailed( context )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'NotFoundException:[id=null, entityClassName=null]' )
        }
    }


    @Test
    void calculatePopulationVersionForGroupSend() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        actionItemPostCompositeService.calculatePopulationVersionForGroupSend( [groupSendId: aip.id] )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Processing
    }


    @Test
    void scheduledPostCallbackFailed() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        SchedulerJobContext jobContext = new SchedulerJobContext( 'test' )
        jobContext.setParameter( 'groupSendId', aip.id )
        SchedulerErrorContext context = new SchedulerErrorContext()
        context.jobContext = jobContext
        context.cause = null
        actionItemPostCompositeService.scheduledPostCallbackFailed( context )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Error
        assert aip.postingErrorCode == ActionItemErrorCode.UNKNOWN_ERROR
    }


    @Test
    void scheduledPostCallbackFailedWithCause() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        SchedulerJobContext jobContext = new SchedulerJobContext( 'test' )
        jobContext.setParameter( 'groupSendId', aip.id )
        SchedulerErrorContext context = new SchedulerErrorContext()
        context.jobContext = jobContext
        context.cause = new Exception( 'test' )
        actionItemPostCompositeService.scheduledPostCallbackFailed( context )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Error
        assert aip.postingErrorText == new Exception( 'test' ).message
        assert aip.postingErrorCode == ActionItemErrorCode.UNKNOWN_ERROR
    }


    @Test
    void assignPopulationVersion() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        assert actionItemPostCompositeService.assignPopulationVersion( aip ) != null
    }


    @Test
    void completePost() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        assert actionItemPostCompositeService.completePost( aip.id ).postingCurrentState == ActionItemPostExecutionState.Complete
    }


    @Test
    void stopPost() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        assert actionItemPostCompositeService.stopPost( aip.id ).postingCurrentState == ActionItemPostExecutionState.Stopped
    }


    @Test
    void deletePost() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        actionItemPostCompositeService.deletePost( aip.id )
        assert ActionItemPost.get( aip.id ) == null
    }


    private def newAIP() {
        getInstance()
    }


    private getInstance() {
        CommunicationPopulation population = CommunicationPopulation.findAllByPopulationName( 'Quinley Student Population' )[0]
        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy( population.id, 'CSRAOR001' )
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
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
        actionItemPost.populationCalculationId = populationCalculation.id
        actionItemPost
    }


}
