/*******************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type
import net.hedtech.banner.general.CommunicationCommonUtility
import org.hibernate.criterion.Order

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Version

/**
 * Domain for ActionItemPostReadOnly
 */
@Entity
@Table(name = "GVQ_GCBAPST")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
@NamedQueries(value = [
        @NamedQuery(name = "ActionItemPostReadOnly.fetchJobs",
                query = """FROM ActionItemPostReadOnly a WHERE upper(a.postingName) like :postingName order by a.lastModified desc
          """),
        @NamedQuery(name = "ActionItemPostReadOnly.fetchJobsCount",
                query = """SELECT COUNT(a.postingId) FROM ActionItemPostReadOnly a
                           WHERE upper(a.postingName) like :postingName
                           AND (a.recurringPostIndicator = true OR a.recurringPostDetailsId is null)
            """
        ),
        @NamedQuery(name = "ActionItemPostReadOnly.fetchByPostingId",
                query = """FROM ActionItemPostReadOnly a
                           WHERE a.postingId = :postingId 
            """
        ),
        @NamedQuery(name = "ActionItemPostReadOnly.fetchRecurringJobsCount",
                query = """SELECT COUNT(a.postingId) FROM ActionItemPostReadOnly a
                           WHERE a.recurringPostDetailsId = :recurringPostDetailsId
                           AND a.recurringPostIndicator = false
            """
        ),
        @NamedQuery(name = "ActionItemPostReadOnly.fetchRecurringJobsStateCount",
                query = """SELECT COUNT(a.postingId) FROM ActionItemPostReadOnly a
                           WHERE a.recurringPostDetailsId = :recurringPostDetailsId
                           AND a.postingCurrentState = :postingCurrentState
            """
        )

])
class ActionItemPostReadOnly implements Serializable {

    /**
     * ACTION ITEM POST ID: Action Item post ID.
     */
    @Id
    @Column(name = "GCBAPST_SURROGATE_ID")
    Long postingId

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
     * POSTING SCHEDULE DATE TIME: Delete time of action item posting schedule.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "GCBAPST_SCHEDULED_DATETIME")
    Date postingScheduleDateTime

    @Column(name = "GCBAPST_STARTED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date postingStartedDate

    /**
     * POSTING CREATOR ID: The ID of user who created the Action item posting.
     */
    @Column(name = "GCBAPST_CREATOR_ID")
    String postingCreatorId

    /**
     * POSTING CREATION DATE TIME: The date and time the posting job is scheduled to be processed.
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
    String postingCurrentState

    /**
     * POSTING JOB ID: The job ID of the scheduled job.
     */
    @Column(name = "GCBAPST_JOB_ID")
    String aSyncJobId

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
     * POSTING GROUP ID: The group ID of post.
     */
    @Column(name = "GCBAPST_GROUP_ID")
    String aSyncGroupId

    /**
     * POSTING PARAMETER VALUES: The map of parameter name and values for a specific Action Item post.
     */
    @Column(name = "GCBAPST_PARAMETER_VALUES")
    String postingParameterValues

    /**
     * GROUP FOLDER ID: folder id selected for the Action Item Group.
     */
    @Column(name = "ACTION_GROUP_GCRFLDR_ID")
    Long groupFolderId

    /**
     * GROUP FOLDER NAME: folder Name selected for the Action Item Group.
     */
    @Column(name = "ACTION_GROUP_GCRFLDR_NAME")
    String groupFolderName

    /**
     * GROUP NAME: Name for the action Item Group for Group management control.
     */
    @Column(name = "ACTION_GROUP_NAME")
    String groupName

    /**
     * GROUP TITLE: Title for the action Item Group. The title displays for the user assigned the Group.
     */
    @Column(name = "ACTION_GROUP_TITLE")
    String groupTitle

    /**
     * GROUP STATUS: Status of the Group. Possible values (D)raft, (A)ctive and (I)nActive.
     */
    @Column(name = "ACTION_GROUP_STATUS_CODE")
    String groupStatus

    @Column(name = "ACTION_GROUP_POSTED_IND")
    String actionGroupPostedInd

    /**
     * Population Name
     */
    @Column(name = "POSTING_POPULATION")
    String postingPopulation

    /**
     * Activity Date
     */
    @Column(name = "GCBAPST_ACTIVITY_DATE")
    Date lastModified

    /**
     * Last modified by
     */
    @Column(name = "GCBAPST_USER_ID")
    String lastModifiedBy

    /**
     * Version
     */
    @Version
    @Column(name = "GCBAPST_VERSION")
    Long version

    /**
     * Date: Store user entered Date Time .
     */

    @Column(name = "GCBAPST_DISPLAY_DATE")
    Date postingDisplayDateTime

    /**
     * postingTimeZone:Store user entered TimeZone
     */
    @Column(name = "GCBAPST_TIME_ZONE")
    String postingTimeZone

    /**
     * Recurring action item post indicator .
     */
    @Type(type = "yes_no")
    @Column(name = "gcbapst_recur_post_ind")
    boolean recurringPostIndicator

    /**
     * Recurring action item meta data Id
     */
    @Column(name = "gcbapst_gcbrapt_id")
    Long recurringPostDetailsId
    /**
     *
     * @param params
     * @param paginationParams
     */
    def static fetchJobs( params, paginationParams ) {
        ActionItemPostReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemPostReadOnly.fetchJobs' )
                    .setString( 'postingName', params.searchParam )
                    .setFirstResult( paginationParams.offset )
                    .setMaxResults( paginationParams.max )
                    .list()
        }
    }

    /**
     *
     * @return
     */
    def static fetchJobsCount( params ) {
        ActionItemPostReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemPostReadOnly.fetchJobsCount' )
                    .setString( 'postingName', params.searchParam )
                    .uniqueResult()
        }
    }

    def static fetchByPostingId( postingId ) {
        ActionItemPostReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemPostReadOnly.fetchByPostingId' )
                    .setLong( 'postingId', postingId ).list()[0]
        }
    }
    /**
     * Function to fetch post action items based on params
     * @param filterData
     * @param pagingAndSortParams
     * @return
     */
    static fetchWithPagingAndSortParams(filterData, pagingAndSortParams) {
        def queryCriteria = ActionItemPostReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            or {
                and {
                    isNull("recurringPostDetailsId")
                }
                and {
                    eq('recurringPostIndicator', true)
                }
            }

            ilike("postingName", CommunicationCommonUtility.getScrubbedInput(filterData?.name))
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
            if (!pagingAndSortParams?.sortColumn.equals("postingName")) {
                order(Order.asc('postingName').ignoreCase())
            }
        }
    }


    /**
     * Function to fetch recurring post action items based on params
     * @param filterData
     * @param pagingAndSortParams
     * @return
     */
    static fetchRecurringPostActionItemsWithPagingAndSortParams(filterData, pagingAndSortParams) {
        def queryCriteria = ActionItemPostReadOnly.createCriteria()
        queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {
            and {
                eq("recurringPostDetailsId",filterData?.recurringPostDetailsId)
                eq("recurringPostIndicator",false)
            }
            ilike("postingName", CommunicationCommonUtility.getScrubbedInput(filterData?.name))
            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
            if (!pagingAndSortParams?.sortColumn.equals("postingName")) {
                order(Order.asc('postingName').ignoreCase())
            }
        }
    }

    /**
     * Function to fetch post recurring action items count based on params
     * @param params
     * @return
     */
    def static fetchRecurringJobsCount( params ) {
        def actionItemPostReadOnly = ActionItemPostReadOnly.fetchByPostingId(Long.parseLong(params?.recurringPostId))
        ActionItemPostReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemPostReadOnly.fetchRecurringJobsCount' )
                    .setLong( 'recurringPostDetailsId', actionItemPostReadOnly.recurringPostDetailsId)
                    .uniqueResult()
        }
    }

    /**
     * Function to fetch post reucrring action items jobs state count
     * @param recurringPostId
     * @param jobState
     * @return
     */
    def static fetchRecurringJobsStateCount( recurringPostId ,jobState) {
        ActionItemPostReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemPostReadOnly.fetchRecurringJobsStateCount' )
                    .setLong( 'recurringPostDetailsId', recurringPostId)
                    .setString( 'postingCurrentState', jobState)
                    .uniqueResult()
        }
    }


}
