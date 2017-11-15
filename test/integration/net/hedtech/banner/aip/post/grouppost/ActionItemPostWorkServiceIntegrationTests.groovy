/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostWorkServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostWorkService
    def springSecurityService
    def actionItemPostService


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
        def user = springSecurityService.getAuthentication()?.user
        Map actionItemPostWork = [referenceId          : 'somestringfortestinglswefhihvciewranc',
                                  currentExecutionState: ActionItemPostWorkExecutionState.Ready,
                                  creationDateTime     : null, recipientPidm: user.pidm,
                                  actionItemGroupSend  : null]
        ActionItemPost aip = newAIP()
        actionItemPostService.create( aip )
        actionItemPostWork.actionItemGroupSend = aip
        ActionItemPostWork savedOne = actionItemPostWorkService.create( [domainModel: actionItemPostWork] )
        assert savedOne.id != null
        assert savedOne.referenceId == 'somestringfortestinglswefhihvciewranc'
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
