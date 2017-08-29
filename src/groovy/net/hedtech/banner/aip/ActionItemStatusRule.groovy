/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemStatusRule.fetchActionItemStatusRules",
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
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
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
    @Column(name = "GCRAISR_USER_ID")
    String userId

    /***
     * Last activity date for the action item status rule
     */
    @Column(name="GCRAISR_ACTIVITY_DATE")
    Date activityDate

    /***
     * Data Origin column for GCRAISR
     */
    @Column(name="GCRAISR_DATA_ORIGIN")
    String dataOrigin

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

    /**
     *
     * @return
     */
    public static def fetchActionItemStatusRules() {
        ActionItemStatusRule.withSession { session ->
            List actionItemStatusRules = session.getNamedQuery("ActionItemStatusRule.fetchActionItemStatusRules").list()
            return actionItemStatusRules
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public static fetchActionItemStatusRuleById( Long id ) {
        ActionItemStatusRule.withSession { session ->
            ActionItemStatusRule actionItemStatusRule = session.getNamedQuery( "ActionItemStatusRule.fetchActionItemStatusRuleById" ).setLong('myId', id)?.list()[0]
            return actionItemStatusRule
        }
    }

    /**
     *
     * @param id
     * @return
     */
    public static fetchActionItemStatusRulesByActionItemId (long id) {
        ActionItemStatusRule.withSession { session ->
            List<ActionItemStatusRule> actionItemStatusRules = session.getNamedQuery("ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId").setLong("myId", id).list()
            return actionItemStatusRules
        }
    }
}
