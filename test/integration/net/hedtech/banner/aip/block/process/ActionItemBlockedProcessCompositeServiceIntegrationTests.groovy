/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.block.process

import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemBlockedProcessCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemBlockedProcessCompositeService


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
    void getBlockedProcessForSpecifiedActionItem() {
        def result = actionItemBlockedProcessCompositeService.getBlockedProcessForSpecifiedActionItem( ActionItem.findByName( 'Please Review the Attendance Policy' ).id )
        assert result.actionItem.name == 'Please Review the Attendance Policy'
    }


    @Test
    void getBlockedProcessForSpecifiedActionItemNoResult() {
        def result = actionItemBlockedProcessCompositeService.getBlockedProcessForSpecifiedActionItem( -999 )
        assert result.size() == 0
    }

}
