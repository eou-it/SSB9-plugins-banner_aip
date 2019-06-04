/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
@Integration
@Rollback
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
    void testFetchReviewAuditByUserActionItemId() {
        List<ActionItemReviewAudit>  actionItemReviewAudit= actionItemReviewAuditService.fetchReviewAuditByUserActionItemId(-1)
        assertTrue actionItemReviewAudit.isEmpty()
    }


}
