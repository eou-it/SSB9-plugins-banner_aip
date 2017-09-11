/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemStatusServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemStatusCompositeService

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
    void testListActionItemStatusById() {
        ActionItemStatus aiStatus = actionItemStatusService.listActionItemStatuses(  )[1]
        assertNotNull( aiStatus )
        ActionItemStatus actionItemStatus = actionItemStatusService.listActionItemStatusById( aiStatus.id )
        assertEquals( aiStatus.actionItemStatus, actionItemStatus.actionItemStatus )
        assertEquals( aiStatus.actionItemStatusBlockedProcess, actionItemStatus.actionItemStatusBlockedProcess )
        assertEquals( aiStatus.actionItemStatusSystemRequired, actionItemStatus.actionItemStatusSystemRequired )
        assertEquals( aiStatus.actionItemStatusActive, actionItemStatus.actionItemStatusActive )
    }


    /*
    @Test
    void testListActionItemStatusServiceById() {
        List<ActionItemStatus> actionItemStatusList = actionItemStatusService.listActionItemStatuses()
        def id = actionItemStatusList[0].id
        def actionItemStatus = actionItemStatusList[0].actionItemStatus
        List<ActionItemStatus> actionItemStatusById = actionItemStatusService.listActionItemStatusById( id )
        assertFalse actionItemStatusById.isEmpty(  )
        assertEquals(actionItemStatusById[0].actionItemStatus, actionItemStatus)
        assertEquals( 1, actionItemStatusById.size() )
    }
    */


    @Test
    void testFetchActionItemStatusPageSortService() {
        Map params1 = [filterName:"%",sortColumn:"actionItemStatus", sortAscending:true, max:10, offset:0]

        Map params2 = [filterName:"%",sortColumn:"actionItemStatus", sortAscending:true, max:10, offset:10]

        Map params3 = [filterName:"%",sortColumn:"actionItemStatus", sortAscending:true, max:20, offset:20]

        def actionItemStatusList1 = actionItemStatusCompositeService.listActionItemsPageSort(params1)
       // def actionItemStatusList2 = actionItemStatusService.listActionItemsPageSort(params2)
       // def actionItemStatusList3 = actionItemStatusService.listActionItemsPageSort(params3)

        def totalCount = actionItemStatusList1.result.size()
        //+ actionItemStatusList2.result.size() + actionItemStatusList3.result.size()

        def actualLength = actionItemStatusList1.length

        assertEquals( actualLength, totalCount )
    }


}
