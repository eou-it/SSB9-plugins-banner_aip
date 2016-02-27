/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.csr
import org.junit.Before
import org.junit.Test
import org.junit.After

import net.hedtech.banner.testing.BaseIntegrationTestCase


class UserActionItemIntegrationTests extends BaseIntegrationTestCase {

    def actionItemService

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
    void testFetchActionItemByIdService() {
        def actionItemId = 114

        String actionItemStatus = actionItemService.listActionItemById(actionItemId)
        assertEquals('Pending', actionItemStatus)
    }

    @Test
    void testFetchActionItemByPidmService() {
        def actionItemPidm = 124018

        String actionItemStatus = actionItemService.listActionItemByPidm(actionItemPidm)
        assertEquals('Completed', actionItemStatus)
    }
}