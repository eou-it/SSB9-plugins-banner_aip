/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

@NamedQueries(value = [

        @NamedQuery(name = "ActionItemStatusRuleReadOnly.fetchActionItemStatusRulesROById",
                query = """
           FROM ActionItemStatusRuleReadOnly a
           WHERE a.statusRuleId = :myId
          """),
        @NamedQuery(name = "ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO",
                query = """
           FROM ActionItemStatusRuleReadOnly a
          """),
        @NamedQuery(name = "ActionItemStatusRuleReadOnly.fetchActionItemStatusByActionItem",
                query = """
            FROM ActionItemStatusRuleReadOnly a
            WHERE a.statusRuleActionItemId = :myId
            """)
])

@Entity
@Table(name = "GVQ_GCRAISR")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemStatusRuleReadOnly implements Serializable {

    /**
     * ID for ActionItemStatusRule
     */
    @Id
    @Column(name = "STATUS_RULE_ID")
    Long statusRuleId

    /**
     * Action Item Status Rule Sequence Order
     */
    @Column(name = "STATUS_RULE_SEQ_ORDER")
    Long statusRuleSeqOrder

    /**
     * State Rule Label Text for display
     */
    @Column(name = "STATUS_RULE_LABEL_TEXT")
    String statusRuleLabelText

    /**
     * version
     */
    @Version
    @Column(name = "STATUS_RULE_VERSION")
    Long statusRuleVersion

    /**
     *
     */
    @Column(name = "STATUS_RULE_ACTIVITY_DATE")
    Date statusRuleActivityDate

    /**
     * Action Item Id
     */
    @Column(name = "STATUS_RULE_ACTION_ITEM_ID")
    Long statusRuleActionItemId

    /**
     * Action Item Status
     */
    @Column(name = "STATUS_ID")
    Long statusId

    /**
     * Action Item Status Name
     */
    @Column(name = "STATUS_NAME")
    String statusName

    /**
     * Action Item Status Block Indicator
     */
    @Column(name = "STATUS_BLOCK_PROCESS_IND")
    String statusBlockProcessInd

    /**
     * Action Item Status System Required
     */
    @Column(name = "STATUS_SYSTEM_REQUIRED")
    String statusSystemRequired

    /**
     * Action Item Status Active Indicator
     */
    @Column(name = "STATUS_ACTIVE_IND")
    String statusActiveInd

    /**
     * Action Item Status Activity Date
     */
    @Column(name = "STATUS_ACTIVITY_DATE")
    Date statusActivityDate

    /**
     *
     * @param myId
     * @return
     */
    public static def fetchActionItemStatusRuleROById( Long myId ) {
        ActionItemStatusRuleReadOnly.withSession {session ->
            ActionItemStatusRuleReadOnly actionitemStatusRuleRO = session.getNamedQuery( "ActionItemStatusRuleReadOnly" +
                                                                                                 ".fetchActionItemStatusRulesROById" ).setLong( "myId", myId ).list()[0]
            return actionitemStatusRuleRO
        }
    }

    /**
     *
     * @return
     */
    public static fetchActionItemStatusRulesRO() {
        ActionItemStatusRuleReadOnly.withSession {session ->
            List<ActionItemStatusRuleReadOnly> actionItemStatusRule = session.getNamedQuery( "ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleRO" ).list()
            return actionItemStatusRule
        }
    }

    /**
     *
     * @param myId
     * @return
     */
    public static fetchActionItemStatusRulesROByActionItemId( Long myId ) {
        ActionItemStatusRuleReadOnly.withSession {session ->
            List<ActionItemStatusRuleReadOnly> actionItemStatusRuleROs = session.getNamedQuery( "ActionItemStatusRuleReadOnly.fetchActionItemStatusByActionItem" ).setLong( "myId", myId ).list()
            return actionItemStatusRuleROs
        }
    }
}
