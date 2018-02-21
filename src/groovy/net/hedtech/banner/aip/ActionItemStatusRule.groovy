/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
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
        @NamedQuery(name = "ActionItemStatusRule.getActionItemStatusRuleNameByStatusIdAndActionItemId",
                query = """
                  FROM ActionItemStatusRule a WHERE a.actionItemId = :actionItemId and a.actionItemStatusId = :statusId
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
            """),
        @NamedQuery(name = "ActionItemStatusRule.checkIfPresent",
                query = """
                        select count (*) FROM ActionItemStatusRule a
                        WHERE a.actionItemStatusId = :actionItemStatusId
                        """),
        @NamedQuery(name = "ActionItemStatusRule.checkIfPresentAndAssociatedToActionItemContent",
                query = """
                                                select count (*) FROM ActionItemStatusRule a
                                                WHERE a.actionItemStatusId = :actionItemStatusId
                                                AND EXISTS (select b.actionItemId from UserActionItem b where b.actionItemId = a.actionItemId )
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
    @Column(name = "GCRAISR_GCBACTM_ID")
    Long actionItemId

    /***
     * Sequence order in action item
     */
    @Column(name = "GCRAISR_SEQ_ORDER")
    Long seqOrder

    /***
     * Related ID of the action item status rule - Action Item Status ID
     */
    @Column(name = "GCRAISR_GCVASTS_ID")
    Long actionItemStatusId

    /***
     * Resubmit Indicator
     */
    @Column(name = "GCRAISR_RESUBMIT_IND")
    String resubmitInd

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
    String lastModifiedBy

    /***
     * Last activity date for the action item status rule
     */
    @Column(name = "GCRAISR_ACTIVITY_DATE")
    Date lastModified

    /***
     * Data Origin column for GCRAISR
     */
    @Column(name = "GCRAISR_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        actionItemId( blank: false, nullable: false, maxSize: 19 )
        seqOrder( blank: false, nullable: false, maxSize: 5 )
        labelText( blank: false, nullable: false, maxSize: 150 )
        actionItemStatusId( blank: true, nullable: true, maxSize: 19 )
        resubmitInd( blank: true, nullable: true, maxSize: 1 )
        lastModifiedBy( nullable: true, maxSize: 30 )
        lastModified( nullable: true )
        dataOrigin( nullable: true, maxSize: 30 )
        version( nullable: true, maxSize: 30 )
    }

    /**
     *
     * @return
     */
    static def fetchActionItemStatusRules() {
        ActionItemStatusRule.withSession {session ->
            List actionItemStatusRules = session.getNamedQuery( "ActionItemStatusRule.fetchActionItemStatusRules" ).list()
            return actionItemStatusRules
        }
    }

    /**
     *
     * @param id
     * @return
     */
    static fetchActionItemStatusRuleById( Long id ) {
        ActionItemStatusRule.withSession {session ->
            ActionItemStatusRule actionItemStatusRule = session.getNamedQuery( "ActionItemStatusRule.fetchActionItemStatusRuleById" ).setLong( 'myId', id )?.list()[0]
            return actionItemStatusRule
        }
    }

    /**
     *
     * @param id
     * @return
     */
    static fetchActionItemStatusRulesByActionItemId( long id ) {
        ActionItemStatusRule.withSession {session ->
            List<ActionItemStatusRule> actionItemStatusRules = session.getNamedQuery( "ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId" ).setLong( "myId", id ).list()
            return actionItemStatusRules
        }
    }

    /**
     *
     * @param actionItemStatusId
     * @return
     */
    static checkIfPresent( long actionItemStatusId ) {
        ActionItemStatusRule.withSession {session ->
            session.getNamedQuery( "ActionItemStatusRule.checkIfPresent" ).setLong( "actionItemStatusId", actionItemStatusId ).uniqueResult() > 0
        }
    }

    /**
     *
     * @param actionItemStatusId
     * @return
     */
    static checkIfPresentAndAssociatedToActionItemContent( long actionItemStatusId ) {
        ActionItemStatusRule.withSession {session ->
            session.getNamedQuery( "ActionItemStatusRule.checkIfPresentAndAssociatedToActionItemContent" ).setLong( "actionItemStatusId", actionItemStatusId ).uniqueResult() > 0
        }
    }

    /**
     *
     * @param actionItemId
     * @param statusId
     * @return
     */
    static getActionItemStatusRuleNameByStatusIdAndActionItemId( Long statusId, Long actionItemId) {
        ActionItemStatusRule.withSession {session ->
            session.getNamedQuery( "ActionItemStatusRule.getActionItemStatusRuleNameByStatusIdAndActionItemId" )
                    .setLong( "actionItemId", actionItemId )
                    .setLong( "statusId", statusId )
                    .list()
        }
    }

}
