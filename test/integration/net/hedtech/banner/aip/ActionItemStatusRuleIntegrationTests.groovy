/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemStatusRuleIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchActionItemStatusRules() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        assertFalse actionItemStatusRules.isEmpty()
    }


    @Test
    void testFetchActoinItemStatusRuleById() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRuleTemp = actionItemStatusRules[0]
        ActionItemStatusRule actionItemStatusRule = ActionItemStatusRule.fetchActionItemStatusRuleById( actionItemStatusRuleTemp.id )
        assertEquals( actionItemStatusRuleTemp.id, actionItemStatusRule.id )
    }


    @Test
    void testFetchActionItemStatusRuleByActionItemId() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRuleTemp = actionItemStatusRules[0]
        List<ActionItemStatusRule> actionItemStatusRulesNew = ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId( actionItemStatusRuleTemp.actionItemId )
        assertNotNull actionItemStatusRulesNew
        assertTrue actionItemStatusRulesNew.size() >= 1
    }


    @Test
    void testFetchActionItemStatusRulesString() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRule = actionItemStatusRules[0]
        assertNotNull( actionItemStatusRule.toString() )
        assertFalse actionItemStatusRules.isEmpty()
    }


    @Test
    void testActionItemStatusRuleHashCode() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRule = actionItemStatusRules[0]

        def result = actionItemStatusRule.hashCode()
        assertNotNull result

        def actionItemStatusRuleObj = new ActionItemStatusRule()
        assertNotNull actionItemStatusRuleObj
    }


    @Test
    void testActionItemStatusRuleEquals() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRule = actionItemStatusRules[0]
        def newActionItemStatusRule = new ActionItemStatusRule(
                actionItemId: actionItemStatusRule.actionItemId,
                seqOrder: actionItemStatusRule.seqOrder,
                actionItemStatusId: actionItemStatusRule.actionItemStatusId,
                resubmitInd: actionItemStatusRule.resubmitInd,
                lastModified: actionItemStatusRule.lastModified,
                lastModifiedBy: actionItemStatusRule.lastModifiedBy,
                version: actionItemStatusRule.version,
                dataOrigin: actionItemStatusRule.dataOrigin,
                labelText: actionItemStatusRule.labelText
        )
        newActionItemStatusRule.id = actionItemStatusRule.id
        newActionItemStatusRule.version = actionItemStatusRule.version

        def result = actionItemStatusRule.equals( actionItemStatusRule )
        assertTrue result

        result = actionItemStatusRule.equals( newActionItemStatusRule )
        assertTrue result

        result = actionItemStatusRule.equals( null )
        assertFalse result

        def actionItemStatusRuleNull = new ActionItemStatusRule( null )
        result = actionItemStatusRule.equals( actionItemStatusRuleNull )
        assertFalse result
    }


    @Test
    void testNullActionItemIdError() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRule = actionItemStatusRules[0]
        def newActionItemStatusRule = new ActionItemStatusRule()

        newActionItemStatusRule.actionItemStatusId = actionItemStatusRule.actionItemStatusId
        newActionItemStatusRule.resubmitInd = actionItemStatusRule.resubmitInd
        newActionItemStatusRule.seqOrder = actionItemStatusRule.seqOrder
        newActionItemStatusRule.version = actionItemStatusRule.version
        newActionItemStatusRule.labelText = actionItemStatusRule.labelText

        assertFalse newActionItemStatusRule.validate()
        assertTrue( newActionItemStatusRule.errors.allErrors.codes[0].contains( 'actionItemStatusRule.actionItemId.nullable.error' ) )
    }


    @Test
    void testNullSeqOrderError() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRule = actionItemStatusRules[0]
        def newActionItemStatusRule = new ActionItemStatusRule()

        newActionItemStatusRule.actionItemId = actionItemStatusRule.actionItemId
        newActionItemStatusRule.actionItemStatusId = actionItemStatusRule.actionItemStatusId
        newActionItemStatusRule.resubmitInd = actionItemStatusRule.resubmitInd
        newActionItemStatusRule.version = actionItemStatusRule.version
        newActionItemStatusRule.labelText = actionItemStatusRule.labelText

        assertFalse newActionItemStatusRule.validate()
        assertTrue( newActionItemStatusRule.errors.allErrors.codes[0].contains( 'actionItemStatusRule.seqOrder.nullable.error' ) )
    }


    @Test
    void testNullLabelTextError() {
        List<ActionItemStatusRule> actionItemStatusRules = ActionItemStatusRule.fetchActionItemStatusRules()
        def actionItemStatusRule = actionItemStatusRules[0]
        def newActionItemStatusRule = new ActionItemStatusRule()

        newActionItemStatusRule.actionItemId = actionItemStatusRule.actionItemId
        newActionItemStatusRule.seqOrder = actionItemStatusRule.seqOrder
        newActionItemStatusRule.actionItemStatusId = actionItemStatusRule.actionItemStatusId
        newActionItemStatusRule.resubmitInd = actionItemStatusRule.resubmitInd
        newActionItemStatusRule.version = actionItemStatusRule.version

        assertFalse newActionItemStatusRule.validate()
        assertTrue( newActionItemStatusRule.errors.allErrors.codes[0].contains( 'actionItemStatusRule.labelText.nullable.error' ) )
    }

}
