/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import org.hibernate.annotations.Type
import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemDetail.fetchActionItemDetailById",
                query = """
           FROM ActionItemDetail a
           WHERE a.actionItemId = :myId
          """)
])

@Entity
@Table(name = "GCRACNT")

class ActionItemDetail implements Serializable {

    /**
     * Surrogate ID for GCRACNT
     */

    @Id
    @Column(name = "GCRACNT_SURROGATE_ID")
    @SequenceGenerator(name = "GCRACNT_SEQ_GEN", allocationSize = 1, sequenceName = "GCRACNT_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRACNT_SEQ_GEN")
    Long id

    /***
     * Related ID of the action item
     */
    @Column(name = "GCRACNT_ACTION_ITEM_ID", length = 19)
    Long actionItemId

    /**
     * Text for action item detail
     */
    @Column(name = "GCRACNT_ACTION_ITEM_TEXT")
    /*need to figure out what to limit length to for display*/
    String text


    /**
     * User action item pertains to
     */
    @Column(name = "GCRACNT_USER_ID", length = 30)
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRACNT_ACTIVITY_DATE")
    Date activityDate

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCRACNT_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCRACNT_DATA_ORIGIN", length = 30)
    String dataOrigin


    public String toString() {
        """ActionItemDetail[
                id:$id,
                actionItemId:$actionItemId,
                text:$text,
                userId:$userId,
                activityDate:$activityDate,
                version:$version,
                dataOrigin=$dataOrigin]"""
    }

    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (actionItemId != null ? actionItemId.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0);
        return result;
    }

    static constraints = {
        id(nullable: false, maxSize: 19)
        actionItemId(nullable: false, maxSize: 19)
        text(nullable: true) //summary length only for now
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false, maxSize: 30)
        version(nullable: false, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 19)
    }


    public static def fetchActionItemDetailById( Long id ) {
        ActionItemDetail.withSession { session ->
            List actionItemDetail = session.getNamedQuery('ActionItemDetail.fetchActionItemDetailById').setLong('myId', id ).list()
            return actionItemDetail
        }
    }

}
