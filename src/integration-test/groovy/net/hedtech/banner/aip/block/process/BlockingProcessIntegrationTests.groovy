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
class BlockingProcessIntegrationTests extends BaseIntegrationTestCase {

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
    void fetchNonGlobalBlockingProcess() {
        List<BlockingProcess> blockingProcessList = BlockingProcess.fetchNonGlobalBlockingProcess()
        assert blockingProcessList.find {it.processName == 'Prepare for Registration'}.processName == 'Prepare for Registration'
    }
}
