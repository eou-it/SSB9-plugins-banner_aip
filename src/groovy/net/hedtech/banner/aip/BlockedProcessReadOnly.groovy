/*********************************************************************************
 Copyright 2016-2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.*

/**
 * BlockedProcessReadOnly.
 */

@Entity
@Table(name = "GVQ_GCRABLK_CONFIG")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class BlockedProcessReadOnly {

    /**
     *  Action Item ID
     */

    @Id
    @Column(name = "BLOCK_SURROGATE_ID")
    Long id

    @Column(name = "BLOCK_ACTION_ITEM_ID")
    Long blockActionItemId

    @Column(name = "BLOCK_USER_ID", length = 30)
    String blockLastModifiedBy

    @Column(name = "BLOCK_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date blockLastModified

    @Column(name = "CONFIG_NAME")
    String blockConfigName

    @Column(name = "CONFIG_TYPE", columnDefinition = "default 'json/aipBlock'")
    String blockConfigType

    @Column(name = "CONFIG_VALUE")
    @Lob
    String value

    @Column(name = "CONFIG_USER_ID", length = 30)
    String configLastModifiedBy

    @Column(name = "CONFIG_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date configLastModified
}
