/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

/**
 * Action Item Post: Defines the parameters for an action item post.
 */
@Entity
@Table(name = "GCBAPST")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemPost implements Serializable {

    /**
     * SURROGATE ID: Generated unique numeric identifier for this entity.
     */
    @Id
    @Column(name = "GCBAPST_SURROGATE_ID")
    @SequenceGenerator(name = "GCBAPST_SEQ_GEN", allocationSize = 1, sequenceName = "GCBAPST_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBAPST_SEQ_GEN")
    Long id

    /**
     * VERSION: Optimistic lock token.
     */
    @Version
    @Column(name = "GCBAPST_VERSION")
    Long version

    /**
     * POPULATION LIST ID: Population List identification number.
     */
    @Column(name = "GCBAPST_POPLIST_ID")
    Long populationListId

    /**
     * POSTING ACTION ITEM GROUP ID: The Action Item Group ID selected for the Action Item Posting job.
     */
    @Column(name = "GCBAPST_GCBAGRP_ID")
    Long postingActionItemGroupId

    /**
     * POSTING NAME: Name describing the Action Item Posting.
     */
    @Column(name = "GCBAPST_NAME")
    String postingName

    /**
     * POSTING DELETE INDICATOR: Indicator to denote whether the process was deleted.
     */
    @Column(name = "GCBAPST_DELETED")
    String postingDeleteIndicator

    /**
     * POSTING SCHEDULE TYPE: Posting Schedule type of Action Item Posting.
     */
    @Column(name = "GCBAPST_SCHEDULE_TYPE")
    String postingScheduleType

    /**
     * POSTING DISPLAY START DATE: Display Start Date of Action Item Posting.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBAPST_DISPLAY_START_DATE")
    Date postingDisplayStartDate

    /**
     * POSTING DISPLAY END DATE: Display End Date of Action Item Posting.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBAPST_DISPLAY_END_DATE")
    Date postingDisplayEndDate

    /**
     * POSTING SCHEDULE DELETE TIME: Delete time of action item posting schedule.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBAPST_SCHEDULED_DATETIME")
    Date postingScheduleDeleteTime

    /**
     * POSTING CREATOR ID: The ID of user who created the Action item posting.
     */
    @Column(name = "GCBAPST_CREATOR_ID")
    String postingCreatorId

    /**
     * POSTING SCHEDULE DATE TIME: The date and time the posting job is scheduled to be processed.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBAPST_CREATION_DATETIME")
    Date postingScheduleDateTime

    /**
     * POPULATION REGENERATE INDICATOR: Indicates if the chosen population needs to be recalculated before posting. Values are (Y)es and (N)o
     */
    @Type(type = "yes_no")
    @Column(name = "GCBAPST_RECALC_ON_POST")
    Boolean populationRegenerateIndicator

    /**
     * POSTING CURRENT STATE: The current state of Action Item post.
     */
    @Column(name = "GCBAPST_CURRENT_STATE")
    String postingCurrentState

    /**
     * POSTING JOB ID: The job ID of the scheduled job.
     */
    @Column(name = "GCBAPST_JOB_ID")
    Long postingJobId

    /**
     * POPULATION CALCULATION ID: The id of the specific population calculation resolved to feed the Action Item post.
     */
    @Column(name = "GCBAPST_POPCALC_ID")
    Long populationCalculationId

    /**
     * POSTING ERROR CODE: The Error code of Action Item post.
     */
    @Column(name = "GCBAPST_ERROR_CODE")
    String postingErrorCode

    /**
     * POSTING ERROR TEXT: The Error text of Action Item post.
     */
    @Column(name = "GCBAPST_ERROR_TEXT")
    String postingErrorText

    /**
     * POSTING GROUP ID: The group ID of Action Item post.
     */
    @Column(name = "GCBAPST_GROUP_ID")
    String postingGroupId

    /**
     * POSTING PARAMETER VALUES: The map of parameter name and values for a specific Action Item post.
     */
    @Column(name = "GCBAPST_PARAMETER_VALUES")
    String postingParameterValues

    /**
     * ACTIVITY DATE: Date that record was created or last updated.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "GCBAPST_ACTIVITY_DATE")
    Date lastModified

    /**
     * USER ID: The user ID of the person who inserted or last updated this record.
     */
    @Column(name = "GCBAPST_USER_ID")
    String lastModifiedBy

    /**
     * DATA ORIGIN: Source system that created or updated the data.
     */
    @Column(name = "GCBAPST_DATA_ORIGIN")
    String dataOrigin

    /**
     * VPDI CODE: Multi-entity processing code.
     */
    @Column(name = "GCBAPST_VPDI_CODE")
    String vpdiCode


    static constraints = {
        id( nullable: false, maxSize: 19 )
        version( nullable: true, maxSize: 19 )
        populationListId( nullable: false, maxSize: 19 )
        postingActionItemGroupId( nullable: false, maxSize: 19 )
        postingName( nullable: false, maxSize: 2048 )
        postingDeleteIndicator( nullable: false, maxSize: 1 )
        postingScheduleType( nullable: false, maxSize: 30 )
        postingDisplayStartDate( nullable: false )
        postingDisplayEndDate( nullable: false )
        postingScheduleDeleteTime( nullable: false )
        postingCreatorId( nullable: false, maxSize: 30 )
        postingScheduleDateTime( nullable: false )
        populationRegenerateIndicator( nullable: false, maxSize: 1 )
        postingCurrentState( nullable: true, maxSize: 255 )
        postingJobId( nullable: true, maxSize: 19 )
        populationCalculationId( nullable: true, maxSize: 19 )
        postingErrorCode( nullable: true, maxSize: 256 )
        postingErrorText( nullable: true )
        postingGroupId( nullable: true, maxSize: 256 )
        postingParameterValues( nullable: true )
        lastModified( nullable: false )
        lastModifiedBy( nullable: false, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 30 )
        vpdiCode( nullable: true, maxSize: 6 )
    }
}
