/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Version



@NamedQueries(value = [
        @NamedQuery(name = "AipReviewState.fetchReviewStateByCodeAndLocale",
                query = """FROM AipReviewState a
           WHERE a.reviewStateCode = :code
           and (upper(a.locale) = upper(:locale) or upper(a.locale) = 'EN_US')
          """),
        @NamedQuery(name = "AipReviewState.fetchNonDefaultReviewStates",
                        query = """FROM AipReviewState a
                   WHERE a.reviewStateCode != ( SELECT d.defReviewStateCode FROM AipDefaultReviewState d)
                   and (upper(a.locale) = upper(:locale) or upper(a.locale) = 'EN_US')
                  """)
])

@Entity
@Table(name = "GCRRVST")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain for Review State
 */

class AipReviewState implements Serializable {

    /**
     * Surrogate ID for GCRRVST.
     */
    @Id
    @Column(name = "GCRRVST_SURROGATE_ID")
    @SequenceGenerator(name = "GCRRVST_SEQ_GEN", allocationSize = 1, sequenceName = "GCRRVST_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRRVST_SEQ_GEN")
    Long id

    /**
     * Code for Review State.
     */
    @Column(name = "GCRRVST_RVST_CODE")
    String reviewStateCode

    /**
     * Name of the Review State.
     */
    @Column(name = "GCRRVST_RVST_NAME")
    String reviewStateName

    /**
     * Locale for Review State Name.
     */
    @Column(name = "GCRRVST_LOCALE")
    String locale

    /**
     * Review Ongoing Indicator indicates that review is in progress.
     */
    @Column(name = "GCRRVST_REVIEW_ONGOING_IND")
    String reviewOngoingInd

    /**
     * Review Success Indicator indicates if review is successful.
     */
    @Column(name = "GCRRVST_REVIEW_SUCCESS_IND")
    String reviewSuccessInd

    /**
     * User ID that created or modified the Review State.
     */
    @Column(name = "GCRRVST_USER_ID")
    String userId

    /**
     * Last activity date for the Review State.
     */
    @Column(name = "GCRRVST_ACTIVITY_DATE")
    Date activityDate

    /**
     * Version of the Review State.
     */
    @Version
    @Column(name = "GCRRVST_VERSION")
    Long version

    /**
     * Data Origin.
     */

    @Column(name = "GCRRVST_DATA_ORIGIN")
    String dataOrigin

    /**
     * VPDI Code for MEP.
     */
    @Column(name = "GCRRVST_VPDI_CODE")
    String vpdiCode

    static constraints = {
        id(nullable: false, maxSize: 19)
        reviewStateCode(nullable: false, maxSize: 10)
        reviewStateName(nullable: false, maxSize: 30)
        locale(nullable: false, maxSize: 20)
        reviewOngoingInd(nullable: false, maxSize: 1)
        reviewSuccessInd(nullable: false, maxSize: 1)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false)
        version(nullable: true, maxSize: 19)
        dataOrigin(nullable: true, maxSize: 30)
        vpdiCode(nullable: true, maxSize: 6)
    }

    /**
     * Fetch Review State for given code and locale
     * @param code Code of review state
     * @param locale User locale
     * @return List of Review States
     */
    static def fetchReviewStateByCodeAndLocale(String code, String locale) {
        AipReviewState.withSession { session ->
            session.getNamedQuery('AipReviewState.fetchReviewStateByCodeAndLocale')
                    .setString('code', code).setString('locale', locale)?.list()
        }
    }
    /**
     * Fetch list of review states which does not include default code
     * @param locale User Locale
     * @return List of review states
     */
    static def fetchNonDefaultReviewStates(String locale) {
            AipReviewState.withSession { session ->
                session.getNamedQuery('AipReviewState.fetchNonDefaultReviewStates')
                        .setString('locale', locale)?.list()
            }
        }

}
