/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.block.process


import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemBlockedProcessServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemBlockedProcessService


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
    void testListBlockedProcessById() {

        List<ActionItemBlockedProcess> actionItemBlockedProcess = actionItemBlockedProcessService.listBlockedActionItems()

        def blockId = actionItemBlockedProcess[0].id

        List<ActionItemBlockedProcess> actionItemBlockedProcessById = ActionItemBlockedProcess.fetchActionItemBlockProcessById( blockId )

        List<ActionItemBlockedProcess> actionItemBlockedProcessByIdService = actionItemBlockedProcessService.listBlockedProcessById( blockId )
        assertEquals( actionItemBlockedProcessById.blockedProcessId, actionItemBlockedProcessByIdService.blockedProcessId )

    }


    @Test
    void listBlockedProcessByActionItemId() {
        ActionItem.findByName( 'Notice of Scholastic Standards' ).id
        List<ActionItemBlockedProcess> actionItemBlockedProcess = actionItemBlockedProcessService.listBlockedProcessByActionItemId( ActionItem.findByName( 'Notice of Scholastic Standards' ).id )
        assert actionItemBlockedProcess.find() {
            it.blockedProcessId == BlockingProcess.findByProcessName('Prepare for Registration').id
        }.blockedProcessId == BlockingProcess.findByProcessName('Prepare for Registration').id
    }

}
