/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.job

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
@Integration
@Rollback
class ActionItemJobServiceIntegrationTests extends BaseIntegrationTestCase {
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
    void testFetchJobsNoParam() {
        ActionItemJob actionItemJob = newActionItemJob()
        actionItemJob.save()
        List<ActionItemJob> actionItemJobList = actionItemJobService.fetchPending()
        assert actionItemJobList.size() > 0
    }


    private ActionItemJob newActionItemJob() {
        new ActionItemJob( referenceId: 'TestRefId', status: ActionItemJobStatus.PENDING, creationDateTime: new Date() )
    }


    @Test
    void acquireJobPresent() {
        ActionItemJob actionItemJob = newActionItemJob()
        ActionItemJob actionItemJob1 = actionItemJob.save()
        assert actionItemJobService.acquire( actionItemJob1.id ) == true
    }


    @Test
    void acquireJobNotPresent() {
        assert actionItemJobService.acquire( -1L ) == false
    }


    @Test
    void markCompletedIfJobPresent() {
        ActionItemJob actionItemJob = newActionItemJob()
        ActionItemJob actionItemJob1 = actionItemJob.save()
        actionItemJobService.markCompleted( actionItemJob1.id )
    }


    @Test
    void markCompletedIfJobNotPresent() {
        actionItemJobService.markCompleted( -1L )
    }
}
