/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.block.process

import grails.util.Holders
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class BlockingProcessCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def blockingProcessCompositeService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        Holders.config.BANNER_AIP_BLOCK_PROCESS_PERSONA = ['EVERYONE', 'STUDENT', 'REGISTRAR', 'FACULTYINSTRUCTOR', 'FACULTYADVISOR', 'FACULTYBOTH']
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void loadBlockingProcessLov() {
        def result = blockingProcessCompositeService.loadBlockingProcessLov()
        assert result.persona.findAll {key, value -> key == 'EVERYONE'}.find {key, value -> key == 'EVERYONE'}.value == 'Every One'
    }
}
