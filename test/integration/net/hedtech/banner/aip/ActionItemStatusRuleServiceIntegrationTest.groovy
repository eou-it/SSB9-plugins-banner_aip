/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemStatusRuleServiceIntegrationTest extends BaseIntegrationTestCase {

    def actionItemStatusRuleService


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
    void testListActionItemStatusRules() {
        List<ActionItemStatusRule> actionItemStatusRules = actionItemStatusRuleService.listActionItemStatusRules()
        assertTrue( actionItemStatusRules.size() > 0 )
    }


    @Test
    void testListActionItemStatusRuleById() {
        ActionItemStatusRule aisr = actionItemStatusRuleService.listActionItemStatusRules()[0]
        assertNotNull( aisr )
        ActionItemStatusRule actionItemStatusRule = actionItemStatusRuleService.getActionItemStatusRuleById( aisr.id )
        assertEquals( aisr.id, actionItemStatusRule.id )
        assertEquals( aisr.labelText, actionItemStatusRule.labelText )
        assertEquals( aisr.seqOrder, actionItemStatusRule.seqOrder )
        assertEquals( aisr.actionItemStatusId, actionItemStatusRule.actionItemStatusId )
        assertEquals( aisr.resubmitInd, actionItemStatusRule.resubmitInd )
        assertEquals( aisr.actionItemId, actionItemStatusRule.actionItemId )
    }


    @Test
    void testGetActionItemStatusRuleByActionItemId() {
        List<ActionItemStatusRule> actionItemStatusRules = actionItemStatusRuleService.getActionItemStatusRuleByActionItemId( ActionItem.findByName( 'Policy Handbook' ).id )
        assertTrue( actionItemStatusRules.size() > 0 )
        ActionItemStatusRule obj = actionItemStatusRules.find() {
            it.seqOrder == 2
        }
        assert obj.seqOrder == 2
        assert obj.labelText == 'I beg to differ.'
    }


    @Test
    void testValidateSuccessCases() {
        ActionItemStatusRule actionItemStatusRule = new ActionItemStatusRule(
                actionItemId: 1,
                seqOrder: 1,
                labelText: 'Text'
        )
        actionItemStatusRuleService.validate( actionItemStatusRule )
        actionItemStatusRuleService.validate( [domainModel: [
                actionItemId: 1,
                seqOrder    : 1,
                labelText   : 'Text'
        ]] )

    }


    @Test
    void testValidateNullActionItemId() {

        try {
            actionItemStatusRuleService.validate( [domainModel: [
                    actionItemId: null,
                    seqOrder    : 1,
                    labelText   : 'Text'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'ActionItemIdCanNotBeNullError' )
        }
    }


    @Test
    void testValidateNullSeqOrder() {
        try {
            actionItemStatusRuleService.validate( [domainModel: [
                    actionItemId: 1,
                    seqOrder    : null,
                    labelText   : 'Text'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'SeqOrderCanNotBeNullError' )
        }
    }


    @Test
    void testValidateNullLabelText() {
        try {
            actionItemStatusRuleService.validate( [domainModel: [
                    actionItemId: 1,
                    seqOrder    : 1,
                    labelText   : null
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'LabelTextCanNotBeNullError' )
        }
    }


    @Test
    void testValidateSizeLimit() {
        try {
            actionItemStatusRuleService.validate( [domainModel: [
                    actionItemId: 1,
                    seqOrder    : 1,
                    labelText   : 'Text', resubmitInd: 'ASKHKSHKHKHKSAHKJHKHSKHSKHAKSKHKHKHKHS'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'MaxSizeError' )
        }
    }


    @Test
    void testValidateInvalidInput() {
        try {
            actionItemStatusRuleService.validate( [domainModel: [
                    actionItemId: '1XYZ',
                    seqOrder    : 1,
                    labelText   : 'Text'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'ValidationError' )
        }
    }
}
