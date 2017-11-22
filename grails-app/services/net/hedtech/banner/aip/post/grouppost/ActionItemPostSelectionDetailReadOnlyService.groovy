/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Posting Details
 */
class ActionItemPostSelectionDetailReadOnlyService extends ServiceBase {

    /**
     *
     * @param postingId
     * @return
     */
    List<ActionItemPostSelectionDetailReadOnly> fetchSelectionIds( postingId ) {
        ActionItemPostSelectionDetailReadOnly.fetchSelectionIds( postingId )
    }
}
