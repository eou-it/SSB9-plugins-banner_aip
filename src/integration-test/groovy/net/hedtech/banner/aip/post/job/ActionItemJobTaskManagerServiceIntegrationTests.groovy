/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.job

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.commons.lang.NotImplementedException
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
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
        actionItemJobTaskManagerService.setSimulatedFailureException( null )
        actionItemJobTaskManagerService.process( actionItemJob )

    }


    @Test(expected = RuntimeException.class)
    void testProcessWithException() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        actionItemJobTaskManagerService.setSimulatedFailureException( new Exception( "test" ) )
        actionItemJobTaskManagerService.process( actionItemJob )
    }


    @Test(expected = ApplicationException.class)
    void testProcessWithApplicationException() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        actionItemJobTaskManagerService.setSimulatedFailureException( new ApplicationException( this, new Exception( "test" ) ) )
        actionItemJobTaskManagerService.process( actionItemJob )
    }


    @Test
    void markFailed() {
        ActionItemJob actionItemJob = new ActionItemJob( status: ActionItemJobStatus.PENDING, referenceId: 'ashahs', creationDateTime: new Date() )
        actionItemJob = actionItemJobService.create( actionItemJob )
        actionItemJobTaskManagerService.markFailed( actionItemJob, 'UNKNOWN_ERROR', new Exception( 'test' ) )
        ActionItemJob failedOne = actionItemJobService.get( actionItemJob.id )
        assert failedOne.status == ActionItemJobStatus.FAILED
        assert failedOne.errorCode.name() == 'UNKNOWN_ERROR'
        assertNotNull failedOne.errorText
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
