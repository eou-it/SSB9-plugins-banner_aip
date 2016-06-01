/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase
import org.springframework.transaction.annotation.Transactional

class ActionItemGroupService extends ServiceBase {

    boolean transactional = true

    //simple return of all action items
    def listActionItemGroups() {
        return ActionItemGroup.fetchActionItemGroups( )
    }

    def listActionItemGroupById(Long actionItemGroupId) {
        return ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
    }

    /*
    void preCreate( map ) {
        def group = map.domainModel
        //group.summary = generateSummary( group.group )
    }
    */

}