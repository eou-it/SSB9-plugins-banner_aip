/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Manages document upload instances.
 */
class UploadDocumentContentService extends ServiceBase {

    /**
     * Validation before uploading document
     * @param dataMap
     */
    def preCreateValidation( domainModelOrMap ) {
        UploadDocumentContentService udc = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as UploadDocumentContentService
        }
    /**
     * This method is responsible for getting content of attached document based on ID.
     * @param params
     * @return
     */
    def fetchContentByFileUploadId(id) {
        UploadDocumentContent.fetchContentByFileUploadId(id)
    }
}


