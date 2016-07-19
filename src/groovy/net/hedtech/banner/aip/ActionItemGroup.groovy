/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import javax.persistence.*


@NamedQueries(value = [
        @NamedQuery(name = "ActionItemGroup.fetchActionItemGroups",
                query = """
           FROM ActionItemGroup a
          """),
        @NamedQuery(name = "ActionItemGroup.fetchActionItemGroupById",
                query = """
           FROM ActionItemGroup a
           WHERE a.id = :myId
          """)
])

@Entity
@Table(name = "GCBAGRP")

class ActionItemGroup implements Serializable {

    /**
     * Surrogate ID for GCBAGRP
     */

    @Id
    @Column(name = "GCBAGRP_SURROGATE_ID")
    @SequenceGenerator(name = "GCBAGRP_SEQ_GEN", allocationSize = 1, sequenceName = "GCBAGRP_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBAGRP_SEQ_GEN")
    Long id

    /**
     * Version of the action item
     */

    @Version
    @Column(name = "GCBAGRP_VERSION", length = 19, nullable = false)
    Long version

    /**
     * Name of the action item
     */
    @Column(name = "GCBAGRP_TITLE", length = 60)
    String title

    /***
     * Related ID of the action item
     */
    @Column(name = "GCBAGRP_FOLDER_ID", length = 19)
    Long folderId

    /**
     * Description for action item
     */
    @Column(name = "GCBAGRP_DESCRIPTION")
    /*need to figure out what to limit length to for display*/
    String description

    /**
     * Indicator that the action item is active
     */
    @Column(name = "GCBAGRP_STATUS", length = 30)
    String status

    /**
     * User action item pertains to
     */
    @Column(name = "GCBAGRP_USER_ID", length = 30)
    String userId

    /**
     * Last activity date for the action item
     */
    @Column(name = "GCBAGRP_ACTIVITY_DATE")
    Date activityDate

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCBAGRP_DATA_ORIGIN", length = 30)
    String dataOrigin


    public String toString() {
        """ActionItemGroup[
                id:$id,
                title:$title,
                folderId:$folderId,
                description:$description,
                status:$status,
                userId:$userId,
                activityDate:$activityDate,
                version:$version,
                dataOrigin:$dataOrigin]"""
    }


    boolean equals( o ) {
        if (this.is( o )) return true
        if (!(o instanceof ActionItemGroup)) return false

        ActionItemGroup that = (ActionItemGroup) o

        if (activityDate != that.activityDate) return false
        if (dataOrigin != that.dataOrigin) return false
        if (description != that.description) return false
        if (folderId != that.folderId) return false
        if (id != that.id) return false
        if (status != that.status) return false
        if (title != that.title) return false
        if (userId != that.userId) return false
        if (version != that.version) return false

        return true
    }


    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0);
        return result;
    }

    static constraints = {
        title(nullable: false, maxSize: 2048, unique: 'folderId')
        description(nullable: true) //summary length only for now
        folderId(nullable: false, maxSize: 30)
        status(nullable: false, maxSize: 30)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false, maxSize: 30)
        //dataOrigin(nullable: true, maxSize: 19)
    }


    public static def fetchActionItemGroups() {
        ActionItemGroup.withSession { session ->
            List<ActionItemGroup> actionItemGroup = session.getNamedQuery('ActionItemGroup.fetchActionItemGroups').list()
            return actionItemGroup
        }
    }

    public static def fetchActionItemGroupById(Long id) {
        ActionItemGroup.withSession { session ->
            ActionItemGroup actionItemGroupById = session.getNamedQuery('ActionItemGroup.fetchActionItemGroupById').setLong('myId', id).list()[0]
            return actionItemGroupById
        }


    }
}