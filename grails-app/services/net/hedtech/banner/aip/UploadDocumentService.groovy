/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.service.ServiceBase

/**
 * Manages document upload instances.
 */
class UploadDocumentService extends ServiceBase {
     static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'


    /**
     * This method is responsible for getting list is attached documents for a response.
     * @param params Map of userActionItemId and responseId
     * @return List of Documents metadata
     */
    def fetchDocuments(params) {
        UploadDocument.fetchDocuments(params)
    }
    /**
     * Method to fetch count of documents
     * @param params Map of userActionItemId and responseId
     * @return Document count
     */
    def fetchDocumentsCount(params) {
        UploadDocument.fetchDocumentsCount(params)
    }
    /**
     * Method to fetch file storage location by Document ID
     * @param id Document Id
     * @return File Storage location - AIP or BDM
     */
    def fetchFileLocationById(id) {
        UploadDocument.fetchFileLocationById(id)
    }
}
