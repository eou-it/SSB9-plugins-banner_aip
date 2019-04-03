/*******************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.service.DatabaseModifiesState
import org.hibernate.annotations.Type

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Version


/**
 * Action Item Post Recurring Meta Data: Defines the parameters for an action item post recurring.
 */
@Entity
@Table(name = "GCBRAPT")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)


@DatabaseModifiesState
class ActionItemPostRecurringDetails implements Serializable {

    /**
     * SURROGATE ID: Generated unique numeric identifier for this entity.
     */
    @Id
    @Column(name = "GCBRAPT_SURROGATE_ID")
    @SequenceGenerator(name = "GCBRAPT_SEQ_GEN", allocationSize = 1, sequenceName = "GCBRAPT_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBRAPT_SEQ_GEN")
    Long id


    /**
     * FREQUENCY: Action item post recurring frequency.
     */
    @Column(name = "GCBRAPT_FREQUENCY")
    Long recurFrequency

    /**
     * FREQUENCY TYPE: Action Item post Recurring frequency type.
     */
    @Column(name = "GCBRAPT_FREQUENCY_TYPE")
    String recurFrequencyType

    /**
     * POSTING DISPLAY START DAYS: Action item post display start date offset.
     */
    @Column(name = "GCBRAPT_POST_START_DAYS")
    Long postingDispStartDays

    /**
     * POSTING DISPLAY END DAYS: Action item post display end date offset.
     */
    @Column(name = "GCBRAPT_POST_END_DAYS")
    long postingDispEndtDays

    /**
     * POSTING DISPLAY END DATE: Display End Date of Action Item Posting.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBRAPT_POST_END_DATE")
    Date postingDisplayEndDate

    /**
     * RECUR START DATE: Recurring start Date of Action Item Posting.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBRAPT_RECUR_START_DATE")
    Date recurStartDate

    /**
     * RECUR END DATE: Recurring end Date of Action Item Posting.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBRAPT_RECUR_END_DATE")
    Date recurEndDate

    /**
     * RECUR START TIME: The time the recurring job is scheduled to be processed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GCBRAPT_RECUR_START_TIME")
    Date recurStartTime

    /**
     * RECUR POST TIMEZONE: The timezone of recurring post.
     */
    @Column(name = "GCBRAPT_RECUR_POST_TIMEZONE")
    String recurPostTimezone

    /**
     * ACTIVITY DATE: Date that record was created or last updated.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GCBRAPT_ACTIVITY_DATE")
    Date lastModified

    /**
     * USER ID: The user ID of the person who inserted or last updated this record.
     */
    @Column(name = "GCBRAPT_USER_ID")
    String lastModifiedBy

    /**
     * DATA ORIGIN: Source system that created or updated the data.
     */
    @Column(name = "GCBRAPT_DATA_ORIGIN")
    String dataOrigin

    /**
     * VPDI CODE: Multi-entity processing code.
     */
    @Column(name = "GCBRAPT_VPDI_CODE")
    String vpdiCode

    /**
     * VERSION: Optimistic lock token.
     */
    @Version
    @Column(name = "GCBRAPT_VERSION")
    Long version


    static constraints = {

        recurFrequency( nullable: false, maxSize: 2 )
        recurFrequencyType( nullable: false )
        postingDispStartDays( nullable: false, maxSize: 2 )
        postingDispEndtDays( nullable: true, maxSize: 2 )
        postingDisplayEndDate( nullable: true )
        recurStartDate( nullable: false )
        recurEndDate( nullable: false )
        recurStartTime( nullable: false )
        recurPostTimezone( nullable: false)
        lastModified( nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 30 )
        vpdiCode( nullable: true, maxSize: 6 )
        version( nullable: true, maxSize: 19 )

    }

}
