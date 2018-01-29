/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.job

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.commons.lang.NotImplementedException
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemJobTaskManagerServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemJobTaskManagerService
    def actionItemJobService


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
    void getJobType() {
        assert actionItemJobTaskManagerService.getJobType().name == 'net.hedtech.banner.aip.post.job.ActionItemJob'
    }


    @Test(expected = NotImplementedException.class)
    void create() {
        actionItemJobTaskManagerService.create()
    }


    @Test
    void init() {
        actionItemJobTaskManagerService.init()
    }


    @Test
    void delete() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        actionItemJobTaskManagerService.delete( actionItemJob )
        assert ActionItemJob.findById( actionItemJob.id ) == null
    }


    @Test(expected = NotImplementedException.class)
    void getFailedJobs() {
        actionItemJobTaskManagerService.getFailedJobs()
    }


    @Test
    void getPendingJobs() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        assert actionItemJobTaskManagerService.getPendingJobs( 5000 ).size() > 0
    }


    @Test
    void acquire() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        assert actionItemJobTaskManagerService.acquire( actionItemJob ) == true
    }


    @Test
    void markComplete() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        actionItemJobTaskManagerService.markComplete( actionItemJob )
        ActionItemJob completedOne = actionItemJobService.get( actionItemJob.id )
        assert completedOne.status == ActionItemJobStatus.COMPLETED
    }


    @Test
    void process() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        actionItemJobTaskManagerService.process( actionItemJob )

    }


    @Test
    void markFailed() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        actionItemJobTaskManagerService.markFailed( actionItemJob, 'UNKNOWN_ERROR', new Exception( 'test' ) )
        ActionItemJob failedOne = actionItemJobService.get( actionItemJob.id )
        assert failedOne.status == ActionItemJobStatus.FAILED
    }


    @Test
    void updateMonitorRecord() {
        assert actionItemJobTaskManagerService.updateMonitorRecord( null ) == null
    }


    @Test
    void updateMonitorRecord1() {
        actionItemJobTaskManagerService.setSimulatedFailureException( null )
    }
}
