/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "ActionItemReviewAudit.fetchReviewAuditByPidmAndActionItemId",
                query = """
           FROM ActionItemReviewAudit a
           WHERE a.pidm = :pidm and a.actionItemId =:actionItemId
          """)
])


@Entity
@Table(name = "GCBRAUD")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemReviewAudit implements Serializable {

    /**
     * Surrogate ID for GCBRAUD
     */

    @Id
    @Column(name = "GCBRAUD_SURROGATE_ID")
    @SequenceGenerator(name = "GCBRAUD_SEQ_GEN", allocationSize = 1, sequenceName = "GCBRAUD_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBRAUD_SEQ_GEN")
    Long id

    /**
     * Related ID of the action item
     */
    @Column(name = "GCBRAUD_ACTION_ITEM_ID")
    Long actionItemId

    /**
     * PIDM of the user action item belongs to
     */
    @Column(name = "GCBRAUD_USER_PIDM")
    Long pidm

    /**
     * ResponseID of action item
     */
    @Column(name = "GCBRAUD_RESPONSE_ID")
    Long responseId

    /**
     * Reviewer Pidm
     */
    @Column(name = "GCBRAUD_REVIEWER_PIDM")
    Long reviewerPidm

    /**
     * review date
     */
    @Column(name = "GCBRAUD_REVIEW_DATETIME")
    Date reviewDate

    /**
     * review decision
     */
    @Column(name = "GCBRAUD_RVST_CODE")
    String reviewStateCode

    /***
     * External comment Indicator
     */
    @Type(type = "yes_no")
    @Column(name = "GCBRAUD_EXTERNAL_COMMENT_IND")
    Boolean externalCommentInd

    /**
     * review comments
     */
    @Column(name = "GCBRAUD_REV_COMMENTS")
    String reviewComments

    /**
     * Contact info
     */
    @Column(name = "GCBRAUD_CONTACT_INFO")
    String contactInfo

    /**
     * User action item pertains to
     */
    @Column(name = "GCBRAUD_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCBRAUD_ACTIVITY_DATE")
    Date lastModified

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCBRAUD_VERSION")
    Long version

    /**
     * Data Origin column
     */
    @Column(name = "GCBRAUD_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        actionItemId( nullable: false, maxSize: 19 )
        pidm( nullable: false, maxSize: 8 )
        responseId( nullable: false, maxSize: 19 )
        reviewerPidm( nullable: false, maxSize: 8 )
        reviewDate( nullable: false )
        reviewStateCode( nullable: false, maxSize: 10 )
        externalCommentInd(nullable: false, maxSize: 1)
        reviewComments( nullable: true, maxSize: 4000 )
        contactInfo( nullable: true, maxSize: 200 )
        lastModifiedBy(nullable: true, maxSize: 30)
        lastModified(nullable: true)
        dataOrigin(nullable: true, maxSize: 30)
        version(nullable: true, maxSize: 30)
    }

    /**
     *
     * @param pidm,actionItemId
     * @return
     */
    public static def fetchReviewAuditByPidmAndActionItemId(Long pidm,Long actionItemId) {
        ActionItemReviewAudit.withSession { session ->
            List<ActionItemReviewAudit> actionItemReviewAuditList = session.getNamedQuery("ActionItemReviewAudit.fetchReviewAuditByPidmAndActionItemId")
                    .setLong("pidm", pidm)
                    .setLong("actionItemId",actionItemId)
                    .list()
            return actionItemReviewAuditList
        }
    }

}
