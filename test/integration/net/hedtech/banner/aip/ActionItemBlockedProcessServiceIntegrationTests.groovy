/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

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

/*
    @Test
    void testListBlockedProcessesByType() {

        List<ConfigurationData> configData = actionItemBlockedProcessService.listBlockedProcessesByType()
        assertNotNull( configData )

        println configData

    }

    @Test
    void testListBlockedProcessByNameAndType() {

        List<ConfigurationData> configData = actionItemBlockedProcessService.listBlockedProcessesByType()

        def configDataByName = actionItemBlockedProcessService.listBlockedProcessesByNameAndType( configData.name[0].toString() )

        assertTrue( configData.value[0].contains( configDataByName.url ) )

    }
    */

/*
    @Test
    void testParseBlockedProcessJSON() {

        List<ConfigurationData> configData = actionItemBlockedProcessService.listBlockedProcessesByType(  )
        def processProps = actionItemBlockedProcessService.listBlockedProcessesByNameAndType( configData.name[0].toString() )

        assertNotNull( processProps.url )
        assertNotNull( processProps.processNamei18n )

    }


    @Test
    void testListBlockedProcessByActionItemList() {

        List<ActionItemBlockedProcess> actionItemBlockedProcessList = ActionItemBlockedProcess.fetchActionItemBlockedProcessList( )
        List<ActionItemBlockedProcess> actionItemBlockedProcessServiceList = actionItemBlockedProcessService.listBlockedActionItems()

        assertEquals( actionItemBlockedProcessList.size(), actionItemBlockedProcessServiceList.size() )

    }


    @Test
    void testListBlockedProcessByActionItemId() {

        List<ActionItemBlockedProcess> actionItemBlockedProcess   = actionItemBlockedProcessService.listBlockedActionItems()

        def blockActionItemId = actionItemBlockedProcess[0].blockActionItemId

        List<ActionItemBlockedProcess> actionItemBlockedProcessByActionId = ActionItemBlockedProcess.fetchActionItemBlockProcessByActionId( blockActionItemId )

        List<ActionItemBlockedProcess> actionItemBlockedProcessByActionIdService = actionItemBlockedProcessService.listBlockedProcessByActionItemId( blockActionItemId )

        assertEquals( actionItemBlockedProcessByActionId.blockConfigName, actionItemBlockedProcessByActionIdService.blockConfigName )

    }
    */


    @Test
    void testListBlockedProcessById() {

        List<ActionItemBlockedProcess> actionItemBlockedProcess = actionItemBlockedProcessService.listBlockedActionItems()

        def blockId = actionItemBlockedProcess[0].blockId

        List<ActionItemBlockedProcess> actionItemBlockedProcessById = ActionItemBlockedProcess.fetchActionItemBlockProcessById( blockId )

        List<ActionItemBlockedProcess> actionItemBlockedProcessByIdService = actionItemBlockedProcessService.listBlockedProcessById( blockId )
        assertEquals( actionItemBlockedProcessById.blockConfigName, actionItemBlockedProcessByIdService.blockConfigName )

    }

}