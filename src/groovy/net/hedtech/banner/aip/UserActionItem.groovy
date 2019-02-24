/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
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
        @NamedQuery(name = "UserActionItem.fetchUserActionItemById",
                query = """
           FROM UserActionItem a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "UserActionItem.countUserActionItemByActionItemId",
                query = """select count(a.id)
           FROM UserActionItem a
           WHERE a.actionItemId = :myActionItemId
          """),
        @NamedQuery(name = "UserActionItem.fetchUserActionItemByPidm",
                query = """
           FROM UserActionItem a
           WHERE a.pidm = :myPidm
          """),
        @NamedQuery(name = "UserActionItem.countUserActionItemByPidm",
                query = """select count(a.pidm)
               FROM UserActionItem a
               WHERE a.pidm = :myPidm
              """),
        @NamedQuery(name = "UserActionItem.countUserActionItemByActionItemIdAndPidm",
                        query = """select count(a.id)
                       FROM UserActionItem a
                       WHERE a.actionItemId = :myActionItemId AND a.pidm = :myPidm
                      """),
        @NamedQuery(name = "UserActionItem.isExistingInDateRangeForPidmAndActionItemId",
                query = """ SELECT count(a.pidm)
                   FROM UserActionItem a
                   WHERE a.pidm = :myPidm
                   AND a.actionItemId = :myActionItemId
                   AND :myDisplayStartDate <= a.displayEndDate
                   AND a.displayStartDate <= :myDisplayEndDate
                  """)
])

@Entity
@Table(name = "GCRAACT")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain for User Action Item Details
 */

class UserActionItem implements Serializable {

    /**
     * Surrogate ID for GCRAACT
     */

    @Id
    @Column(name = "GCRAACT_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAACT_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAACT_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAACT_SEQ_GEN")
    Long id

    /**
     * Related ID of the action item
     */
    @Column(name = "GCRAACT_GCBACTM_ID")
    Long actionItemId

    /**
     * PIDM of the user action item belongs to
     */
    @Column(name = "GCRAACT_PIDM")
    Long pidm

    /**
     * Status of action item
     */
    @Column(name = "GCRAACT_GCVASTS_ID")
    Long status

    /**
     * user Response Date
     */
    @Column(name = "GCRAACT_USER_RESPONSE_DATE")
    Date userResponseDate

    /**
     * Display Start date
     */
    @Column(name = "gcraact_display_start_date")
    Date displayStartDate

    /**
     * Display End date
     */
    @Column(name = "gcraact_display_end_date")
    Date displayEndDate

    /**
     * Group Id associated with action Item group table
     */
    @Column(name = "gcraact_gcbagrp_id")
    Long groupId

    /**
     * Review status code associated with action Item review table
     */
    @Column(name = "GCRAACT_RVST_CODE")
    String reviewStateCode

    /**
     * User action item pertains to
     */
    @Column(name = "GCRAACT_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRAACT_ACTIVITY_DATE")
    Date lastModified

    /**
     * UserID that created the action item
     */
    @Column(name = "GCRAACT_CREATOR_ID")
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "GCRAACT_CREATE_DATE")
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCRAACT_VERSION")
    Long version

    /**
     * Data Origin column
     */
    @Column(name = "GCRAACT_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        actionItemId(nullable: false, maxSize: 19)
        pidm(nullable: false, maxSize: 8)
        status(nullable: false, maxSize: 30)
        lastModifiedBy(nullable: true, maxSize: 30)
        userResponseDate(nullable: true)
        displayStartDate(nullable: false)
        displayEndDate(nullable: false)
        groupId(nullable: false)
        reviewStateCode(nullable: true, maxSize: 10)
        lastModified(nullable: true)
        creatorId(nullable: true, maxSize: 30)
        createDate(nullable: true)
        dataOrigin(nullable: true, maxSize: 30)
    }

    /**
     *
     * @param id
     * @return
     */
    static def fetchUserActionItemById(Long id) {
        UserActionItem.withSession { session ->
            session.getNamedQuery('UserActionItem.fetchUserActionItemById').setLong('myId', id).list()[0]
        }
    }
    /**
     *
     * @param id
     * @return
     */
    static def countUserActionItemByActionItemId(Long actionItemId) {
        UserActionItem.withSession { session ->
            session.getNamedQuery('UserActionItem.countUserActionItemByActionItemId').setLong('myActionItemId', actionItemId).list()[0]
        }
    }
    /**
     *
     * @param pidm
     * @return
     */
    static def countUserActionItemByPidm(Long pidm) {
        UserActionItem.withSession { session ->
            session.getNamedQuery('UserActionItem.countUserActionItemByPidm').setLong('myPidm', pidm).list()[0]
        }
    }
    /**
     *
     * @param id
     * @param pidm
     * @return
     */
    static def countUserActionItemByActionItemIdAndPidm(Long actionItemId, Long pidm) {
        UserActionItem.withSession { session ->
            session.getNamedQuery('UserActionItem.countUserActionItemByActionItemIdAndPidm').setLong('myActionItemId', actionItemId)
                    .setLong('myPidm', pidm).list()[0]
        }
    }

    /**
     *
     * @param pidm
     * @return
     */
    static def fetchUserActionItemsByPidm(Long pidm) {
        UserActionItem.withSession { session ->
            session.getNamedQuery('UserActionItem.fetchUserActionItemByPidm').setLong('myPidm', pidm).list()
        }
    }

    /**
     *
     * @param UserActionItem
     * @return boolean Does an UserActionItem for this pidm and and ActionItem with overlapping display dates already exist
     */
    static boolean isExistingInDateRangeForPidmAndActionItemId(UserActionItem itemToTest) {
        UserActionItem.withSession { session ->
            session.getNamedQuery(
                    'UserActionItem.isExistingInDateRangeForPidmAndActionItemId').setLong('myPidm', itemToTest.pidm)
                    .setLong('myActionItemId', itemToTest.actionItemId)
                    .setDate('myDisplayStartDate', itemToTest.displayStartDate)
                    .setDate('myDisplayEndDate', itemToTest.displayEndDate)
                    .uniqueResult() > 0
        }
    }
}
