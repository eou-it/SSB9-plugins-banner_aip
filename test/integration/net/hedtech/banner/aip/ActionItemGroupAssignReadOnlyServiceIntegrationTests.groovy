/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemGroupAssignReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemGroupAssignReadOnlyService


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
    void testListBlockedProcessById() {
        ActionItemGroupAssignReadOnly domain = ActionItemGroupAssignReadOnly.findByGroupName( 'Enrollment' )
        def activeActionItems = actionItemGroupAssignReadOnlyService.fetchActiveActionItemByGroupId( domain.actionItemGroupId )
        assertEquals( activeActionItems.size() > 0 )

    }

}
