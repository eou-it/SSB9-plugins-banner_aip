/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
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
    void valueOf() {
        assert ActionItemPostWorkExecutionState.valueOf( 0 ) == ActionItemPostWorkExecutionState.Ready
    }


    @Test(expected = IllegalArgumentException.class)
    void valueOfFail() {
        assert ActionItemPostWorkExecutionState.valueOf( 6 ) == ActionItemPostWorkExecutionState.Ready
    }


    void isTerminal() {
        ActionItemPostWorkExecutionState executionState = ActionItemPostWorkExecutionState.Stopped
        assert executionState.terminal() == true
    }


    void isRunning() {
        ActionItemPostWorkExecutionState executionState = ActionItemPostWorkExecutionState.Stopped
        assert executionState.isRunning() == false
    }


    @Test
    void testListBlockedProcessById() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
        List<ActionItemBlockedProcess> actionItemBlockedProcessById = ActionItemBlockedProcess.fetchActionItemBlockProcessById( actionItemBlockedProcessList.id[0] )

        assertEquals( actionItemBlockedProcessList.blockActionItemId[0], actionItemBlockedProcessById.blockActionItemId[0] )
        assertEquals( actionItemBlockedProcessList.blockConfigName[0], actionItemBlockedProcessById.blockConfigName[0] )
        assertEquals( actionItemBlockedProcessList.id[0], actionItemBlockedProcessById.id[0] )
        assertEquals( actionItemBlockedProcessList.blockConfigType[0], actionItemBlockedProcessById.blockConfigType[0] )

    }


    @Test
    void testListBlockedProcessByActionId() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
        List<ActionItemBlockedProcess> actionItemBlockedProcessByActionId = ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId(
                actionItemBlockedProcessList.blockActionItemId[0] )

        assertEquals( actionItemBlockedProcessList.blockActionItemId[0], actionItemBlockedProcessByActionId.blockActionItemId[0] )
        assertEquals( actionItemBlockedProcessList.blockConfigName[0], actionItemBlockedProcessByActionId.blockConfigName[0] )
        assertEquals( actionItemBlockedProcessList.id[0], actionItemBlockedProcessByActionId.id[0] )
        assertEquals( actionItemBlockedProcessList.blockConfigType[0], actionItemBlockedProcessByActionId.blockConfigType[0] )

    }

}
