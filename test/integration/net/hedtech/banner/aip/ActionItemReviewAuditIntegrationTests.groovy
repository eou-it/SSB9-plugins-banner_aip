/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemReviewAuditIntegrationTests extends BaseIntegrationTestCase {

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
    void testfetchReviewAuditByPidmAndActionItemId() {
        List<ActionItemReviewAudit> actionItemReviewAudit = ActionItemReviewAudit.fetchReviewAuditByPidmAndActionItemId(-1,-1)
        assertTrue actionItemReviewAudit.isEmpty()
    }


}
