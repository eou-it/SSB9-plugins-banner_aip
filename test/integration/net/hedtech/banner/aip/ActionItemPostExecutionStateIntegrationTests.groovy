/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.post.grouppost.ActionItemPostExecutionState
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

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
        assert executionState.set().size() == 8
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
