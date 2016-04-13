/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr

import org.hibernate.annotations.Type
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
     * Version of the action item
     */
    @Version
    @Column(name = "GCBAGRP_VERSION", length = 19)
    Long version

    /**
     * Data Origin column for SORNOTE
     */
    @Column(name = "GCBAGRP_DATA_ORIGIN", length = 30)
    String dataOrigin


    public String toString() {
        """ActionItemGroup[
                id:$id,
                title:$title,
                folderId: $folderId,
                description:$description,
                status:$status,
                userId:$userId,
                activityDate:$activityDate,
                version:$version,
                dataOrigin=$dataOrigin]"""
    }

    int hashCode() {
        int result;
        result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
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
        title(nullable: false, maxSize: 2048)
        description(nullable: true) //summary length only for now
        folderId(nullable: false, maxSize: 30)
        status(nullable: false, maxSize: 30)
        userId(nullable: false, maxSize: 30)
        activityDate(nullable: false, maxSize: 30)
        version(nullable: false, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 19)
    }


    public static def fetchActionItemGroups() {
        ActionItemGroup.withSession { session ->
            List ActionItemGroup = session.getNamedQuery('ActionItemGroup.fetchActionItemGroups').list()
            return ActionItemGroup
        }
    }

    public static def fetchActionItemGroupById(Long id) {
        ActionItemGroup.withSession { session ->
            List actionItemGroupById = session.getNamedQuery('ActionItemGroup.fetchActionItemGroupById').setLong('myId', id).list()
            return actionItemGroupById
        }


    }
}