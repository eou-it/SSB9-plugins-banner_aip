/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

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
    @Column(name = "GCRACNT_ACTION_ITEM_TEXT", columnDefinition = "TEXT")
    /*need to figure out what to limit length to for display*/
    String text

    /***
    * Related ID of the action item template
    */
    @Column(name = "GCRACNT_TEMPLATE_REFERENCE_ID", length = 19)
    Long actionItemTemplateId


    /**
     * User action item pertains to
     */
    @Column(name = "GCRACNT_USER_ID", length = 30)
    String lastModifiedby

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCRACNT_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModified


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
                actionItemTemplateId:$actionItemTemplateId,
                userId:$lastModifiedby,
                activityDate:$lastModified,
                version:$version,
                dataOrigin=$dataOrigin]"""
    }


    boolean equals( o ) {
        if (this.is( o )) return true
        if (!(o instanceof ActionItemDetail)) return false

        ActionItemDetail that = (ActionItemDetail) o

        if (actionItemId != that.actionItemId) return false
        if (lastModified != that.lastModified) return false
        if (dataOrigin != that.dataOrigin) return false
        if (id != that.id) return false
        if (text != that.text) return false
        if (actionItemTemplateId != that.actionItemTemplateId) return false
        if (lastModifiedby != that.lastModifiedby) return false
        if (version != that.version) return false

        return true
    }


    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (actionItemId != null ? actionItemId.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (actionItemTemplateId != null ? actionItemTemplateId.hashCode() : 0);
        result = 31 * result + (lastModifiedby != null ? lastModifiedby.hashCode() : 0);
        result = 31 * result + (lastModified != null ? lastModified.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0);
        return result;
    }

    static constraints = {
//        id(nullable: false, maxSize: 19)
        actionItemId(nullable: false, maxSize: 19)
        text(nullable: true) //summary length only for now
        actionItemTemplateId(nullable: true)
        lastModifiedby(nullable: false, maxSize: 30)
        lastModified(nullable: false, maxSize: 30)
        version(nullable: true, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 19)
    }


    public static def fetchActionItemDetailById( Long id ) {
        ActionItemDetail.withSession { session ->
            List actionItemDetail = session.getNamedQuery('ActionItemDetail.fetchActionItemDetailById').setLong('myId', id ).list()
            return actionItemDetail[0]?actionItemDetail[0]:[]
        }
    }
}
