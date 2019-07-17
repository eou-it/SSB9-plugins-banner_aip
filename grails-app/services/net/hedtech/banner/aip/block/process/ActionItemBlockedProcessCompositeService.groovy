/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.i18n.MessageHelper
import org.springframework.transaction.annotation.Propagation

/**
 * Service class for Action item Blocked Process
 */
@Transactional
@Slf4j
class ActionItemBlockedProcessCompositeService {

    def actionItemBlockedProcessReadOnlyService
    def actionItemBlockedProcessService
    def blockingProcessService


    def getBlockedProcessForSpecifiedActionItem( Long actionItemId ) {
        def result = [:]
        List<ActionItemBlockedProcessReadOnly> blockedList = actionItemBlockedProcessReadOnlyService.fetchByActionItemId( actionItemId )
        if (!blockedList) {
            return []
        }
        boolean globalBlockProcess = blockedList.find {
            it.processGlobalProcInd == AIPConstants.YES_IND
        }?.processGlobalProcInd == AIPConstants.YES_IND

        def actionItem = blockedList?.get( 0 )
        blockedList.retainAll {it.processGlobalProcInd != AIPConstants.YES_IND}
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
                     blockedProcessAppRoleDisplay : obj.blockedProcessAppRole ? MessageHelper.message( 'aip.blocking.process.persona.' + obj.blockedProcessAppRole ) : null,
                     processCode                  : obj.processCode,
                     processName                  : obj.processName,
                     version                      : obj.version,
                     urls                         : processIdAndUrlMap.get( obj.id.blockingProcessId )
                    ]
            )
        }
        result.actionItem = [name: actionItem.actionItemName, actionItemFolderId: actionItem.actionItemFolderId, id: actionItem.id.actionItemId]
        result.blockedProcess = processUrlList.sort() {it.processName}
        result.globalBlockProcess = globalBlockProcess
        result
    }

    /**
     *
     * @param paramMap
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = ApplicationException.class)
    def updateBlockedProcessItems( paramMap ) {
        boolean isGlobalBlock = paramMap.globalBlockProcess

        log.debug(  'Input params for updateBlockedProcessItems' + paramMap )
        if (!paramMap.actionItemId) {
            log.error(  'Action Item Id not present' )
            return [success: false, message: MessageHelper.message( 'incomplete.request.action.item.id.not.present' )]
        }
        def initialSize = paramMap.blockedProcesses?.size()
        def uniqueListSie = paramMap.blockedProcesses?.unique().size()
        if (uniqueListSie != initialSize) {
            return [success: false, message: MessageHelper.message( 'process.persona.combination.not.unique' )]
        }
        List blockedProcesses = paramMap.blockedProcesses?.unique()
        long actionItemId = new Long( paramMap.actionItemId )
        List<ActionItemBlockedProcess> exitingBlockedProcessList = actionItemBlockedProcessService.listBlockedProcessByActionItemId( actionItemId )
        List<Long> deleteActionItemBlockedProcessList = []
        if (exitingBlockedProcessList) {
            deleteActionItemBlockedProcessList.push( exitingBlockedProcessList.id )
        }
        List<ActionItemBlockedProcess> addActionItemBlockedProcessList = []
        blockedProcesses.each {process ->
            ActionItemBlockedProcess actionItemBlockedProcess
            actionItemBlockedProcess = new ActionItemBlockedProcess(
                    blockedProcessId: process.processId,
                    blockActionItemId: actionItemId,
                    blockedProcessRole: process.persona
            )
            addActionItemBlockedProcessList.push( actionItemBlockedProcess )
        }
        if (isGlobalBlock) {
            List<Long> globalProcessIds = blockingProcessService.fetchGlobalBlockingProcess().id
            globalProcessIds.each {processId ->
                def actionItemBlockedProcess = new ActionItemBlockedProcess(
                        blockedProcessId: processId,
                        blockActionItemId: actionItemId,
                        blockedProcessRole: null
                )
                addActionItemBlockedProcessList.push( actionItemBlockedProcess )
            }
        }
        actionItemBlockedProcessService.delete( deleteActionItemBlockedProcessList )
        actionItemBlockedProcessService.createOrUpdate( addActionItemBlockedProcessList, false )
        [success: true]
    }

}
