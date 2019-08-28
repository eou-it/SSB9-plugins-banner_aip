/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase
@Transactional
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
