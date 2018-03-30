/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.general.CommunicationCommonUtility
import org.hibernate.criterion.Order
import org.springframework.web.context.request.RequestContextHolder

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemStatus.fetchActionItemStatuses",
                query = """
           FROM ActionItemStatus a order by a.actionItemStatus
          """),
        @NamedQuery(name = "ActionItemStatus.fetchDefaultActionItemStatus",
                query = """
                   FROM ActionItemStatus a
                   WHERE a.actionItemStatusDefault = :myY  AND (:mepCode is null or a.mepCode = :mepCode)
                  """),
        @NamedQuery(name = "ActionItemStatus.checkIfNameAlreadyPresent",
                query = """ SELECT COUNT(actionItemStatus)
                           FROM ActionItemStatus a
                           WHERE UPPER(a.actionItemStatus) = UPPER( :actionItemStatus)
                          """),
        @NamedQuery(name = "ActionItemStatus.fetchActionItemStatusById",
                query = """
           FROM ActionItemStatus a
           WHERE a.id = :myId
          """),
        @NamedQuery(name = "ActionItemStatus.fetchActionItemStatusCount",
                query = """SELECT COUNT(a.id) FROM ActionItemStatus a where upper(a.actionItemStatus) like upper(:actionItemStatus)
            """
        )
])

@Entity
@Table(name = "GCVASTS")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Action Status Validation
 */
class ActionItemStatus implements Serializable {

    /**
     * Surrogate ID for GCVASTS
     */

    @Id
    @Column(name = "GCVASTS_SURROGATE_ID")
    @SequenceGenerator(name = "GCVASTS_SEQ_GEN", allocationSize = 1, sequenceName = "GCVASTS_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCVASTS_SEQ_GEN")
    Long id

    /**
     * Name of the action item status
     */
    @Column(name = "GCVASTS_STATUS_RULE_NAME")
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
    String actionItemStatusActive

    @Column(name = "GCVASTS_DEFAULT_IND")
    String actionItemStatusDefault

    /**
     * User action item status was last updated by
     */
    @Column(name = "GCVASTS_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the action item sttus
     */
    @Column(name = "GCVASTS_ACTIVITY_DATE")
    Date lastModified

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCVASTS_VERSION")
    Long version

    /**
     * VPDI Code
     */
    @Column(name = "GCVASTS_VPDI_CODE")
    String mepCode

    /**
     * Data Origin column for GCVASTS
     */
    @Column(name = "GCVASTS_DATA_ORIGIN")
    String dataOrigin

    static constraints = {
        actionItemStatus( blank: false, nullable: false, maxSize: 30 ) //unique: true produces stacktrace during create
        actionItemStatusActive( blank: false, nullable: false, maxSize: 1 )
        lastModified( nullable: true )
        actionItemStatusBlockedProcess( blank: false, nullable: false, maxSize: 1 )
        actionItemStatusDefault( blank: true, nullable: true, maxSize: 1 )
        actionItemStatusSystemRequired( blank: false, nullable: false, maxSize: 1 )
        lastModifiedBy( blank: false, nullable: true, maxSize: 30 )
        version( nullable: true )
        mepCode( nullable: true )
        dataOrigin( nullable: true, maxSize: 30 )
    }

    /**
     *
     * @return
     */
    static def fetchActionItemStatuses() {
        ActionItemStatus.withSession {session ->
            List<ActionItemStatus> actionItemStatus = session.getNamedQuery( 'ActionItemStatus.fetchActionItemStatuses' ).list()
            return actionItemStatus
        }
    }

    /**
     *
     * @param mepCode
     * @return
     */
    static ActionItemStatus fetchDefaultActionItemStatus( mepCode = null ) {
        ActionItemStatus.withSession {session ->
            session.getNamedQuery( 'ActionItemStatus.fetchDefaultActionItemStatus' )
                    .setString( 'myY', 'Y' )
                    .setString( 'mepCode', mepCode )
                    .uniqueResult()
        }
    }
    /**
     *
     * @param myId
     * @return
     */
    static def fetchActionItemStatusById( Long myId ) {
        ActionItemStatus.withSession {session ->
            session.getNamedQuery( 'ActionItemStatus.fetchActionItemStatusById' ).setLong( 'myId', myId )?.list()[0]
        }
    }

    /**
     *
     * @return
     */
    static def fetchActionItemStatusCount( actionItemStatus ) {
        ActionItemStatus.withSession {session ->
            session.getNamedQuery( 'ActionItemStatus.fetchActionItemStatusCount' )
                    .setString( 'actionItemStatus', CommunicationCommonUtility.getScrubbedInput( actionItemStatus ) )
                    .uniqueResult()
        }
    }

    /**
     *
     * @param actionItemStatus
     * @return
     */
    static def checkIfNameAlreadyPresent( actionItemStatus ) {
        ActionItemStatus.withSession {session ->
            session.getNamedQuery( 'ActionItemStatus.checkIfNameAlreadyPresent' )
                    .setString( 'actionItemStatus', actionItemStatus )
                    .uniqueResult() > 0
        }
    }

    /**
     *
     * @param filterData
     * @param pagingAndSortParams
     * @return
     */
    static fetchWithPagingAndSortParams( filterData, pagingAndSortParams ) {
        def queryCriteria = ActionItemStatus.createCriteria()
        def results = queryCriteria.list( max: pagingAndSortParams.max, offset: pagingAndSortParams.offset ) {
            ilike( "actionItemStatus", CommunicationCommonUtility.getScrubbedInput( filterData?.params?.name ) )
            order( (pagingAndSortParams.sortAscending ? Order.asc( pagingAndSortParams?.sortColumn ) : Order.desc( pagingAndSortParams?.sortColumn )).ignoreCase() )
            if (!pagingAndSortParams?.sortColumn.equals( "actionItemStatus" )) {
                order( Order.asc( 'actionItemStatus' ).ignoreCase() )
            }
        }

        return results
    }
}
