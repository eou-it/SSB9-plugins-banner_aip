/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemStatusRuleReadOnlyIntegrationTest extends BaseIntegrationTestCase{
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
    void testFetchActionItemStatusReadOnly() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusROs = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO()
        assertFalse actionItemStatusROs.isEmpty()
    }

    @Test
    void testFetchActionItemStatusReadOnlyById() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusROs = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO()
        ActionItemStatusRuleReadOnly actionItemStatusRuleReadOnly = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById(actionItemStatusROs[0].statusRuleId)
        assertEquals (actionItemStatusROs[0].statusRuleId, actionItemStatusRuleReadOnly.statusRuleId)
    }

    @Test
    void testFetchActionItemStatusReadOnluByActionItemId() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusROs = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO()
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlys = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROByActionItemId(actionItemStatusROs[0].statusRuleActionItemId)
        assertFalse actionItemStatusRuleReadOnlys.isEmpty()
        assertEquals(actionItemStatusRuleReadOnlys[0].statusRuleActionItemId, actionItemStatusROs[0].statusRuleActionItemId)
    }

    @Test
    void testFetchActionItemStatusRuleReadOnlyString() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO()
        def actionItemStatusRuleRO = actionItemStatusRuleReadOnlies[0]
        assertNotNull actionItemStatusRuleRO.toString()
        assertFalse actionItemStatusRuleRO.toString().isEmpty()
    }

    @Test
    void testActionItemStatusRuleROHashCode() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO()
        def actionItemStatusRO = actionItemStatusRuleReadOnlies[0]
        def result = actionItemStatusRO.hashCode()
        assertNotNull result
        def actinItemStatusRuleROObj = new ActionItemStatusRuleReadOnly()
        assertNotNull actinItemStatusRuleROObj
    }

    @Test
    void testActionItemStatusRuleROEquals() {
        List<ActionItemStatusRuleReadOnly> actionItemStatusRuleReadOnlies = ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO()
        def actionItemStatusRuleRO = actionItemStatusRuleReadOnlies[0]
        def newActionItemStatusRuleRO = new ActionItemStatusRuleReadOnly(
                statusRuleSeqOrder: actionItemStatusRuleRO.statusRuleSeqOrder,
                statusRuleLabelText: actionItemStatusRuleRO.statusRuleLabelText,
                statusRuleActivityDate: actionItemStatusRuleRO.statusRuleActivityDate,
                statusRuleActionItemId: actionItemStatusRuleRO.statusRuleActionItemId,
                statusId: actionItemStatusRuleRO.statusId,
                statusName: actionItemStatusRuleRO.statusName,
                statusBlockProcessInd: actionItemStatusRuleRO.statusBlockProcessInd,
                statusSystemRequired: actionItemStatusRuleRO.statusSystemRequired,
                statusActiveInd: actionItemStatusRuleRO.statusActiveInd,
                statusActivityDate: actionItemStatusRuleRO.statusActivityDate,
                statusRuleVersion: actionItemStatusRuleRO.statusRuleVersion
        )
        newActionItemStatusRuleRO.statusRuleId = actionItemStatusRuleRO.statusRuleId

        def result = actionItemStatusRuleRO.equals(actionItemStatusRuleRO)
        assertTrue result

        result = actionItemStatusRuleRO.equals(newActionItemStatusRuleRO)
        assertTrue result

        def actionItemStatusRuleRONull = new ActionItemStatusRuleReadOnly(null)
        result = actionItemStatusRuleRO.equals(actionItemStatusRuleRONull)
        assertFalse result
    }
}
