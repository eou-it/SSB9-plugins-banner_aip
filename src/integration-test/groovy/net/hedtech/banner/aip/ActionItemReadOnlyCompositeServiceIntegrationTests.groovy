/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemReadOnlyCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemReadOnlyCompositeService


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
    void testFetchActionItemROPageSortService() {
        Map params1 = [filterName: "%", sortColumn: "actionItemName", sortAscending: true, max: 10, offset: 0]

        Map params2 = [filterName: "%", sortColumn: "actionItemName", sortAscending: true, max: 10, offset: 10]


        def actionItemROList1 = actionItemReadOnlyCompositeService.listActionItemsPageSort( params1 )
        def actionItemROList2 = actionItemReadOnlyCompositeService.listActionItemsPageSort( params2 )
        Map params3 = [filterName: "%", sortColumn: "actionItemName", sortAscending: true, max: actionItemROList1.length, offset: 20]
        def actionItemROList3 = actionItemReadOnlyCompositeService.listActionItemsPageSort( params3 )

        def totalCount = actionItemROList1.result.size() + actionItemROList2.result.size() + actionItemROList3.result.size()

        def actualLength = actionItemROList1.length

        assertEquals( actualLength, totalCount )
    }


    @Test
    void openActionItem() {
        def result = actionItemReadOnlyCompositeService.openActionItem( ActionItem.findByName( 'All staff: Prepare for winter snow' ).id )
        assert result.success == true
        assert result.errors == []
        assert result.actionItem != null
    }


    @Test
    void openActionItemFailedCase() {
        def result = actionItemReadOnlyCompositeService.openActionItem( -99 )
        assert result.success == false
        assert result.actionItem == null
    }

}
