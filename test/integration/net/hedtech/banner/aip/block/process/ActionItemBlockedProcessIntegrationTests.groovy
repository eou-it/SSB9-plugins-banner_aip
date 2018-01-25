/*********************************************************************************
 Copyright 2017-2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.block.process

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemBlockedProcessIntegrationTests extends BaseIntegrationTestCase {

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
    void testListBlockedProcesses() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList()

        assertNotNull( actionItemBlockedProcessList )

    }


    @Test
    void testListBlockedProcessById() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
        List<ActionItemBlockedProcess> actionItemBlockedProcessById = ActionItemBlockedProcess.fetchActionItemBlockProcessById( actionItemBlockedProcessList.id[0] )

        assertEquals( actionItemBlockedProcessList.blockActionItemId[0], actionItemBlockedProcessById.blockActionItemId[0] )
        assertEquals( actionItemBlockedProcessList.blockedProcessId[0], actionItemBlockedProcessById.blockedProcessId[0] )
        assertEquals( actionItemBlockedProcessList.id[0], actionItemBlockedProcessById.id[0] )
        assertEquals( actionItemBlockedProcessList.blockedProcessId[0], actionItemBlockedProcessById.blockedProcessId[0] )

    }


    @Test
    void testListBlockedProcessByActionId() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
        List<ActionItemBlockedProcess> actionItemBlockedProcessByActionId = ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId(
                actionItemBlockedProcessList.blockActionItemId[0] )

        assertEquals( actionItemBlockedProcessList.blockActionItemId[0], actionItemBlockedProcessByActionId.blockActionItemId[0] )
        assertEquals( actionItemBlockedProcessList.blockedProcessId[0], actionItemBlockedProcessByActionId.blockedProcessId[0] )
        assertEquals( actionItemBlockedProcessList.id[0], actionItemBlockedProcessByActionId.id[0] )
        assertEquals( actionItemBlockedProcessList.blockedProcessId[0], actionItemBlockedProcessByActionId.blockedProcessId[0] )

    }

}
