/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.general.ConfigurationData
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


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

        println configData

    }

    @Test
    void testListBlockedProcessByName() {

        List<ConfigurationData> configData = actionItemBlockedProcessService.listBlockedProcesses(  )

        List<ConfigurationData> configDataByName = actionItemBlockedProcessService.listBlockedProcessesByName( configData.name )

        println configDataByName

    }

}