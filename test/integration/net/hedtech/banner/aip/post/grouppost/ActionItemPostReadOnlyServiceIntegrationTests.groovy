/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostReadOnlyService


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
    void testFetchJobs() {
        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = actionItemPostReadOnlyService.listActionItemPostJobList( [searchParam: '%'],
                [max: 1000, offset: 0] ).result
        assert actionItemReadPostOnlyList.size() > 0
    }

}
