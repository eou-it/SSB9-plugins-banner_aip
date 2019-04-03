/*******************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.job.ActionItemJob
import net.hedtech.banner.aip.post.job.ActionItemJobStatus
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.asynchronous.AsynchronousBannerAuthenticationSpoofer
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersion
import net.hedtech.banner.general.scheduler.SchedulerErrorContext
import net.hedtech.banner.general.scheduler.SchedulerJobContext
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.json.JSONObject
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
            assert it.currentExecutionState == ActionItemPostWorkExecutionState.Complete
            refList.push( it.referenceId )
            ActionItemJob actionItemJob = new ActionItemJob( referenceId: it.referenceId, status: ActionItemJobStatus.PENDING, creationDateTime: new Date() )
            actionItemJob = actionItemJobService.create( actionItemJob )
            assert actionItemJob.referenceId == it.referenceId
            assert actionItemJob.status == ActionItemJobStatus.PENDING
            assert actionItemJob.id != null
        }

        actionItemPostCompositeService.stopPendingAndDispatchedJobs( aip.id )
        actionItemJobService.list( [max: 10000] ).each {ActionItemJob it ->
            if (refList.contains( it.referenceId )) {
               assert it.status == ActionItemJobStatus.STOPPED
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
            assert it.currentExecutionState == ActionItemPostWorkExecutionState.Complete
            refList.push( it.referenceId )
            ActionItemJob actionItemJob = new ActionItemJob( referenceId: it.referenceId, status: ActionItemJobStatus.PENDING, creationDateTime: new Date() )
            actionItemJob = actionItemJobService.create( actionItemJob )
            assert actionItemJob.referenceId == it.referenceId
            assert actionItemJob.status == ActionItemJobStatus.PENDING
            assert actionItemJob.id != null
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
        aip.postingScheduleDateTime = new Date() + 2
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
        actionItemPostCompositeService.schedulePost( aip, USERNAME )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Scheduled
    }


    @Test
    void schedulePostValidateInvalidDate() {
        ActionItemPost aip = newAIP()
        aip.postingScheduleDateTime = new Date() - 2
        aip = actionItemPostService.create( aip )
        try {
            actionItemPostCompositeService.schedulePost( aip, USERNAME )
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
        actionItemPostCompositeService.schedulePostImmediately( aip, USERNAME )
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
    void calculatePopulationVersionForPostFailed() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        SchedulerJobContext jobContext = new SchedulerJobContext( 'test' )
        jobContext.setParameter( 'groupSendId', aip.id )
        SchedulerErrorContext context = new SchedulerErrorContext()
        context.jobContext = jobContext
        context.cause = null
        actionItemPostCompositeService.calculatePopulationVersionForPostFailed( context )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Error
        assert aip.postingErrorCode == ActionItemErrorCode.UNKNOWN_ERROR
    }


    @Test
    void generatePostItemsFired() {
        ActionItemPost aip = newAIP()
        actionItemPostCompositeService.setAsynchronousBannerAuthenticationSpoofer( new AsynchronousBannerAuthenticationSpoofer() )
        aip = actionItemPostService.create( aip )
        SchedulerJobContext jobContext = new SchedulerJobContext( 'test' )
        jobContext.setParameter( 'groupSendId', aip.id )
        actionItemPostCompositeService.generatePostItemsFired( jobContext )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Processing
    }


    @Test
    void generatePostItemsFailed() {
        ActionItemPost aip = newAIP()
        actionItemPostCompositeService.setAsynchronousBannerAuthenticationSpoofer( new AsynchronousBannerAuthenticationSpoofer() )
        aip = actionItemPostService.create( aip )
        SchedulerJobContext jobContext = new SchedulerJobContext( 'test' )
        jobContext.setParameter( 'groupSendId', aip.id )
        SchedulerErrorContext context = new SchedulerErrorContext()
        context.jobContext = jobContext
        context.cause = new Exception( 'test' )
        actionItemPostCompositeService.generatePostItemsFailed( context )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Error
        assert aip.postingErrorText == new Exception( 'test' ).message
        assert aip.postingErrorCode == ActionItemErrorCode.UNKNOWN_ERROR
    }


    @Test
    void calculatePopulationVersionForPostFired() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        SchedulerJobContext jobContext = new SchedulerJobContext( 'test' )
        jobContext.setParameter( 'groupSendId', aip.id )
        SchedulerErrorContext context = new SchedulerErrorContext()
        context.jobContext = jobContext
        actionItemPostCompositeService.calculatePopulationVersionForPostFired( jobContext )
        assert aip.postingCurrentState == ActionItemPostExecutionState.Processing
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
    void stopPostTerminalState() {
        ActionItemPost aip = newAIP()
        aip.postingCurrentState = ActionItemPostExecutionState.Error
        aip = actionItemPostService.create( aip )
        try {
            actionItemPostCompositeService.stopPost( aip.id )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'cannotStopConcludedPost' )
        }
    }


    @Test
    void deletePost() {
        ActionItemPost aip = newAIP()
        aip = actionItemPostService.create( aip )
        actionItemPostCompositeService.deletePost( aip.id )
        assert ActionItemPost.get( aip.id ) == null
    }


    @Test
    void deletePostNoPendingExecutionState() {
        try {
            ActionItemPost aip = newAIP()
            aip.postingCurrentState = ActionItemPostExecutionState.Calculating
            aip = actionItemPostService.create( aip )
            actionItemPostCompositeService.deletePost( aip.id )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'cannotDeleteRunningPost' )
        }

    }


    @Test
    void deletePostInvaidGroupSend() {
        try {
            actionItemPostCompositeService.deletePost( -99 )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'NotFoundException:[id=-99, entityClassName=ActionItemPost]' )
        }
    }


    @Test
    void sendAsynchronousPostItem() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal","06/21/2018");
        correspondingServerDetails.put("timeVal", "0330");
        correspondingServerDetails.put("timeZoneVal", "(GMT+5:30) Asia/Kolkata");
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
        requestMap.displayDatetimeZone=correspondingServerDetails
        def result = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap )
        assert result.success == true
        assert result.savedJob != null
        ActionItemPost post = result.savedJob
        assert post.id != null
        assert post.postingErrorText == null
        assert post.lastModifiedBy == USERNAME

    }


    @Test
    void sendAsynchronousPostItemInvalidScheduledDate() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal","06/21/2018");
        correspondingServerDetails.put("timeVal", "0330");
        correspondingServerDetails.put("timeZoneVal", "(GMT+5:30) Asia/Kolkata");
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postingActionItemGroupId = actionItemGroup.id
        requestMap.scheduled = true
        requestMap.recalculateOnPost = false
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = new Date() - 1
        requestMap.scheduledStartTime = "0900"
        requestMap.timezoneStringOffset = "Asia/Kolkata"
        requestMap.displayDatetimeZone=correspondingServerDetails;
        requestMap.actionItemIds = actionItemIds
        print "REM,$requestMap"
        try {
            actionItemPostCompositeService.sendAsynchronousPostItem( requestMap )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.display.obsolete.schedule.date' )
        }

    }


    @Test
    void sendAsynchronousPostItemInvalidDisplayStartDate() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal","06/21/2018");
        correspondingServerDetails.put("timeVal", "0330");
        correspondingServerDetails.put("timeZoneVal", "(GMT+5:30) Asia/Kolkata");
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postingActionItemGroupId = actionItemGroup.id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.displayDatetimeZone=correspondingServerDetails;
        requestMap.displayStartDate = testingDateFormat.format( new Date() - 1 )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = new Date()
        requestMap.actionItemIds = actionItemIds
        try {
            actionItemPostCompositeService.sendAsynchronousPostItem( requestMap )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.obsolete.display.start.date' )
        }

    }


    @Test
    void markActionItemPosted() {
        ActionItem actionItem = ActionItem.findByName( 'Drug and Alcohol Policy' )
        actionItem.postedIndicator = 'N'
        actionItem = actionItem.save( flush: true )
        assert actionItem.postedIndicator == 'N'
        actionItemPostCompositeService.markActionItemPosted( actionItem.id )
        actionItem = ActionItem.findByName( 'Drug and Alcohol Policy' )
        assert actionItem.postedIndicator == 'Y'
    }


    @Test
    void sendAsynchronousPostItemInvalidDisplayEndDate() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal","06/21/2018");
        correspondingServerDetails.put("timeVal", "0330");
        correspondingServerDetails.put("timeZoneVal", "(GMT+5:30) Asia/Kolkata");
        def requestMap = [:]
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.populationId = populationListView.id
        requestMap.referenceId = UUID.randomUUID().toString()
        requestMap.postingActionItemGroupId = actionItemGroup.id
        requestMap.postNow = true
        requestMap.recalculateOnPost = false
        requestMap.displayStartDate = testingDateFormat.format( new Date() )
        requestMap.displayEndDate = testingDateFormat.format( new Date() - 1 )
        requestMap.scheduledStartDate = new Date() - 1
        requestMap.displayDatetimeZone=correspondingServerDetails;
        requestMap.actionItemIds = actionItemIds
        try {
            actionItemPostCompositeService.sendAsynchronousPostItem( requestMap )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.display.start.date.more.than.display.end.date' )
        }

    }


    @Test
    void updateAsynchronousPostItem() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal","06/21/2018");
        correspondingServerDetails.put("timeVal", "0330");
        correspondingServerDetails.put("timeZoneVal", "(GMT+5:30) Asia/Kolkata");
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
        requestMap.displayDatetimeZone=correspondingServerDetails;
        requestMap.actionItemIds = actionItemIds
        def result = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap )
        assert result.success == true
        assert result.savedJob != null
        ActionItemPost post = result.savedJob
        assert post.id != null
        assert post.postingErrorText == null
        assert post.lastModifiedBy == USERNAME
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes1'
        def result1 = actionItemPostCompositeService.sendAsynchronousPostItem( requestMap )
        assert result1.success == true
        assert result1.savedJob != null
        ActionItemPost actionItemPost = (ActionItemPost) actionItemPostService.get( post.id )
        assert actionItemPost != null
        requestMap.postingName = 'testPostByPopulationSendInTwoMinutes'
        requestMap.postId = post.id
        requestMap.displayStartDate = testingDateFormat.format( new Date() + 2 )
        requestMap.displayEndDate = testingDateFormat.format( new Date() + 50 )
        requestMap.scheduledStartDate = new Date() + 2
        def updateResult = actionItemPostCompositeService.updateAsynchronousPostItem( requestMap )
        assert updateResult.success == true
        assert updateResult.savedJob != null
        ActionItemPost updatedPost = updateResult.savedJob
        assert updatedPost.id == post.id
        assert updatedPost.postingErrorText == null
        assert updatedPost.lastModifiedBy == USERNAME

    }


    private def newAIP() {
        getInstance()
    }


    private getInstance() {
        CommunicationPopulation population = CommunicationPopulation.findAllByPopulationName( 'AIP Student Population 1' )[0]
        CommunicationPopulationVersion populationVersion = CommunicationPopulationVersion.findLatestByPopulationId( population.id )
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        CommunicationPopulationListView populationListView = actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] )[0]
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        List<Long> actionItemIds = ActionItemGroupAssign.fetchByGroupId( actionItemGroup.id ).collect {it.actionItemId}
        def correspondingServerDetails =new JSONObject()
        correspondingServerDetails.put("dateVal","06/21/2018");
        correspondingServerDetails.put("timeVal", "0330");
        correspondingServerDetails.put("timeZoneVal", "(GMT+5:30) Asia/Kolkata");
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
        requestMap.displayDatetimeZone=correspondingServerDetails;
        def actionItemPost = actionItemPostCompositeService.getActionPostInstance( requestMap, springSecurityService.getAuthentication()?.user )
        actionItemPost.populationCalculationId = populationVersion.id
        actionItemPost.populationVersionId = populationVersion.id
        actionItemPost
    }

}
