/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemStatusServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemStatusService


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
    void testListActionItemStatusById() {
        def aiStatus = actionItemStatusService.listActionItemStatuses()[1]
        assertNotNull( aiStatus )
        ActionItemStatus actionItemStatus = actionItemStatusService.listActionItemStatusById( aiStatus.id )
        assertEquals( aiStatus.actionItemStatus, actionItemStatus.actionItemStatus )
        assertEquals( aiStatus.actionItemStatusBlockedProcess, actionItemStatus.actionItemStatusBlockedProcess )
        assertEquals( aiStatus.actionItemStatusSystemRequired, actionItemStatus.actionItemStatusSystemRequired )
        assertEquals( aiStatus.actionItemStatusActive, actionItemStatus.actionItemStatusActive )
    }


    @Test
    void testListActionItemStatusServiceById() {
        List<ActionItemStatus> actionItemStatusList = actionItemStatusService.listActionItemStatuses()
        def id = actionItemStatusList[0].id
        def actionItemStatus = actionItemStatusList[0].actionItemStatus
        ActionItemStatus actionItemStatusById = actionItemStatusService.listActionItemStatusById( id )
        assertNotNull actionItemStatusById
        assertEquals( actionItemStatusById.actionItemStatus, actionItemStatus )
    }


    @Test
    void testListActionItemStatusCount() {
        assert 0 < actionItemStatusService.listActionItemStatusCount('%')
    }


    @Test
    void checkIfNameAlreadyPresent() {
        List<ActionItemStatus> actionItemStatusList = actionItemStatusService.listActionItemStatuses()
        assertTrue actionItemStatusService.checkIfNameAlreadyPresent( actionItemStatusList[0].actionItemStatus )
    }


    @Test
    void checkIfNameAlreadyPresentCaseTest() {
        assertTrue actionItemStatusService.checkIfNameAlreadyPresent( 'Completed' )
        assertTrue actionItemStatusService.checkIfNameAlreadyPresent( 'completed' )
        assertTrue actionItemStatusService.checkIfNameAlreadyPresent( 'COMPLETED' )
        assertTrue actionItemStatusService.checkIfNameAlreadyPresent( 'CompLeTeD' )
    }
}
