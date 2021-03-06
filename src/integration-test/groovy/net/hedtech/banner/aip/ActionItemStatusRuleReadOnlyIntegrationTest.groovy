/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
class ActionItemStatusRuleReadOnlyIntegrationTest extends BaseIntegrationTestCase{
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
    void testFetchActionItemStatusReadOnly() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusROs = ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
        assertFalse actionItemStatusROs.isEmpty()
    }

    @Test
    void testFetchActionItemStatusReadOnlyById() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusROs = ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
        ActionItemStatusRuleReadOnly actionItemStatusRuleReadOnly =
                ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById(actionItemStatusROs[0].statusRuleId)
        assertEquals (actionItemStatusROs[0].statusRuleId, actionItemStatusRuleReadOnly.statusRuleId)
    }

    @Test
    void testFetchActionItemStatusReadOnluByActionItemId() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusROs = ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlys =
                ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesROByActionItemId(actionItemStatusROs[0].statusRuleActionItemId)
        assertFalse actionItemStatusRuleReadOnlys.isEmpty()
        assertEquals(actionItemStatusRuleReadOnlys[0].statusRuleActionItemId, actionItemStatusROs[0].statusRuleActionItemId)
    }

    @Test
    void testIfReviewRequiredNotNull() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusROs = ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
        actionItemStatusROs.each { response ->
            assertNotNull response.statusReviewRequired
        }
    }

    @Test
    void testFetchActionItemStatusRuleReadOnlyString() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
        def actionItemStatusRuleRO = actionItemStatusRuleReadOnlies[0]
        assertNotNull actionItemStatusRuleRO.toString()
        assertFalse actionItemStatusRuleRO.toString().isEmpty()
    }

    @Test
    void testActionItemStatusRuleROHashCode() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesRO()
        def actionItemStatusRO = actionItemStatusRuleReadOnlies[0]
        def result = actionItemStatusRO.hashCode()
        assertNotNull result
        def actinItemStatusRuleROObj = new ActionItemStatusRuleReadOnly()
        assertNotNull actinItemStatusRuleROObj
    }

}
