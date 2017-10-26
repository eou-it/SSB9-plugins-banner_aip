/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

/**
 * Read only view for summary of action item posting jobs.
 */
@Entity
@Table(name = "GVQ_GCRAPST")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class ActionItemPostDetailReadOnly implements Serializable {

    /**
     * SURROGATE ID: Generated unique numeric identifier for this entity.
     */
    @Id
    @Column(name = "GCRAPST_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAPST_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAPST_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAPST_SEQ_GEN")
    Long id

    /**
     * ACTION ITEM POST ID: Action Item post ID.
     */
    @Column(name = "GCRAPST_GCBAPST_ID")
    Long actionItemPostId

    /**
     * ACTION ITEM ID: Action Item ID of the action item to post.
     */
    @Column(name = "GCRAPST_GCBACTM_ID")
    Long actionItemId

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
    String postingJobId

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
     * ACTION ITEM FOLDER ID: Foreign key reference to the folder under which this action item is organized.
     */
    @Column(name = "ACTION_ITEM_GCRFLDR_ID")
    Long actionItemFolderId

    /**
     * ACTION ITEM NAME: Name of the Action Item for Action Item management control.
     */
    @Column(name = "ACTION_ITEM_NAME")
    String actionItemName

    /**
     * ACTION ITEM TITLE: Title of the Action Item. This displays for the user assigned the Action Item.
     */
    @Column(name = "ACTION_ITEM_TITLE")
    String actionItemTitle

    /**
     * ACTION ITEM STATUS: Status of the Action Item. Valid values are (D)raft, (A)ctive and (I)nactive.
     */
    @Column(name = "ACTION_ITEM_STATUS_CODE")
    String actionItemStatus

    /**
     * ACTION ITEM POSTING INDICATOR: Posting status of Action Item. Valid values are (Y)es and (N)o. Default is (N)o.
     */
    @Type(type = "yes_no")
    @Column(name = "ACTION_ITEM_POSTED_IND")
    Boolean actionItemPostingIndicator

    /**
     * CREATOR: The Oracle user name that first created this record.
     */
    @Column(name = "ACTION_ITEM_CREATOR_ID")
    String creator

    /**
     * CREATE DATE: The date when this Action Item record was created.
     */
    @Temporal(TemporalType.DATE)
    @Column(name = "ACTION_ITEM_CREATE_DATE")
    Date createDate

    /**
     * GROUP FOLDER ID: folder id selected for the Action Item Group.
     */
    @Column(name = "ACTION_GROUP_GCRFLDR_ID")
    Long groupFolderId

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

    /**
     * GROUP POSTED INDICATOR: Posting Status of Group. Possible values (N)o and (Y)es.
     */
    @Type(type = "yes_no")
    @Column(name = "ACTION_GROUP_POSTED_IND")
    Boolean groupPostedIndicator
}


