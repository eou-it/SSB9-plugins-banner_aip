/*********************************************************************************
 Copyright 2015 Ellucian Company L.P. and its affiliates.
 *********************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.sql.Sql
import net.hedtech.banner.service.ServiceBase

/**
 * DAO service interface for ActionItem group send item objects.
 */
class ActionItemPostWorkService extends ServiceBase {

    def preCreate( domainModelOrMap ) {
        ActionItemPostWork groupSendItem = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as
                ActionItemPostWork
        if (groupSendItem.getCreationDateTime() == null) {
            groupSendItem.setCreationDateTime( new Date() )
        };
    }

    public def fetchRunningGroupSendItemCount( Long groupSendId ) {
        Sql sql = new Sql(sessionFactory.getCurrentSession().connection())
        def count = 0
        def readyvar = 'Ready'
        try {
            sql.eachRow("select count(*) as totalCount from GCRAIIM where GCRAIIM_GROUP_SEND_ID = ? and GCRAIIM_CURRENT_STATE = ?", [groupSendId,
                                                                                                                                    readyvar]) { row ->
                count = row.totalCount
            }
        } finally {
            sql?.close()
        }
        return count
    }

}
