/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import org.hibernate.annotations.Type
import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "ActionItemList.fetchActionItems",
                query = """
           FROM ActionItemList a
          """)
])

@Entity
@Table(name = "GCBCSRT")

class ActionItemList implements Serializable {

    /**
     * Surrogate ID for GCBCSRT
     */
    @Id
    @Column(name = "GCBCSRT_SURROGATE_ID", length = 19)
    Long id

    /**
     * Name of the action item
     */
    @Column(name = "GCBCSRT_NAME", length = 2048)
    String actionItem

    /**
     * Indicator that the action item is active
     */
    @Column(name = "GCBCSRT_ACTIVE", length = 1)
    Integer active

    /**
     * User action item pertains to
     */
    @Column(name = "GCBCSRT_USER_ID", length = 30)
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCBCSRT_ACTIVITY_DATE", length = 30)
    Date activityDate

    /**
     * Description for action item
     */
    @Column(name = "GCBCSRT_DESCRIPTION", length = 4000)
    /*need to figure out what to limit length to for display*/
    String description

    /**
     * UserID that created the action item
     */
    @Column(name = "GCBCSRT_CREATOR_ID", length = 30)
    String creatorId

    /**
     * Date the action item was created
     */
    @Column(name = "GCBCSRT_CREATE_DATE", length = 30)
    Date createDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCBCSRT_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCBCSRT_DATA_ORIGIN", length = 30)
    String dataOrigin


    public String toString() {
        """ActionItemList[
                id:$id,
                actionItem:$actionItem,
                active:$active,
                userId:$userId,
                activityDate:$activityDate,
                description:$description,
                creatorId:$creatorId
                createDate:$createDate,
                version:$version,        ],
                dataOrigin=$dataOrigin, ]"""
    }

    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (actionItem != null ? actionItem.hashCode() : 0);
        result = 31 * result + (active != null ? active.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (creatorId != null ? creatorId.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0);
        return result;
    }

    static constraints = {
        id(nullable: false, maxSize: 19)
        actionItem(nullable: false, maxSize: 2048)
        active(nullable: false, maxSize: 1)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false, maxSize: 30)
        description(nullable: true, maxSize: 4000) //summary length only for now
        creatorId(nullable: true, maxSize: 30)
        createDate(nullable: true, maxSize: 30)
        version(nullable: false, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 19)
    }

    public static def fetchActionItems() {
        def actionItem
        ActionItemList.withSession { session ->
            actionItem = session.getNamedQuery(
                    'ActionItemList.fetchActionItems').list()[0]
        }
        return actionItem
    }
}