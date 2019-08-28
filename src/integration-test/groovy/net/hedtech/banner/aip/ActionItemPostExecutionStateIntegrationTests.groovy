/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.aip.post.grouppost.ActionItemPostExecutionState
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemPostExecutionStateIntegrationTests extends BaseIntegrationTestCase {

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
    void set() {
        ActionItemPostExecutionState executionState = ActionItemPostExecutionState.New
        assert 10,executionState.set().size()
    }


    @Test
    void getStateEnum() {
        assert ActionItemPostExecutionState.getStateEnum( 'New' ) == ActionItemPostExecutionState.New
        assert ActionItemPostExecutionState.getStateEnum( 'Scheduled' ) == ActionItemPostExecutionState.Scheduled
        assert ActionItemPostExecutionState.getStateEnum( 'Queued' ) == ActionItemPostExecutionState.Queued
        assert ActionItemPostExecutionState.getStateEnum( 'Calculating' ) == ActionItemPostExecutionState.Calculating
        assert ActionItemPostExecutionState.getStateEnum( 'Processing' ) == ActionItemPostExecutionState.Processing
        assert ActionItemPostExecutionState.getStateEnum( 'Complete' ) == ActionItemPostExecutionState.Complete
        assert ActionItemPostExecutionState.getStateEnum( 'Stopped' ) == ActionItemPostExecutionState.Stopped
        assert ActionItemPostExecutionState.getStateEnum( 'Error' ) == ActionItemPostExecutionState.Error
    }


    @Test(expected = IllegalArgumentException.class)
    void getStateEnumFailedCase() {
        assert ActionItemPostExecutionState.getStateEnum( 'New1' )
    }


    @Test
    void isTerminal() {
        ActionItemPostExecutionState executionState = ActionItemPostExecutionState.New
        assert executionState.isTerminal() == false
    }


    @Test
    void isTerminalForComplete() {
        ActionItemPostExecutionState executionState = ActionItemPostExecutionState.Complete
        assert executionState.isTerminal() == true
    }


    @Test
    void isRunning() {
        ActionItemPostExecutionState executionState = ActionItemPostExecutionState.New
        assert executionState.isRunning() == true
    }


    @Test
    void isRunningForComplete() {
        ActionItemPostExecutionState executionState = ActionItemPostExecutionState.Complete
        assert executionState.isRunning() == false
    }


    @Test
    void isPending() {
        ActionItemPostExecutionState executionState = ActionItemPostExecutionState.Complete
        assert executionState.isPending() == false
    }


    @Test
    void isPendingForNew() {
        ActionItemPostExecutionState executionState = ActionItemPostExecutionState.New
        assert executionState.isPending() == true
    }
}
