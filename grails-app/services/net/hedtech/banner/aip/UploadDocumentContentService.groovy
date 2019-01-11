/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Manages document upload instances.
 */
class UploadDocumentContentService extends ServiceBase {

    /**
     * This method is responsible for getting content of attached document based on ID.
     * @param params
     * @return
     */
    def fetchContentByFileUploadId(id) {
        UploadDocumentContent.fetchContentByFileUploadId(id)
    }
}


