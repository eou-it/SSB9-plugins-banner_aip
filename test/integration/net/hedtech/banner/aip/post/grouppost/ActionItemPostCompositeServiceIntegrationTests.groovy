/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.job.ActionItemJob
import net.hedtech.banner.aip.post.job.ActionItemJobStatus
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculation
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
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
        requestMap.scheduledStartDate = new Date()
        requestMap.actionItemIds = actionItemIds
        def actionItemPost = actionItemPostCompositeService.getActionPostInstance( requestMap, springSecurityService.getAuthentication()?.user )
        actionItemPost.populationCalculationId = populationCalculation.id
        actionItemPost
    }


}
