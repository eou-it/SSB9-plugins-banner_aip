/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.block.process

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemBlockedProcessCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemBlockedProcessCompositeService


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
    void getBlockedProcessForSpecifiedActionItem() {
        def result = actionItemBlockedProcessCompositeService.getBlockedProcessForSpecifiedActionItem( ActionItem.findByName( 'Please Review the Attendance Policy' ).id )
        assert result.actionItem.name == 'Please Review the Attendance Policy'
    }


    @Test
    void getBlockedProcessForSpecifiedActionItemNoResult() {
        def result = actionItemBlockedProcessCompositeService.getBlockedProcessForSpecifiedActionItem( -999 )
        assert result.size() == 0
    }


    @Test
    void updateBlockedProcessItemsFailedCase() {
        Map paramMap = [actionItemId: null]
        def result = actionItemBlockedProcessCompositeService.updateBlockedProcessItems( paramMap )
        assert !result.success
        assert result.message == 'Action Item Id not present'
    }


    @Test
    void checkDuplicateProcessPersonaEntries() {
        Map paramMap = [actionItemId: 22, blockedProcesses: [[persona: 'X', processId: 3], [persona: 'X', processId: 3]], globalBlockProcess: true]
        def result = actionItemBlockedProcessCompositeService.updateBlockedProcessItems( paramMap )
    }


    @Test
    void updateBlockedProcessItems() {
        ActionItem actionItem = ActionItem.findByName( 'The Declaration of Independence' )
        BlockingProcess blockingProcess = BlockingProcess.findByProcessName( 'Prepare for Registration' )
        Map paramMap = [actionItemId: actionItem.id.toString(), globalBlockProcess: false, blockedProcesses: [[processId: blockingProcess.id, persona: 'STUDENT']]]
        def result = actionItemBlockedProcessCompositeService.updateBlockedProcessItems( paramMap )
        assert result.success
        paramMap = [actionItemId: actionItem.id.toString(), globalBlockProcess: false, blockedProcesses: [[processId: BlockingProcess.findByProcessName( 'Prepare for Registration' ).id, persona: null]]]
        result = actionItemBlockedProcessCompositeService.updateBlockedProcessItems( paramMap )
        assert result.success
        paramMap = [actionItemId: actionItem.id.toString(), globalBlockProcess: true, blockedProcesses: [[processId: BlockingProcess.findByProcessName( 'Prepare for Registration' ).id, persona: 'STUDENT']]]
        result = actionItemBlockedProcessCompositeService.updateBlockedProcessItems( paramMap )
        assert result.success
        paramMap = [actionItemId: actionItem.id.toString(), globalBlockProcess: true, blockedProcesses: []]
        result = actionItemBlockedProcessCompositeService.updateBlockedProcessItems( paramMap )
        assert result.success
    }
}
