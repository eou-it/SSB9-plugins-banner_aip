/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.general.CommunicationCommonUtility
import org.hibernate.FlushMode
import org.hibernate.criterion.Order

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemStatus.fetchActionItemStatuses",
                query = """
           FROM ActionItemStatus a
          """),
        @NamedQuery(name = "ActionItemStatus.fetchActionItemStatusById",
                query = """
           FROM ActionItemStatus a
           WHERE a.id = :myId
          """),
        @NamedQuery(name="ActionItemStatus.fetchActionItemStatusCount",
                query = """SELECT COUNT(a.id) FROM ActionItemStatus a
            """
        )
])

@Entity
@Table(name = "GCVASTS")

class ActionItemStatus implements Serializable {

    /**
     * Surrogate ID for GCVASTS
     */

    @Id
    @Column(name = "GCVASTS_SURROGATE_ID")
    @SequenceGenerator(name = "GCVASTS_SEQ_GEN", allocationSize = 1, sequenceName = "GCVASTS_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCVASTS_SEQ_GEN")
    Long actionItemStatusId

    /**
     * Name of the action item status
     */
    @Column(name = "GCVASTS_ACTION_ITEM_STATUS")
    String actionItemStatus

    /**
     * Indicator for a blocked process
     */
    @Column(name = "GCVASTS_BLOCK_PROCESS_IND")
    String actionItemStatusBlockedProcess

    /***
     * Indicator for system required
     */

    @Column(name = "GCVASTS_SYSTEM_REQUIRED")
    String actionItemStatusSystemRequired


    /***
     * Indicator for active record
     */

    @Column(name = "GCVASTS_ACTIVE_IND")
    String  actionItemStatusActive


    /**
     * User action item status was last updated by
     */
    @Column(name = "GCVASTS_USER_ID")
    String actionItemStatusUserId

    /**
     * Last activity date for the action item sttus
     */
    @Column(name = "GCVASTS_ACTIVITY_DATE")
    Date actionItemStatusActivityDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCVASTS_VERSION")
    Long actionItemStatusVersion

    /**
     * Data Origin column for GCVASTS
     */
    @Column(name = "GCVASTS_DATA_ORIGIN")
    String actionItemStatusDataOrigin

    static constraints = {
        actionItemStatus(blank: false, nullable: false, maxSize: 30, unique:true)
        actionItemStatusActive(blank: false, nullable: false, maxSize: 1)
        actionItemStatusActivityDate(blank: false, nullable: false, maxSize: 30)
        actionItemStatusBlockedProcess(blank: false, nullable: false, maxSize: 1)
        actionItemStatusSystemRequired(blank: false, nullable: false, maxSize: 1) //summary length only for now
        actionItemStatusUserId(blank: false, nullable: false, maxSize: 30)
        actionItemStatusVersion(nullable: true, maxSize: 30)
        actionItemStatusDataOrigin(nullable: true, maxSize: 30)
    }


    @Override
    public String toString() {
        return "ActionItemStatus{" +
                "actionItemStatusId=" + actionItemStatusId +
                ", actionItemStatus='" + actionItemStatus + '\'' +
                ", actionItemStatusBlockedProcess='" + actionItemStatusBlockedProcess + '\'' +
                ", actionItemStatusSystemRequired='" + actionItemStatusSystemRequired + '\'' +
                ", actionItemStatusActive='" + actionItemStatusActive + '\'' +
                ", actionItemStatusUserId='" + actionItemStatusUserId + '\'' +
                ", actionItemStatusActivityDate=" + actionItemStatusActivityDate +
                ", actionItemStatusVersion=" + actionItemStatusVersion +
                ", actionItemStatusDataOrigin='" + actionItemStatusDataOrigin + '\'' +
                '}';
    }



    boolean equals( o ) {
        if (this.is( o )) return true
        if (getClass() != o.class) return false

        ActionItemStatus that = (ActionItemStatus) o

        if (actionItemStatus != that.actionItemStatus) return false
        if (actionItemStatusActive != that.actionItemStatusActive) return false
        if (actionItemStatusActivityDate != that.actionItemStatusActivityDate) return false
        if (actionItemStatusBlockedProcess != that.actionItemStatusBlockedProcess) return false
        if (actionItemStatusDataOrigin != that.actionItemStatusDataOrigin) return false
        if (actionItemStatusSystemRequired != that.actionItemStatusSystemRequired) return false
        if (actionItemStatusUserId != that.actionItemStatusUserId) return false
        if (actionItemStatusVersion != that.actionItemStatusVersion) return false
        if (actionItemStatusId != that.actionItemStatusId) return false

        return true
    }


    int hashCode() {
        int result
        result = (actionItemStatusId != null ? actionItemStatusId.hashCode() : 0)
        result = 31 * result + (actionItemStatus != null ? actionItemStatus.hashCode() : 0)
        result = 31 * result + (actionItemStatusBlockedProcess != null ? actionItemStatusBlockedProcess.hashCode() : 0)
        result = 31 * result + (actionItemStatusSystemRequired != null ? actionItemStatusSystemRequired.hashCode() : 0)
        result = 31 * result + (actionItemStatusActive != null ? actionItemStatusActive.hashCode() : 0)
        result = 31 * result + (actionItemStatusUserId != null ? actionItemStatusUserId.hashCode() : 0)
        result = 31 * result + (actionItemStatusActivityDate != null ? actionItemStatusActivityDate.hashCode() : 0)
        result = 31 * result + (actionItemStatusVersion != null ? actionItemStatusVersion.hashCode() : 0)
        result = 31 * result + (actionItemStatusDataOrigin != null ? actionItemStatusDataOrigin.hashCode() : 0)
        return result
    }


    public static def fetchActionItemStatuses( ) {
        ActionItemStatus.withSession { session ->
            List<ActionItemStatus> actionItemStatus = session.getNamedQuery('ActionItemStatus.fetchActionItemStatuses').list()
            return actionItemStatus
        }
    }

    public static def fetchActionItemStatusById( Long myId) {
        ActionItemStatus.withSession { session ->
            ActionItemStatus actionItemStatus = session.getNamedQuery( 'ActionItemStatus.fetchActionItemStatusById' ).setLong('myId', myId)?.list()[0]
            return actionItemStatus
        }
    }


    public static def fetchActionItemStatusCount() {
        ActionItemStatus.withSession { session ->
            List actionItemStatuses = session.getNamedQuery( 'ActionItemStatus.fetchActionItemStatusCount' ).list()
            return actionItemStatuses
        }
    }


    public static fetchWithPagingAndSortParams(filterData, pagingAndSortParams) {

        def searchStatus = filterData?.params?.status
        def queryCriteria = ActionItemStatus.createCriteria()
        def results = queryCriteria.list(max: pagingAndSortParams.max, offset: pagingAndSortParams.offset) {

            ilike("actionItemStatus", CommunicationCommonUtility.getScrubbedInput(filterData?.params?.name))

            order((pagingAndSortParams.sortAscending ? Order.asc(pagingAndSortParams?.sortColumn) : Order.desc(pagingAndSortParams?.sortColumn)).ignoreCase())
        }

        return results
    }



}
