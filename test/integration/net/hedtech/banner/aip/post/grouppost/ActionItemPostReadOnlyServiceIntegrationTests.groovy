/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostReadOnlyService


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
        def ourName = 'jsgfjdekd'

        ActionItemPost myAip = newActionItemPost( ourName )
        myAip.save()

        assert ActionItemPost.findAllByPostingName( ourName ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( ourName ).size() > 0

        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = actionItemPostReadOnlyService.listActionItemPostJobList( [searchParam: ''],
                                                                                                                           [max: 1000, offset: 0] ).result

        assert actionItemReadPostOnlyList.size() > 0
    }


    @Test
    void testFetchJobByName() {
        def ourName = 'kdsfwkw'

        ActionItemPost myAip = newActionItemPost( ourName )
        myAip.save()

        assert ActionItemPost.findAllByPostingName( ourName ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( ourName ).size() > 0
        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = actionItemPostReadOnlyService.listActionItemPostJobList( [searchParam: ourName],
                                                                                                                           [max: 1000, offset: 0] ).result

        assert actionItemReadPostOnlyList.size() > 0
        assertEquals( ourName, actionItemReadPostOnlyList[0].postingName )
    }


    @Test
    void statusPosted() {
        def ourName = 'kdsfwkw'
        ActionItemPost myAip = newActionItemPost( ourName )
        myAip = myAip.save()
        assert ActionItemPost.findAllByPostingName( ourName ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( ourName ).size() > 0
        assert AIPConstants.NO_IND == actionItemPostReadOnlyService.statusPosted( myAip.id )
        assert AIPConstants.NO_IND == actionItemPostReadOnlyService.statusPosted( -99 )
    }


    @Test
    void statusPostedScheduledOne() {
        ActionItemPost myAip = newScheduledActionItemPost( 'test_scheduled_kdsfwkw' )
        myAip = myAip.save( flush: true )
        assert AIPConstants.YES_IND == actionItemPostReadOnlyService.statusPosted( myAip.id )
    }


    @Test
    void JobDetailsByPostId() {
        def ourName = 'kdsfwkw'
        ActionItemPost myAip = newScheduledActionItemPost( ourName )
        myAip = myAip.save()
        assert ActionItemPost.findAllByPostingName( ourName ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( ourName ).size() > 0
        def result = actionItemPostReadOnlyService.JobDetailsByPostId( myAip.id )
        assert result.postingId == myAip.id
    }


    private ActionItemPost newActionItemPost( String name ) {
        ActionItemPost aip = new ActionItemPost()
        aip.populationVersionId = 32548072L
        aip.populationListId = 43987234L
        aip.postingName = name
        aip.postingActionItemGroupId = ActionItemGroup.fetchActionItemGroups()[0]?.id
        aip.postingDeleteIndicator = 'N'
        aip.postingDisplayStartDate = new Date()
        aip.postingDisplayEndDate = new Date() + 1
        aip.postingCreatorId = 'rgre'
        aip.postingCreationDateTime = new Date()
        aip.populationRegenerateIndicator = 'N'
        aip.lastModified = new Date()
        aip.lastModifiedBy = 'edffeds'
        aip
    }


    private ActionItemPost newScheduledActionItemPost( String name ) {
        ActionItemPost aip = new ActionItemPost()
        aip.populationVersionId = 32548072L
        aip.populationListId = 43987234L
        aip.postingName = name
        aip.postingActionItemGroupId = ActionItemGroup.fetchActionItemGroups()[0]?.id
        aip.postingDeleteIndicator = 'N'
        aip.postingDisplayStartDate = new Date()
        aip.postingDisplayEndDate = new Date() + 1
        aip.postingScheduleDateTime = new Date() + 3
        aip.postingCreatorId = 'rgre'
        aip.postingCreationDateTime = new Date()
        aip.populationRegenerateIndicator = 'N'
        aip.postingCurrentState = ActionItemPostExecutionState.Scheduled
        aip.lastModified = new Date()
        aip.lastModifiedBy = 'edffeds'
        aip
    }
}
