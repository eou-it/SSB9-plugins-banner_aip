/** *****************************************************************************
 © 2016 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */
package net.hedtech.banner.aip

import javax.persistence.*

/**
 * BlockedProcessReadOnly.
 *
 * Date: 12/21/2016
 * Time: 12:54 PM
 */

@Entity
@Table(name = "GVQ_GCRABLK_CONFIG")
class BlockedProcessReadOnly {

    /**
     *  Action Item ID
     */

    @Id
    @Column(name = "BLOCK_ACTION_ITEM_ID")
    Long id

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


    @Override
    public String toString() {
        return "BlockedProcessReadOnly{" +
                "id=" + id +
                ", blockLastModifiedBy='" + blockLastModifiedBy + '\'' +
                ", blockLastModified=" + blockLastModified +
                ", blockConfigName='" + blockConfigName + '\'' +
                ", blockConfigType='" + blockConfigType + '\'' +
                ", value='" + value + '\'' +
                ", configLastModifiedBy='" + configLastModifiedBy + '\'' +
                ", configLastModified=" + configLastModified +
                '}';
    }


    boolean equals( o ) {
        if (this.is( o )) return true
        if (!(o instanceof BlockedProcessReadOnly)) return false

        BlockedProcessReadOnly that = (BlockedProcessReadOnly) o

        if (blockConfigName != that.blockConfigName) return false
        if (blockConfigType != that.blockConfigType) return false
        if (blockLastModified != that.blockLastModified) return false
        if (blockLastModifiedBy != that.blockLastModifiedBy) return false
        if (configLastModified != that.configLastModified) return false
        if (configLastModifiedBy != that.configLastModifiedBy) return false
        if (id != that.id) return false
        if (value != that.value) return false

        return true
    }


    int hashCode() {
        int result
        result = id.hashCode()
        result = 31 * result + blockLastModifiedBy.hashCode()
        result = 31 * result + blockLastModified.hashCode()
        result = 31 * result + blockConfigName.hashCode()
        result = 31 * result + blockConfigType.hashCode()
        result = 31 * result + (value != null ? value.hashCode() : 0)
        result = 31 * result + configLastModifiedBy.hashCode()
        result = 31 * result + configLastModified.hashCode()
        return result
    }
}
