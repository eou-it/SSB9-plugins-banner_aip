/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.block.process

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


@Integration
@Rollback
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
            it.processUrl == '/ssb/term/termSelection?mode=plan'
        }.processUrl == '/ssb/term/termSelection?mode=plan'
    }
}
