/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.block.process

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
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
        ActionItemBlockedProcess process = actionItemBlockedProcessList.get( 0 )
        List<ActionItemBlockedProcess> actionItemBlockedProcessById = ActionItemBlockedProcess.fetchActionItemBlockProcessById( process.id )
        actionItemBlockedProcessById = actionItemBlockedProcessById.findAll {
            it.blockActionItemId == process.blockActionItemId
        }
        assertEquals( process.blockActionItemId, actionItemBlockedProcessById.blockActionItemId[0] )
        assertEquals( process.blockedProcessId, actionItemBlockedProcessById.blockedProcessId[0] )
        assertEquals( process.id, actionItemBlockedProcessById.id[0] )
        assertEquals( process.blockedProcessId, actionItemBlockedProcessById.blockedProcessId[0] )

    }


    @Test
    void testListBlockedProcessByActionId() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList()
        ActionItemBlockedProcess process = actionItemBlockedProcessList.get( 0 )
        List<ActionItemBlockedProcess> actionItemBlockedProcessByActionId = ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId(
                process.blockActionItemId )
        actionItemBlockedProcessByActionId = actionItemBlockedProcessByActionId.findAll {
            it.blockedProcessId == process.blockedProcessId
        }
        assertEquals( process.blockActionItemId, actionItemBlockedProcessByActionId.blockActionItemId[0] )
        assertEquals( process.blockedProcessId, actionItemBlockedProcessByActionId.blockedProcessId[0] )
        assertEquals( process.id, actionItemBlockedProcessByActionId.id[0] )
        assertEquals( process.blockedProcessId, actionItemBlockedProcessByActionId.blockedProcessId[0] )

    }

}
