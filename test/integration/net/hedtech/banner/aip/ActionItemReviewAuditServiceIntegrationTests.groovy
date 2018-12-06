/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.block.process.ActionItemBlockedProcess
import net.hedtech.banner.aip.block.process.BlockingProcess
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemReviewAuditServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemReviewAuditService



    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        loginSSB('AIDADM001', '111111')
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void fetchReviewAuditByPidmAndActionItemId() {
        List<ActionItemReviewAudit>  actionItemReviewAudit= actionItemReviewAuditService.fetchReviewAuditByPidmAndActionItemId(-1,-1)
        assertTrue actionItemReviewAudit.isEmpty()
    }


}
