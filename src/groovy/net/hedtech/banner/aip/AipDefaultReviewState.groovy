/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*


@Entity
@Table(name = "GCRRVSD")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain for Default Review State
 */

class AipDefaultReviewState implements Serializable {

    /**
     * Surrogate ID for GCRRVSD.
     */
    @Id
    @Column(name = "GCRRVSD_SURROGATE_ID")
    @SequenceGenerator(name = "GCRRVSD_SEQ_GEN", allocationSize = 1, sequenceName = "GCRRVSD_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRRVSD_SEQ_GEN")
    Long id

    /**
     * Default Review State code.
     */
    @Column(name = "GCRRVSD_RVST_CODE")
    String defReviewStateCode

    /**
     * User ID that created or modified the Review State.
     */
    @Column(name = "GCRRVSD_USER_ID")
    String userId

    /**
     * Last activity date for the Review State.
     */
    @Column(name = "GCRRVSD_ACTIVITY_DATE")
    Date activityDate

    /**
     * Version of the Review State.
     */
    @Version
    @Column(name = "GCRRVSD_VERSION")
    Long version

    /**
     * Data Origin.
     */

    @Column(name = "GCRRVSD_DATA_ORIGIN")
    String dataOrigin

    /**
     * VPDI Code for MEP.
     */
    @Column(name = "GCRRVSD_VPDI_CODE")
    String vpdiCode

    static constraints = {
        id(nullable: false, maxSize: 19)
        defReviewStateCode(nullable: false, maxSize: 10)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false)
        version(nullable: true, maxSize: 19)
        dataOrigin(nullable: true, maxSize: 30)
        vpdiCode(nullable: true, maxSize: 6)
    }
}
