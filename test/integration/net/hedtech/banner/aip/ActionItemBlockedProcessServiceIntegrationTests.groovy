/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.configuration.ConfigurationData
import net.hedtech.banner.aip.ActionItemBlockedProcess
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import grails.converters.JSON

import javax.swing.Action


class ActionItemBlockedProcessServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemBlockedProcessService


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }


    @Test
    void testListBlockedProcesses() {

        List<ConfigurationData> configData = actionItemBlockedProcessService.listBlockedProcesses(  )

        assertNotNull( configData )

    }

    @Test
    void testListBlockedProcessByName() {

        List<ConfigurationData> configData = actionItemBlockedProcessService.listBlockedProcesses(  )
        List<ConfigurationData> configDataByName = actionItemBlockedProcessService.listBlockedProcessesByName( configData.name[0] )

        assertEquals( configData.name[0], configDataByName.name[0] )
        assertEquals( configData.value[0], configDataByName.value[0] )
        assertEquals( configData.type[0], configDataByName.type[0] )

    }


    @Test
    void testParseBlockedProcessJSON() {

        List<ConfigurationData> configData = actionItemBlockedProcessService.listBlockedProcesses(  )
        def processProps = actionItemBlockedProcessService.listBlockedProcessProps( configData.name[0].toString() )

        assertNotNull( processProps.url )
        assertNotNull( processProps.i18n )

    }


    @Test
    void testListBlockedProcessByActionItemList() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList(  )
        List<ActionItemBlockedProcess> actionItemBlockedProcessServiceList = actionItemBlockedProcessService.listBlockedActionItems()

        assertEquals( actionItemBlockedProcessList.size(), actionItemBlockedProcessServiceList.size() )

    }


    @Test
    void testListBlockedProcessByActionItemId() {

        List<ActionItemBlockedProcess> actionItemBlockedProcess   = actionItemBlockedProcessService.listBlockedActionItems()

        def blockActionItemId = actionItemBlockedProcess[0].blockActionItemId

        List<ActionItemBlockedProcess> actionItemBlockedProcessByActionId = ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId( blockActionItemId )

        List<ActionItemBlockedProcess> actionItemBlockedProcessByActionIdService = actionItemBlockedProcessService.listBlockedProcessByActionItemId( blockActionItemId )

        println actionItemBlockedProcessByActionId

        assertEquals( actionItemBlockedProcessByActionId.blockConfigName, actionItemBlockedProcessByActionIdService.blockConfigName )

    }


    @Test
    void testListBlockedProcessById() {

        List<ActionItemBlockedProcess> actionItemBlockedProcess = actionItemBlockedProcessService.listBlockedActionItems()

        def blockId = actionItemBlockedProcess[0].blockId

        List<ActionItemBlockedProcess> actionItemBlockedProcessById = ActionItemBlockedProcess.fetchActionItemBlockProcessById( blockId )

        List<ActionItemBlockedProcess> actionItemBlockedProcessByIdService = actionItemBlockedProcessService.listBlockedProcessById(
                blockId )
        assertEquals( actionItemBlockedProcessById.blockConfigName, actionItemBlockedProcessByIdService.blockConfigName )

    }

}