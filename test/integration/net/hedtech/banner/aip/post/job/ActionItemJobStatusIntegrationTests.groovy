/*********************************************************************************
 Copyright 2017-2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.job

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemJobStatusIntegrationTests extends BaseIntegrationTestCase {

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
        ActionItemJobStatus actionItemErrorCode = ActionItemJobStatus.PENDING
        actionItemErrorCode.set().size() == 5
    }
}
