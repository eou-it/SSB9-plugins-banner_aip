/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Test class for Action Item Status Rule Readonly
 */
@Integration
@Rollback
class ActionItemStatusRuleReadOnlyServiceIntegrationTest extends BaseIntegrationTestCase {

    def actionItemStatusRuleReadOnlyService

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
    void testListActionItemStatusRuleRO() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = actionItemStatusRuleReadOnlyService.listActionItemStatusRulesRO()
        assertFalse actionItemStatusRuleReadOnlies.isEmpty()
        assertTrue(actionItemStatusRuleReadOnlies.size() > 0)
    }

    @Test
    void testGetActionItemStatusRuleROById() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = actionItemStatusRuleReadOnlyService.listActionItemStatusRulesRO()
        ActionItemStatusRuleReadOnly actionItemStatusRuleReadOnly = actionItemStatusRuleReadOnlyService.getActionItemStatusRuleROById(actionItemStatusRuleReadOnlies[0].statusRuleId)
        assertTrue(actionItemStatusRuleReadOnlies[0].equals(actionItemStatusRuleReadOnly))
    }

    @Test
    void testGetAcionItemStatusRuleROByActionItemId() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = actionItemStatusRuleReadOnlyService.listActionItemStatusRulesRO()
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies1 = actionItemStatusRuleReadOnlyService
                .getActionItemStatusRulesROByActionItemId(actionItemStatusRuleReadOnlies[0].statusRuleActionItemId)
        assertFalse(actionItemStatusRuleReadOnlies1.isEmpty())
        assertEquals(actionItemStatusRuleReadOnlies[0].statusRuleActionItemId, actionItemStatusRuleReadOnlies1[0].statusRuleActionItemId)

    }

    @Test
    void testIfReviewRequiredPresentInResponseList() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = actionItemStatusRuleReadOnlyService.listActionItemStatusRulesRO()
        actionItemStatusRuleReadOnlies.each { response ->
            assertNotNull response.statusReviewRequired
        }
    }
}
