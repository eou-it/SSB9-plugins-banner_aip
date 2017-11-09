/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = ActionItemPostReadOnly.fetchJobs( [searchParam: '%'], [max: 1000, offset: 0] )
        assert actionItemReadPostOnlyList.size() > 0
    }

}
