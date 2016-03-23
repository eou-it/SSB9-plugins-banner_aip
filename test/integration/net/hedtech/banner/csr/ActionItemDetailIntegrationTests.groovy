/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemDetailIntegrationTests extends BaseIntegrationTestCase {

    def actionItemDetailService

    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchActionItemDetailById() {
        def actionItemId = 25

        List<ActionItemDetail> actionItemDetailId = actionItemDetailService.listActionItemDetailById( actionItemId )
        println actionItemDetailId
        assertFalse actionItemDetailId.isEmpty(  )
        assert 1 == actionItemDetailId.size()

    }

}