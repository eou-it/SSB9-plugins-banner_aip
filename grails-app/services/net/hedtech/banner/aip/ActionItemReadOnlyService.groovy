/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

class ActionItemReadOnlyService extends ServiceBase {

    /**
     *
     * @param aiId
     * @return
     */
    def getActionItemROById( Long aiId ) {
        ActionItemReadOnly.fetchActionItemROById( aiId )
    }

    /**
     *
     * @return
     */
    def listActionItemRO() {
        ActionItemReadOnly.fetchActionItemRO()
    }

    /**
     *
     * @param param
     * @return
     */
    def listActionItemROCount( param ) {
        ActionItemReadOnly.fetchActionItemROCount( param )
    }

    /**
     *
     * @param params
     * @return
     */
    def fetchWithPagingAndSortParams( Map params ) {
        ActionItemReadOnly.fetchWithPagingAndSortParams(
                [name: params?.filterName],
                [sortColumn: params.sortColumn, sortAscending: params.sortAscending, max: params.max, offset: params.offset] )
    }
}
