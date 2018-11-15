/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "AipReviewState.fetchReviewStateByCodeAndLocale",
                query = """FROM AipReviewState a
           WHERE a.reviewStateCode = :code
           and upper(a.locale) = upper(:locale)
          """)
])

@Entity
@Table(name = "GCVRVST")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain for Review State
 */

class AipReviewState implements Serializable {

    /**
     * Surrogate ID for GCVRVST.
     */
    @Id
    @Column(name = "GCVRVST_SURROGATE_ID")
    @SequenceGenerator(name = "GCVRVST_SEQ_GEN", allocationSize = 1, sequenceName = "GCVRVST_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCVRVST_SEQ_GEN")
    Long id

    /**
     * Code for Review State.
     */
    @Column(name = "GCVRVST_REVIEW_STATE_CODE")
    String reviewStateCode

    /**
     * Name of the Review State.
     */
    @Column(name = "GCVRVST_REVIEW_STATE_NAME")
    String reviewStateName

    /**
     * Locale for Review State Name.
     */
    @Column(name = "GCVRVST_LOCALE")
    String locale

    /**
     * Review Ongoing Indicator indicates that review is in progress.
     */
    @Column(name = "GCVRVST_REVIEW_ONGOING_IND")
    String reviewOngoingInd

    /**
     * Review Success Indicator indicates if review is successful.
     */
    @Column(name = "GCVRVST_REVIEW_SUCCESS_IND")
    String reviewSuccessInd

    /**
     * Default indicator indicates that this Review State is default.
     */
    @Column(name = "GCVRVST_DEFAULT_IND")
    String defaultInd

    /**
     * Active Indicator indicates that the Review State is active.
     */
    @Column(name = "GCVRVST_ACTIVE_IND")
    String activeInd

    /**
     * User ID that created or modified the Review State.
     */
    @Column(name = "GCVRVST_USER_ID")
    String userId

    /**
     * Last activity date for the Review State.
     */
    @Column(name = "GCVRVST_ACTIVITY_DATE")
    Date activityDate

    /**
     * Comments for the Review State.
     */
    @Column(name = "GCVRVST_COMMENTS")
    String comments

    /**
     * Version of the Review State.
     */
    @Version
    @Column(name = "GCVRVST_VERSION")
    Long version

    /**
     * Data Origin.
     */

    @Column(name = "GCVRVST_DATA_ORIGIN")
    String dataOrigin

    /**
     * VPDI Code for MEP.
     */
    @Column(name = "GCVRVST_VPDI_CODE")
    String vpdiCode

    static constraints = {
        id(nullable: false, maxSize: 19)
        reviewStateCode(nullable: false, maxSize: 10)
        reviewStateName(nullable: false, maxSize: 30)
        locale(nullable: false, maxSize: 20)
        reviewOngoingInd(nullable: false, maxSize: 1)
        reviewSuccessInd(nullable: false, maxSize: 1)
        defaultInd(nullable: false, maxSize: 1)
        activeInd(nullable: false, maxSize: 1)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false)
        comments(nullable: true, maxSize: 2000)
        version(nullable: true, maxSize: 19)
        dataOrigin(nullable: true, maxSize: 30)
        vpdiCode(nullable: true, maxSize: 6)
    }

    /**
     *
     * @param code Code of review state
     * @param locale User locale
     * @return List of Review States
     */
    static def fetchReviewStateByCodeAndLocale(String code, String locale) {
        AipReviewState.withSession { session ->
            session.getNamedQuery('AipReviewState.fetchReviewStateByCodeAndLocale')
                    .setString('code', code).setString('locale', locale)?.list()[0]
        }
    }

}
