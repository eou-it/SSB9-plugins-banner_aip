/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.commons.lang.NotImplementedException
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostWorkTaskManagerServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostWorkTaskManagerService
    def actionItemPostService
    def actionItemPostWorkService
    def springSecurityService


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
    void getJobType() {
        assert actionItemPostWorkTaskManagerService.getJobType().name == 'net.hedtech.banner.aip.post.grouppost.ActionItemPostWork'
    }


    @Test(expected = NotImplementedException.class)
    void create() {
        actionItemPostWorkTaskManagerService.create()
    }


    @Test
    void init() {
        actionItemPostWorkTaskManagerService.init()
    }


    @Test
    void delete() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork = actionItemPostWorkService.create( actionItemPostWork )
        actionItemPostWorkTaskManagerService.delete( actionItemPostWork )
        assert ActionItemPost.findById( actionItemPostWork.id ) == null
    }


    @Test(expected = NotImplementedException.class)
    void getFailedJobs() {
        actionItemPostWorkTaskManagerService.getFailedJobs()
    }


    @Test
    void getPendingJobs() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostWork.currentExecutionState = ActionItemPostWorkExecutionState.Ready
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork = actionItemPostWorkService.create( actionItemPostWork )
        assert actionItemPostWorkTaskManagerService.getPendingJobs( 5000 ).size() > 0
    }


    @Test
    void acquire() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork = actionItemPostWorkService.create( actionItemPostWork )
        assertTrue actionItemPostWorkTaskManagerService.acquire( actionItemPostWork )
    }


    @Test
    void markComplete() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork = actionItemPostWorkService.create( actionItemPostWork )
        actionItemPostWorkTaskManagerService.markComplete( actionItemPostWork )
        /* ActionItemPostWork completedOne = actionItemPostWorkService.get( actionItemPostWork.id )
         assert completedOne.currentExecutionState == ActionItemPostWorkExecutionState.Complete*/
        // TODO Need to complete this
    }


    @Test
    void process() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork = actionItemPostWorkService.create( actionItemPostWork )
        //actionItemPostWorkTaskManagerService.process( actionItemPostWork )//TODO Need to complete this

    }


    @Test
    void markFailed() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork = actionItemPostWorkService.create( actionItemPostWork )
        actionItemPostWorkTaskManagerService.markFailed( actionItemPostWork, ActionItemErrorCode.UNKNOWN_ERROR.name(  ), new Exception( 'test' ) )
        ActionItemPostWork failedOne = actionItemPostWorkService.get( actionItemPostWork.id )
        assert failedOne.currentExecutionState == ActionItemPostWorkExecutionState.Failed
    }


    @Test
    void updateMonitorRecord() {
        assert actionItemPostWorkTaskManagerService.updateMonitorRecord( null ) == null
    }


    @Test
    void updateMonitorRecord1() {
        actionItemPostWorkTaskManagerService.setSimulatedFailureException( null )
    }


    private def newAIP() {
        new ActionItemPost(
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingDeleteIndicator: false,
                postingScheduleType: "some type",
                postingActionItemGroupId: ActionItemGroup.findByName( 'Enrollment' ).id,
                postingDisplayStartDate: new Date(),
                postingCreationDateTime: new Date(),
                postingDisplayEndDate: new Date(),
                postingCreatorId: 'me',
                postingScheduleDateTime: new Date(),
                populationRegenerateIndicator: false,
                postingCurrentState: ActionItemPostExecutionState.New,
                postingStartedDate: null,
                postingStopDate: null,
                postingJobId: "la43j45h546k56g6f6r77a7kjfn",
                populationCalculationId: 1L,
                postingErrorCode: ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                postingErrorText: null,
                postingGroupId: null,
                postingParameterValues: null
        )
    }


    private def newActionItemPostWork() {
        def user = springSecurityService.getAuthentication()?.user
        new ActionItemPostWork( referenceId: 'somestringfortestinglswefhihvciewranc',
                                currentExecutionState: ActionItemPostWorkExecutionState.Ready,
                                creationDateTime: new Date(), recipientPidm: user.pidm )
    }

}
