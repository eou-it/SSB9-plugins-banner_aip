/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Blocking Process
 */
class BlockingProcessService extends ServiceBase {
    /**
     * Lists Non Global blocking process
     * @return
     */
    def fetchNonGlobalBlockingProcess() {
        BlockingProcess.fetchNonGlobalBlockingProcess()
    }

    def fetchGlobalBlockingProcess() {
            BlockingProcess.fetchGlobalBlockingProcess()
        }
}
