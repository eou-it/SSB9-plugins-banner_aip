/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
@Integration
@Rollback

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
        def data = 'jsgfjdekd'
        def paramObj=[searchParam  : "",
                      sortColumn   : "postingName",
                      sortAscending: true,
                      max          : 1000,
                      offset       : 0]

        ActionItemPost aiPost = newActionItemPost( data )
        aiPost.save(flush: true,failOnError: true)
        assert ActionItemPost.findAllByPostingName( data ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( data ).size() > 0
        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = actionItemPostReadOnlyService.listActionItemPostJobList( paramObj ).result
        assertEquals 0, actionItemReadPostOnlyList.size()
    }


    @Test
    void testFetchJobByName() {
        def data = 'kdsfwkw'
        def paramObj=[searchParam  : data,
                      sortColumn   : "postingName",
                      sortAscending: true,
                      max          : 1000,
                      offset       : 0]
        
        ActionItemPost aiPost = newActionItemPost( data )
        aiPost.save(flush: true,failOnError: true)
        assert ActionItemPost.findAllByPostingName( data ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( data ).size() > 0
        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = actionItemPostReadOnlyService.listActionItemPostJobList( paramObj ).result
        assert actionItemReadPostOnlyList.size() > 0
        assertEquals( data, actionItemReadPostOnlyList[0].postingName )
    }


    @Test
    void statusPosted() {
        def data = 'kdsfwkw'
        ActionItemPost aiPost = newActionItemPost( data )
        aiPost = aiPost.save(flush: true,failOnError: true)
        assert ActionItemPost.findAllByPostingName( data ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( data ).size() > 0
        assert AIPConstants.NO_IND == actionItemPostReadOnlyService.statusPosted( aiPost.id )
        assert AIPConstants.NO_IND == actionItemPostReadOnlyService.statusPosted( -99 )
    }


    @Test
    void statusPostedScheduledOne() {
        ActionItemPost aiPost = newScheduledActionItemPost( 'test_scheduled_kdsfwkw' )
        aiPost = aiPost.save(flush: true,failOnError: true )
        assert AIPConstants.YES_IND == actionItemPostReadOnlyService.statusPosted( aiPost.id )
    }


    @Test
    void JobDetailsByPostId() {
        def data = 'kdsfwkw'
        ActionItemPost aiPost = newScheduledActionItemPost( data )
        aiPost = aiPost.save(flush: true,failOnError: true)
        assert ActionItemPost.findAllByPostingName( data ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( data ).size() > 0
        def result = actionItemPostReadOnlyService.JobDetailsByPostId( aiPost.id )
        assert result.postingId == aiPost.id
    }


    private ActionItemPost newActionItemPost(String name ) {
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


    private ActionItemPost newScheduledActionItemPost(String name ) {
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
