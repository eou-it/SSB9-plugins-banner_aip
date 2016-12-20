/** *****************************************************************************
 © 2016 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */
package net.hedtech.banner.aip

import org.hibernate.annotations.Type

import javax.persistence.*

/**
 * UserBlockedProcessReadOnly.
 *
 * Date: 12/16/2016
 * Time: 9:37 AM
 */

@NamedQueries(value = [
        @NamedQuery(name = "UserBlockedProcessReadOnly.fetchIsBlockedROsByPidmAndId",
                query = """FROM UserBlockedProcessReadOnly a
                   WHERE a.pidm = :myPidm
                   AND a.id = :aid
                   AND a.isBlocking is true
                   """)
])
@Entity
@Table(name = "GVQ_GCRABLK")
class UserBlockedProcessReadOnly {
    /**
     *  Action Item ID in GCRABLK
     */

    @Id
    @Column(name = "ACTION_ITEM_ID")
    Long id

    /**
     * PIDM of the user assigned with this action item
     */

    @Column(name = "ACTION_ITEM_PIDM", length = 9)
    Long pidm

    /**
     * Does this action Item Block the url

     */

    @Type(type = "yes_no")
    @Column(name = "ACTION_ITEM_IS_BLOCKING")
    Boolean isBlocking = false


    @Override
    public String toString() {
        return "UserBlockedProcessReadOnly{" +
                "id=" + id +
                ", pidm=" + pidm +
                ", isBlocking=" + isBlocking +
                '}';
    }

    static constraints = {
        id( nullable: false, maxSize: 19 )
        isBlocking( nullable: false, maxSize: 1 )
        pidm( nullable: false, maxSize: 9 )
    }


    boolean equals( o ) {
        if (this.is( o )) return true
        if (!(o instanceof UserBlockedProcessReadOnly)) return false

        UserBlockedProcessReadOnly that = (UserBlockedProcessReadOnly) o

        if (id != that.id) return false
        if (isBlocking != that.isBlocking) return false
        if (pidm != that.pidm) return false

        return true
    }


    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (pidm != null ? pidm.hashCode() : 0)
        result = 31 * result + (isBlocking != null ? isBlocking.hashCode() : 0)
        return result
    }

    public static def fetchBlockingProcessesROByPidmAndActionItemId( Long pidm, Long aid ) {
        UserActionItemReadOnly.withSession { session ->
            List<UserBlockedProcessReadOnly> blockedProcessesReadOnly = session.getNamedQuery( 'UserBlockedProcessReadOnly.fetchIsBlockedROsByPidmAndId' )
                    .setLong(
                    'myPidm', pidm )
                    .setLong(
                    'aid', aid )
                    .list()
            return blockedProcessesReadOnly
        }
    }
}
