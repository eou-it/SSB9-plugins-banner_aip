/*********************************************************************************
 Copyright 2017-2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.post.grouppost.ActionItemPostWorkExecutionState
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostWorkExecutionStateIntegrationTests extends BaseIntegrationTestCase {

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
        ActionItemPostWorkExecutionState executionState = ActionItemPostWorkExecutionState.Stopped
        assert executionState.set().size() == 5
    }


    @Test
    void isTerminal() {
        ActionItemPostWorkExecutionState executionState = ActionItemPostWorkExecutionState.Stopped
        assert executionState.isTerminal() == true
    }


    @Test
    void isRunning() {
        ActionItemPostWorkExecutionState executionState = ActionItemPostWorkExecutionState.Stopped
        assert executionState.isRunning() == false
    }
}
