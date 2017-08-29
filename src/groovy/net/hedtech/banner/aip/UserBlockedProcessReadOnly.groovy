/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type

import javax.persistence.*

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
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
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

    static constraints = {
        id( nullable: false, maxSize: 19 )
        isBlocking( nullable: false, maxSize: 1 )
        pidm( nullable: false, maxSize: 9 )
    }

    /**
     *
     * @param pidm
     * @param aid
     * @return
     */
    public static def fetchBlockingProcessesROByPidmAndActionItemId( Long pidm, Long aid ) {
        UserActionItemReadOnly.withSession {session ->
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
