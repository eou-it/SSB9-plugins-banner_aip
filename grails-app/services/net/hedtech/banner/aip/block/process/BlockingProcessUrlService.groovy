/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */

package net.hedtech.banner.aip.block.process

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Blocking Process URL
 */
class BlockingProcessUrlService extends ServiceBase {

    /**
     * Lists blocking process urls
     * @return
     */
    def fetchUrls( ) {
        BlockingProcessUrls.fetchUrls( )
    }
}
