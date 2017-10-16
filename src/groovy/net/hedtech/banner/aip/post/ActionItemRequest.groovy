/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * An Action Item Request is a request to send am action item group set
 * to a specific pidm based recipient on behalf of a sender.
 *
 * Implementation note: the named object was favored over a map in order to better
 * describe the required attributes involved and follow the custom from BRM.
 */
@EqualsAndHashCode
@ToString
class ActionItemRequest implements Serializable {

    /** Optional correlation id for tracking the communication externally. **/
    public String referenceId
    /** The pidm of the recipient the communication is targeted towards. **/
    public long recipientPidm
    /** The Oracle user name of the person or entity submitting that submitted the request. **/
    public String initiatorUserId
}
