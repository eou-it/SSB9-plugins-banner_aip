/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.job

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemErrorCodeIntegrationTests extends BaseIntegrationTestCase {

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
    void testSet() {
        ActionItemErrorCode actionItemErrorCode = ActionItemErrorCode.DATA_FIELD_SQL_ERROR
        actionItemErrorCode.set().size() == 4
    }
}
