/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemStatusRuleServiceIntegrationTest extends BaseIntegrationTestCase {

    def actionItemStatusRuleService

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
    void testListActionItemStatusRules() {
        List<ActionItemStatusRule> actionItemStatusRules = actionItemStatusRuleService.listActionItemStatusRules()
        assertTrue(actionItemStatusRules.size()>0)
    }

    @Test
    void testListActionItemStatusRuleById() {
        ActionItemStatusRule aisr = actionItemStatusRuleService.listActionItemStatusRules()[0]
        assertNotNull(aisr)
        ActionItemStatusRule actionItemStatusRule = actionItemStatusRuleService.getActionItemStatusRuleById(aisr.id)
        assertEquals(aisr.id, actionItemStatusRule.id)
        assertEquals(aisr.labelText, actionItemStatusRule.labelText)
        assertEquals(aisr.seqOrder, actionItemStatusRule.seqOrder)
        assertEquals(aisr.actionItemStatusId, actionItemStatusRule.actionItemStatusId)
        assertEquals(aisr.actionItemId, actionItemStatusRule.actionItemId)
    }


}
