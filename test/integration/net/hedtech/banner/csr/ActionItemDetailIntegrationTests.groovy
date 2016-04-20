/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemDetailIntegrationTests extends BaseIntegrationTestCase {

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
        List<ActionItem> actionItemsList = ActionItem.fetchActionItems()
        def actionItemId = actionItemsList[0].id
        List<ActionItemDetail> actionItemDetailId = ActionItemDetail.fetchActionItemDetailById( actionItemId )
        assert 1 <= actionItemDetailId.size()
        println actionItemDetailId
    }

}