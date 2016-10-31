/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import com.sun.tools.corba.se.idl.SequenceGen
import org.hibernate.FlushMode

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActioinItemStatusRule.fetchActionItemStatusRules",
                query = """
           FROM ActionItemStatusRule a
          """),
        @NamedQuery(name = "ActionItemStatusRule.fetchActionItemStatusRuleById",
                query = """
           FROM ActionItemStatusRule a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId",
                query = """
            FROM ActionItemStatusRule a
            WHERE a.actionItemId = :myId
            """)
])

@Entity
@Table(name = "GCRAISR")

class ActionItemStatusRule implements Serializable {

    /**
     *  Surrogate ID for GCRAISR
     */
    @Id
    @Column(name = "GCRAISR_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAISR_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAISR_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAISR_SEQ_GEN")
    Long id

    /***
     * Related ID of the action item status rule - Action Item
     */
    @Column(name = "GCRAISR_ACTION_ITEM_ID")
    Long actionItemId

    /***
     * Sequence order in action item
     */
    @Column(name = "GCRAISR_SEQ_ORDER")
    Long seqOrder

    /***
     * Related ID of the action item status rule - Action Item Status ID
     */
    @Column(name = "GCRAISR_ACTION_ITEM_STATUS_ID")
    Long actionItemStatusId

    /***
     * Label for the Action Item Status Rule
     */
    @Column(name = "GCRAISR_LABEL_TEXT")
    String labelText

    /***
     * Version
     */
    @Version
    @Column(name = "GCRAISR_VERSION")
    Long version

    /***
     * User action item status rule was last updated by
     */
    @Column(name = "GCRAIDR_USER_ID")
    String userId

    /***
     * Last activity date for the action item status rule
     */
    @Column(name="GCRAISR_ACTIVITY_DATE")
    String activityDate

    /***
     * Data Origin column for GCRAISR
     */
    @Column(name="GCRAISR_DATA_ORIGIN")
    String dataOrigin

    @Override
    public String toString() {
        return "ActionItemStatusRule{" +
                "id=" + id +
                ", actionItemId=" + actionItemId +
                ", seqOrder=" + seqOrder +
                ", actionItemStatusId=" + actionItemStatusId +
                ", labelText='" + labelText + "\'" +
                ", userId='" + userId + "\'" +
                ", activityDate=" + activityDate +
                ", dataOrigin='" + dataOrigin + "\'" +
                ", version=" + version +
                "}"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ActionItemStatusRule)) return false

        ActionItemStatusRule that = (ActionItemStatusRule) o

        if (actionItemId != that.actionItemId) return false
        if (seqOrder !=  that.seqOrder) return false
        if (actionItemStatusId != that.actionItemStatusId) return false
        if (labelText != that.labelText) return false
        if (version != that.version) return false
        if (id != that.id) return false
        if (userId != that.userId) return false
        if (activityDate != that.activityDate) return false
        if (dataOrigin != that.dataOrigin) return false

        reutrn true
    }

    int hashCode() {
        int result = 0
        result = 31 * result + (id !=null ? id.hashCode() : 0)
        result = 31 * result + (actionItemId !=null ? actionItemId.hashCode() : 0)
        result = 31 * result + (seqOrder != null ? seqOrder.hashCode() : 0)
        result = 31 * result + (actionItemStatusId != null ? actionItemStatusId.hashCode() : 0)
        result = 31 * result + (labelText != null ? labelText.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0)
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0)
        return result
    }

    static constraints = {
        actionItemId(blank: false, nullable: false, maxSize: 19)
        seqOrder(blank: false, nullable: false, maxSize: 5)
        labelText(blank: false, nullable: false, maxSize: 150)
        actionItemStatusId(blank: true, nullable: true, maxSize: 19)
        userId(blank: false, nullable: false, maxSize: 30)
        activityDate(blank: false, nullable: false, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 30)
        version(nullable: true, maxSize: 30)
    }

    public static def fetchActionItemStatusRules() {
        ActionItemStatusRule.withSession { session ->
            List actionItemStatusRules = session.getNamedQuery("ActioinItemStatusRule.fetchActionItemStatusRules").list()
            return actionItemStatusRules
        }
    }

    public static fetchActionItemStatusRuleById( Long id ) {
        ActionItemStatusRule.withSession { session ->
            ActionItemStatusRule actionItemStatusRule = session.getNamedQuery( "ActionItemStatusRule.fetchActionItemStatusRuleById" ).setLong('myId', id)?.list()[0]
            return actionItemStatusRule
        }
    }
    public static fetchActionItemStatusRulesByActionItemId (long id) {
        ActionItemStatusRule.withSession { session ->
            List<ActionItemStatusRule> actionItemStatusRules = session.getNamedQuery("ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId").setLong("myId", id).list()
            return actionItemStatusRules
        }
    }
}
