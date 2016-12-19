/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.configuration.ConfigurationData
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemBlockedProcessReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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

       // def blockedProcessJSON = configDataByName.value[0]

        //assertEquals( configData.value[0], blockedProcessJSON )

        println processProps

    }

}