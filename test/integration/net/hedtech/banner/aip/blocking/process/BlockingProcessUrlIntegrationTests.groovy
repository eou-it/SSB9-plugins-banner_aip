/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.blocking.process

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class BlockingProcessUrlIntegrationTests extends BaseIntegrationTestCase {

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
    void fetchNonGlobalBlockingProcesUrls() {
        def blockingProcess = BlockingProcess.findByProcessNameAndProcessOwnerCode( 'Plan Ahead', 'S' )
        List<BlockingProcessUrls> blockingProcessUrlsList = BlockingProcessUrls.fetchUrlsForSpecificProcess( blockingProcess.id )
        assert blockingProcessUrlsList.find {
            it.processUrl == 'ssb/term/termSelection?mode=plan'
        }.processUrl == 'ssb/term/termSelection?mode=plan'
    }
}
