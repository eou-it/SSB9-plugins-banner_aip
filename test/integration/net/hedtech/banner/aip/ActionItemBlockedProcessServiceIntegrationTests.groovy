/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.blocking.process.ActionItemBlockedProcess
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

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
        assertEquals( actionItemBlockedProcessById.blockConfigName, actionItemBlockedProcessByIdService.blockConfigName )

    }


    @Test
    void listBlockedProcessByActionItemId() {
        ActionItem.findByName( 'Notice of Scholastic Standards' ).id
        List<ActionItemBlockedProcess> actionItemBlockedProcess = actionItemBlockedProcessService.listBlockedProcessByActionItemId( ActionItem.findByName( 'Notice of Scholastic Standards' ).id )
        assert actionItemBlockedProcess.find() {
            it.blockConfigName == 'planAhead'
        }.blockConfigName == 'planAhead'
    }


    @Test
    void listBlockedProcessesByNameAndType() {
        def processMap = actionItemBlockedProcessService.listBlockedProcessesByNameAndType( null )
        assert processMap.url== '["/ssb/term/termSelection?mode=registration"]'
        assert processMap.processNamei18n == 'aip.blocked.process.name.register.for.classes'
    }


    @Test
    void listBlockedProcessesByType() {
        def processMap = actionItemBlockedProcessService.listBlockedProcessesByType()[0]
        assert processMap.id == 1
        assert processMap.name == 'registerForClasses'
        assert processMap.type == 'json/aipBlock'
        assert processMap.value == '{"aipBlock": {"processNamei18n":"aip.blocked.process.name.register.for.classes","urls":["/ssb/term/termSelection?mode=registration" ] }}'
    }
}
