/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.aip.post.ActionItemErrorCode
import org.hibernate.annotations.Type

import javax.persistence.*

/**
 * Action Item Post: Defines the parameters for an action item post.
 */
@Entity
@Table(name = "GCBAPST")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
@NamedQueries(value = [
        @NamedQuery(name = "ActionItemPost.findRunning",
                query = """ FROM ActionItemPost gs
                    WHERE gs.postingCurrentState = :new_ or
                          gs.postingCurrentState = :processing_ or
                          gs.postingCurrentState = :scheduled_ or
                          gs.postingCurrentState = :queued_ or
                          gs.postingCurrentState = :calculating_"""
        ),
        @NamedQuery(name = "ActionItemPost.fetchCompleted",
                query = """ FROM ActionItemPost gs
                WHERE gs.postingCurrentState = :complete_ """
        ),
        @NamedQuery(name = "ActionItemPost.checkIfJobNameAlreadyExists",
                query = """ select count (gs.postingName) FROM ActionItemPost gs
                        WHERE upper(gs.postingName) = upper( :postingName) """
        )
])
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
    @Type(type = "yes_no")
    boolean postingDeleteIndicator = false

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
     * POSTING SCHEDULE DATE TIME: Date time of action item posting schedule.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GCBAPST_SCHEDULED_DATETIME")
    Date postingScheduleDateTime

    /**
     * POSTING CREATOR ID: The ID of user who created the Action item posting.
     */
    @Column(name = "GCBAPST_CREATOR_ID")
    String postingCreatorId

    /**
     * POSTING SCHEDULE DATE TIME: The date and time the posting job is scheduled to be processed.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GCBAPST_CREATION_DATETIME")
    Date postingCreationDateTime

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
    @Enumerated(EnumType.STRING)
    ActionItemPostExecutionState postingCurrentState = ActionItemPostExecutionState.New

    @Column(name = "GCBAPST_STARTED_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    Date postingStartedDate

    @Column(name = "GCBAPST_STOP_DATE", nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    Date postingStopDate

    /**
     * POSTING JOB ID: The job ID of the scheduled job.
     */
    @Column(name = "GCBAPST_JOB_ID")
    String postingJobId

    /**
     * POPULATION CALCULATION ID: The id of the specific population calculation resolved to feed the Action Item post.
     */
    @Column(name = "GCBAPST_POPCALC_ID")
    Long populationCalculationId

    @Column(name = "GCBAPST_POPVERSION_ID")
    Long populationVersionId

    /**
     * POSTING ERROR CODE: The Error code of Action Item post.
     */

    @Column(name = "GCBAPST_ERROR_CODE")
    @Enumerated(EnumType.STRING)
    ActionItemErrorCode postingErrorCode

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
    @Temporal(TemporalType.TIMESTAMP)
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
        version( nullable: true, maxSize: 19 )
        populationListId( nullable: false, maxSize: 19 )
        postingActionItemGroupId( nullable: false, maxSize: 19 )
        postingName( nullable: false, maxSize: 2048 )
        postingDeleteIndicator( nullable: false, maxSize: 1 )
        postingDisplayStartDate( nullable: false )
        postingDisplayEndDate( nullable: false )
        postingScheduleDateTime( nullable: true )
        postingCreatorId( nullable: false, maxSize: 30 )
        populationRegenerateIndicator( nullable: false, maxSize: 1 )
        postingCurrentState( nullable: true, maxSize: 255 )
        postingJobId( nullable: true, maxSize: 64 )
        populationCalculationId( nullable: true, maxSize: 19 )
        populationVersionId( nullable: true, maxSize: 19 )
        postingErrorCode( nullable: true, maxSize: 256 )
        postingErrorText( nullable: true )
        postingGroupId( nullable: true, maxSize: 256 )
        postingParameterValues( nullable: true )
        lastModified( nullable: true )
        lastModifiedBy( nullable: true, maxSize: 30 )
        dataOrigin( nullable: true, maxSize: 30 )
        vpdiCode( nullable: true, maxSize: 6 )
    }


    void markScheduled( String jobId, String groupId ) {
        assert jobId != null
        assert groupId != null
        assignPostExecutionState( ActionItemPostExecutionState.Scheduled, jobId, groupId )
    }


    void markQueued( String jobId, String groupId ) {
        assert jobId != null
        assert groupId != null
        assignPostExecutionState( ActionItemPostExecutionState.Queued, jobId, groupId )
    }


    void markStopped( Date stopDate = new Date() ) {
        assignPostExecutionState( ActionItemPostExecutionState.Stopped )
        //this.postingStopDate = stopDate
    }


    void markComplete( Date stopDate = new Date() ) {
        assignPostExecutionState( ActionItemPostExecutionState.Complete )
        //this.postingStopDate = stopDate
    }


    void markProcessing() {
        assignPostExecutionState( ActionItemPostExecutionState.Processing )
        if (this.postingStartedDate == null) {
            this.postingStartedDate = new Date()
        }
    }


    void markError( ActionItemErrorCode errorCode, String errorText ) {
        assignPostExecutionState( ActionItemPostExecutionState.Error )
        this.postingErrorCode = errorCode
        this.postingErrorText = errorText
        //this.postingStopDate = postingStopDate
    }


    private void assignPostExecutionState( ActionItemPostExecutionState executionState, String jobId = null, String groupId = null ) {
        this.postingCurrentState = executionState
        this.postingJobId = jobId
        this.postingGroupId = groupId
    }


    static List findRunning( Integer max = Integer.MAX_VALUE ) {
        def query
        ActionItemPost.withSession {session ->
            query = session.getNamedQuery( 'ActionItemPost.findRunning' )
                    .setParameter( 'new_', ActionItemPostExecutionState.New )
                    .setParameter( 'processing_', ActionItemPostExecutionState.Processing )
                    .setParameter( 'scheduled_', ActionItemPostExecutionState.Scheduled )
                    .setParameter( 'queued_', ActionItemPostExecutionState.Queued )
                    .setParameter( 'calculating_', ActionItemPostExecutionState.Calculating )
                    .setFirstResult( 0 )
                    .setMaxResults( max )
                    .list()
        }
        return query
    }


    static List fetchCompleted() {
        def results
        ActionItemPostWork.withSession {session ->
            results = session.getNamedQuery( 'ActionItemPost.fetchCompleted' )
                    .setParameter( 'complete_', ActionItemPostExecutionState.Complete )
                    .list()
        }
        return results
    }


    static int findCountByPopulationCalculationId( Long populationCalculationId ) {
        return ActionItemPost.createCriteria().list {
            projections {
                count()
            }
            eq( 'populationCalculationId', populationCalculationId )
        }[0]
    }

    /**
     * Checks if posting name is already present
     * @param name
     * @return
     */
    static def checkIfJobNameAlreadyExists( name ) {
        def count
        ActionItemPostWork.withSession {session ->
            count = session.getNamedQuery( 'ActionItemPost.checkIfJobNameAlreadyExists' )
                    .setParameter( 'postingName', name )
                    .uniqueResult()
        }
        count > 0
    }

    /*
        public static findByNameWithPagingAndSortParams(filterData, pagingAndSortParams) {

            def descdir = pagingAndSortParams?.sortDirection?.toLowerCase() == 'desc'

            def queryCriteria = ActionItemPost.createCriteria()
            def results = queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
                ilike("name", ActionItemCommonUtility.getScrubbedInput(filterData?.params?.groupSendName))
                ilike("createdBy", filterData?.params?.createdBy)
                order((descdir ? Order.desc(pagingAndSortParams?.sortColumn) : Order.asc(pagingAndSortParams?.sortColumn)).ignoreCase())
            }
            return results
        }
    */
}
