/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.common
/**
 * Enum filed for AIP posting states
 */
public enum PostingStateEnum {
    QUEUED( 'QUEUE' ),
    SCHEDULED( 'SCHEDULED' )
    String statusName

    /**
     *
     * @param statusName
     */
    PostingStateEnum( statusName ) {
        this.statusName = statusName
    }
}
