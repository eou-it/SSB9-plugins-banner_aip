/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.aip.common

import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView

class ActionItemProcessingCommonService {

    /**
     * Gets Communication folders
     * @return
     */
    def fetchCommunicationFolders() {
        CommunicationFolder.list( sort: "name", order: "asc" )
    }

    /**
     * Gets Population List For Send
     * @return
     */
    def fetchPopulationListForSend( searchParam, paginationParam ) {
        CommunicationPopulationListView.findAllForSendByPagination( [params: [name: searchParam]], paginationParam )
    }
}
