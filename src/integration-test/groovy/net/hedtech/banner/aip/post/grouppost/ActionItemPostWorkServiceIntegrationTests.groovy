/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
@Integration
@Rollback
class ActionItemPostWorkServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostWorkService
    def springSecurityService
    def actionItemPostService


    @Before
    void setUp() {
        formContext = ['GUAGMNU','SELFSERVICE']
        super.setUp()
        loginSSB( 'AIPADM001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void testCreateWithObject() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        ActionItemPostWork savedOne = actionItemPostWorkService.create( actionItemPostWork )
        assert savedOne.id != null
        assert savedOne.referenceId == 'somestringfortestinglswefhihvciewranc'

    }


    @Test
    void testCreateWithModel() {
        def user = springSecurityService.getAuthentication()?.user
        Map actionItemPostWork = [referenceId          : 'somestringfortestinglswefhihvciewranc',
                                  currentExecutionState: ActionItemPostWorkExecutionState.Ready,
                                  creationDateTime     : new Date(), recipientPidm: user.pidm,
                                  actionItemGroupSend  : null]
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        ActionItemPostWork savedOne = actionItemPostWorkService.create( [domainModel: actionItemPostWork] )
        assert savedOne.id != null
        assert savedOne.referenceId == 'somestringfortestinglswefhihvciewranc'
    }


    @Test
    void testCreateWithModelWithNoDate() {
        ActionItemPostWork actionItemPostWork = newActionItemPostWork()
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork.creationDateTime = null
        ActionItemPostWork savedOne = actionItemPostWorkService.create( actionItemPostWork )
        assert savedOne.id != null
        assert savedOne.referenceId == 'somestringfortestinglswefhihvciewranc'
    }


    @Test
    void testUpdateStateToStop() {
        actionItemPostWorkService.updateStateToStop( ActionItemPost.findById(2) )
    }


    private def newAIP() {
        new ActionItemPost(
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingDeleteIndicator: false,

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
                aSyncJobId: "la43j45h546k56g6f6r77a7kjfn",
                populationCalculationId: 1L,
                postingErrorCode: ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                postingErrorText: null,
                aSyncGroupId: null,
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
