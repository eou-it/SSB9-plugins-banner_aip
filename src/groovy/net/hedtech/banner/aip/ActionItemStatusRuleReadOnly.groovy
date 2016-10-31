package net.hedtech.banner.aip

import org.hibernate.criterion.Order

import javax.persistence.*


@NamedQueries(value = [

        @NamedQuery(name = "ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById",
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

class ActionItemStatusRuleReadOnly implements Serializable{

    /**
     * ID for ActionItemStatusRule
     */
    @Id
    @Column (name = "STATUS_RULE_ID")
    Long statusRuleId

    /**
     * Action Item Status Rule Sequence Order
     */
    @Column (name = "STATUS_RULE_SEQ_ORDER")
    Long statusRuleSeqOrder

    /**
     * State Rule Label Text for display
     */
    @Column (name = "STATUS_RULE_LABEL_TEXT")
    Long statusRuleLabelText

    /**
     *
     */
    @Column (name = "STATUS_RULE_ACTIVITY_DATE")
    Date statusRuleActivityDate

    /**
     * Action Item Id
     */
    @Column ( name = "STATUS_RULE_ACTION_ITEM_ID")
    Long statusRuleActionItemId

    /**
     * Action Item Status
     */
    @Column (name = "STATUS_ID")
    Long statusId

    /**
     * Action Item Status Name
     */
    @Column ( name = "STATUS_NAME")
    String statusName

    /**
     * Action Item Status Block Indicator
     */
    @Column ( name = "STATUS_BLOCK_PROCESS_IND")
    String statusBlockProcessInd

    /**
     * Action Item Status System Required
     */
    @Column (name = "STATUS_SYSTEM_REQUIRED")
    String statusSystemRequired

    /**
     * Action Item Status Active Indicator
     */
    @Column (name = "STATUS_ACTIVE_IND")
    String statusActiveInd

    /**
     * Action Item Status Activity Date
     */
    @Column (name = "STATUS_ACTIVITY_DATE")
    Date statusActivityDate


    @Override
    public String toString() {
        return "ActionItemStatusRuleReadOnly{" +
                "statusRuleId=" + statusRuleId +
                ", statusRuleSeqOrder=" + statusRuleSeqOrder +
                ", statusRuleLabelText='" + statusRuleLabelText + "\'" +
                ", statusRuleActionItemId=" + statusRuleActionItemId +
                ", statusId=" + statusId +
                ", statusName='" + statusName + "\'" +
                ", statusBlockProcessInd='" + statusBlockProcessInd + "\'" +
                ", statusSystemRequired='" + statusSystemRequired + "\'" +
                ", statusActiveInd='" + statusActiveInd + "\'" +
                ", statusActivityDate=" + statusActivityDate +
                "}"
    }

    boolean equals( o ) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ActionItemStatusRuleReadOnly that = (ActionItemStatusRuleReadOnly) o

        if (statusRuleId != that.statusRuleId) return false
        if (statusRuleSeqOrder != that.statusRuleSeqOrder) return false
        if (statusRuleLabelText != that.statusRuleLabelText) return false
        if (statusRuleActivityDate != that.statusRuleActivityDate) return false
        if (statusRuleActionItemId != that.statusRuleActionItemId) return false
        if (statusId != that.statusId) return false
        if (statusName != that.statusName) return false
        if (statusBlockProcessInd != that.statusBlockProcessInd) return false
        if (statusActiveInd != that.statusActiveInd) return false
        if (statusSystemRequired != that.statusSystemRequired) return false
        if (statusActivityDate != that.statusActivityDate) return false
        return true
    }

    int hashCode() {
        int result = 0
        result = 31 * result + (statusRuleId != null ? statusRuleId.hashCode() : 0)
        result = 31 * result + (statusRuleSeqOrder != null ? statusRuleSeqOrder.hashCode() : 0)
        result = 31 * result + (statusRuleLabelText != null ? statusRuleLabelText.hashCode() : 0)
        result = 31 * result + (statusRuleActivityDate != null ? statusRuleActivityDate.hashCode() : 0)
        result = 31 * result + (statusRuleActionItemId != null ? statusRuleActionItemId.hashCode() : 0)
        result = 31 * result + (statusId != null ? statusId.hashCode() : 0)
        result = 31 * result + (statusName != null ? statusName.hashCode() : 0)
        result = 31 * result + (statusBlockProcessInd != null ? statusBlockProcessInd.hashCode() : 0)
        rerule = 31 * result + (statusActiveInd != null ? statusActiveInd.hashCode() : 0)
        result = 31 * result + (statusSystemRequired != null ? statusSystemRequired.hashCode() : 0)
        result = 31 * result + (statusActivityDate != null ? statusActivityDate.hashCode() : 0)
        return result;
    }

    public static def fetchActionItemStatusRuleROById (Long myId) {
        ActionItemStatusRuleReadOnly.withSession { session ->
            ActionItemStatusRuleReadOnly actionitemStatusRuleRO = session.getNamedQuery ("ActionItemStatusRuleReadOnly.fetchActionItemStatusRuleROById").setLong("myId", myId).list()[0]
            return actionitemStatusRuleRO
        }
    }
    public static fetchActionItemStatusRuleRO() {
        ActionItemStatusRuleReadOnly.withSession { session ->
            List<ActionItemStatusRuleReadOnly> actionItemStatusRule = session.getNamedQuery("ActionItemStatusRuleReadOnl.fetchActionItemStatusRuleRO").list()
            return actionItemStatusRule
        }
    }
    public static fetchActionItemStatusRuleROByActionItemId (Long myId) {
        ActionItemStatusRuleReadOnly.withSession { session ->
            List<ActionItemStatusRuleReadOnly> actionItemStatusRuleROs = session.getNamedQuery("ActionItemStatusRuleReadOnly.fetchActionItemStatusByActionItem").setLong("myId", myId).list()
            return actionItemStatusRuleROs
        }
    }

}
