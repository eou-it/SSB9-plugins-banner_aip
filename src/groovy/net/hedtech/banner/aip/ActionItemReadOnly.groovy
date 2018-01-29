/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import net.hedtech.banner.general.CommunicationCommonUtility
import org.hibernate.criterion.Order

import javax.persistence.*


@NamedQueries(value = [

        @NamedQuery(name = "ActionItemReadOnly.fetchActionItemROById",
                query = """
           FROM ActionItemReadOnly a
           WHERE a.actionItemId = :myId
          """),
        @NamedQuery(name = "ActionItemReadOnly.fetchActionItemRO",
                query = """
           FROM ActionItemReadOnly a
          """),
        @NamedQuery(name = "ActionItemReadOnly.fetchActionItemROCount",
                query = """SELECT COUNT(a.actionItemId) FROM ActionItemReadOnly a 
                           WHERE upper(a.actionItemName) like upper(:actionItemName) 
            """
        )

])
@Entity
@Table(name = "GVQ_GCBACTM")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Action Item Read only View
 */
class ActionItemReadOnly implements Serializable {

    /**
     *  ID for Action Item
     */

    @Id
    @Column(name = "ACTION_ITEM_ID")
    Long actionItemId

    /**
     * Name of the action item
     */

    @Column(name = "ACTION_ITEM_NAME")
    String actionItemName

    /**
     * Title of the action item
     */

    @Column(name = "ACTION_ITEM_TITLE")
    String actionItemTitle

    /**
     * ID of the action item folder
     */

    @Column(name = "ACTION_ITEM_FOLDER_ID")
    Long folderId

    /**
     * Name of the action item folder
     */

    @Column(name = "ACTION_ITEM_FOLDER_NAME")
    String folderName

    /**
     * Description of the action item folder
     */

    @Column(name = "ACTION_ITEM_FOLDER_DESCRIPTION")
    String folderDesc

    /**
     * PIDM of the user action item belongs to
     */

    @Column(name = "ACTION_ITEM_DESCRIPTION", columnDefinition = "TEXT")
    String actionItemDesc

    /**
     * Status of action item
     */

    @Column(name = "ACTION_ITEM_STATUS")
    String actionItemStatus

    /**
     * Status of action item
     */

    @Column(name = "ACTION_ITEM_POSTED_STATUS")
    String actionItemPostedStatus

    /**
     * Last activity date for the action item
     */

    @Column(name = "ACTION_ITEM_ACTIVITY_DATE")
    Date actionItemActivityDate

    /**
     * * User who last updated the Action Item
     */

    @Column(name = "ACTION_ITEM_USER_ID")
    String actionItemUserId

    /**
     * User who last updated the content
     */

    @Column(name = "ACTION_ITEM_CONTENT_USER_ID")
    String actionItemContentUserId

    /**
     * UserID that created the action item
     */

    @Column(name = "ACTION_ITEM_CREATOR_ID")
    String actionItemCreatorId

    /**
     * Date the action item was created
     */

    @Column(name = "ACTION_ITEM_CREATE_DATE")
    Date actionItemCreateDate

    /**
     * Date the action item or the detail was last updated
     */

    @Column(name = "ACTION_ITEM_COMPOSITE_DATE")
    Date actionItemCompositeDate

    /**
     * User who last updated either the action item or the detail
     */

    @Column(name = "ACTION_ITEM_LAST_USER_ID")
    String actionItemLastUserId

    /**
     * Version of the action item
     */

    @Version
    @Column(name = "ACTION_ITEM_VERSION")
    Long actionItemVersion

    /**
     * ID of the action item template
     */
    @Column(name = "ACTION_ITEM_PB_TEMPLATE_ID")
    Long actionItemTemplateId

    /**
     * Name of the action item Page builder template
     */
    @Column(name = "ACTION_ITEM_PB_TEMPLATE_NAME")
    String actionItemTemplateName


    @Column(name = "ACTION_ITEM_PAGE_NAME")
    String actionItemPageName


    @Column(name = "ACTION_ITEM_CONTENT_ID")
    Long actionItemContentId

    /**
     * Date the action item content detail was last updated
     */

    @Column(name = "ACTION_ITEM_CONTENT_DATE")
    Date actionItemContentDate

    /**
     * Content for the action item
     */

    @Column(name = "ACTION_ITEM_CONTENT", columnDefinition = "TEXT")
    String actionItemContent

    /**
     *
     * @param myId
     * @return
     */
    static def fetchActionItemROById( Long myId ) {
        ActionItemReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemROById' )
                    .setLong( 'myId', myId )
                    .list()[0]
        }
    }

    /**
     *
     * @return
     */
    static def fetchActionItemRO() {
        ActionItemReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemRO' ).list()
        }
    }

    /**
     *
     * @return
     */
    static def fetchActionItemROCount( params ) {
        ActionItemReadOnly.withSession {session ->
            session.getNamedQuery( 'ActionItemReadOnly.fetchActionItemROCount' )
                    .setString( 'actionItemName', CommunicationCommonUtility.getScrubbedInput( params?.name ) )
                    .uniqueResult()
        }
    }

    // TODO: make filter (filterData and ilike) optional
    // TODO: filter seems to need tests.
    /**
     *
     * @param filterData
     * @param pagingAndSortParams
     * @return
     */
    static fetchWithPagingAndSortParams( filterData, pagingAndSortParams ) {
        def queryCriteria = ActionItemReadOnly.createCriteria()
        queryCriteria.list( max: pagingAndSortParams.max, offset: pagingAndSortParams.offset ) {
            ilike( "actionItemName", CommunicationCommonUtility.getScrubbedInput( filterData?.name ) )
            order( (pagingAndSortParams.sortAscending ? Order.asc( pagingAndSortParams?.sortColumn ) : Order.desc( pagingAndSortParams?.sortColumn )).ignoreCase() )
            if (!pagingAndSortParams?.sortColumn.equals( "actionItemName" )) {
                order( Order.asc( 'actionItemName' ).ignoreCase() )
            }
        }
    }

}
