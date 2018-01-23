/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import net.hedtech.banner.aip.blocking.process.ActionItemBlockedProcessReadOnly
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action item Blocked Process
 */
class ActionItemBlockedProcessCompositeService extends ServiceBase {
    def actionItemBlockedProcessReadOnlyService


    def getBlockedProcessForSpecifiedActionItem( Long actionItemId ) {
        def result = [:]
        List<ActionItemBlockedProcessReadOnly> blockedList = actionItemBlockedProcessReadOnlyService.fetchByActionItemId( actionItemId )
        if(!blockedList){
            return []
        }
        def actionItem = blockedList?.get( 0 )
        Map processIdAndUrlMap = blockedList.collectEntries {
            [it.id.blockingProcessId, blockedList.findAll {it1 -> it1.id.blockingProcessId == it.id.blockingProcessId}.processUrl.unique().sort()]
        }
        List<Long> allProcessIds = blockedList.id.blockedProcessId.unique()
        List processUrlList = []
        allProcessIds.each {id ->
            ActionItemBlockedProcessReadOnly obj = blockedList.find() {ActionItemBlockedProcessReadOnly it -> it.id.blockedProcessId == id}
            processUrlList.add(
                    [id                           : obj.id,
                     lastModified                 : obj.lastModified,
                     lastModifiedBy               : obj.lastModifiedBy,
                     processPersonaBlockAllowedInd: obj.processPersonaBlockAllowedInd,
                     processSystemReqInd          : obj.processSystemReqInd,
                     blockedProcessAppRole        : obj.blockedProcessAppRole,
                     processName                  : obj.processName,
                     version                      : obj.version,
                     urls                         : processIdAndUrlMap.get( obj.id.blockingProcessId )
                    ]
            )
        }
        result.actionItem = [name: actionItem.actionItemName, actionItemFolderId: actionItem.actionItemFolderId, id: actionItem.id.actionItemId]
        result.blockedProcess = processUrlList.sort() {it.processName}
        result
    }
}
