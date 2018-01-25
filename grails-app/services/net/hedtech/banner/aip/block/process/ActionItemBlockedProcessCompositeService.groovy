/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import grails.transaction.Transactional
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.exceptions.ApplicationException
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Propagation

/**
 * Service class for Action item Blocked Process
 */
class ActionItemBlockedProcessCompositeService {
    static transactional = true
    def actionItemBlockedProcessReadOnlyService
    def actionItemBlockedProcessService
    def blockingProcessService
    private static final LOGGER = Logger.getLogger( this.class )


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
        List blockedProcesses = paramMap.blockedProcesses
        LoggerUtility.debug( LOGGER, 'Input params for updateBlockedProcessItems' + paramMap )
        if (!paramMap.actionItemId || (!isGlobalBlock && !blockedProcesses)) {
            return [success: false, message: 'Invalid Input Request']
        }
        long actionItemId = new Long( paramMap.actionItemId )
        List<Long> exitingBlockedProcessId = actionItemBlockedProcessService.listBlockedProcessByActionItemId( actionItemId )?.blockedProcessId
        LoggerUtility.debug( LOGGER, 'exitingBlockedProcessId' + exitingBlockedProcessId )
        List<Long> inputBlockedProcessId = blockedProcesses.processId
        List<Long> deleteBlockedProcessId = exitingBlockedProcessId - inputBlockedProcessId
        LoggerUtility.debug( LOGGER, 'deleteBlockedProcessId' + deleteBlockedProcessId )
        List<ActionItemBlockedProcess> addActionItemBlockedProcessList = []
        blockedProcesses.each {process ->
            ActionItemBlockedProcess actionItemBlockedProcess = actionItemBlockedProcessService.getBlockedProcessByActionItemAndProcessId( actionItemId, process.processId )
            if (actionItemBlockedProcess) {
                //update
                LoggerUtility.debug( LOGGER, 'actionItemBlockedProcess for update' + actionItemBlockedProcess )
                actionItemBlockedProcess.blockedProcessRole = process.persona
            } else {
                actionItemBlockedProcess = new ActionItemBlockedProcess(
                        blockedProcessId: process.processId,
                        blockActionItemId: actionItemId,
                        blockedProcessRole: process.persona
                )
            }
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
        List<Long> deleteActionItemBlockedProcessList = []
        deleteBlockedProcessId.each {processId ->
            LoggerUtility.debug( LOGGER, 'collecting for delete' + processId )
            deleteActionItemBlockedProcessList.push( actionItemBlockedProcessService.getBlockedProcessByActionItemAndProcessId( actionItemId, processId )?.id )
        }
        actionItemBlockedProcessService.delete( deleteActionItemBlockedProcessList )
        actionItemBlockedProcessService.createOrUpdate( addActionItemBlockedProcessList, false )
        [success: true]
    }

}
