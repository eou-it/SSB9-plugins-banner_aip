/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
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

        def blockId = actionItemBlockedProcess[0].blockId

        List<ActionItemBlockedProcess> actionItemBlockedProcessById = ActionItemBlockedProcess.fetchActionItemBlockProcessById( blockId )

        List<ActionItemBlockedProcess> actionItemBlockedProcessByIdService = actionItemBlockedProcessService.listBlockedProcessById( blockId )
        assertEquals( actionItemBlockedProcessById.blockConfigName, actionItemBlockedProcessByIdService.blockConfigName )

    }

}
