/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.service.ServiceBase

/**
 * Manages document upload instances.
 */
class UploadDocumentService extends ServiceBase {

    static final String NO_NAME_ERROR = '@@r1:DocumentNameCanNotBeNullError@@'
    static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'
    static final String FILE_TYPE_ERROR = '@@r1:FileTypeError@@'
    static final String UNIQUE_DOCUMENT_NAME_ERROR = '@@r1:UniqueNameInFolderError@@'
    static final String OTHER_VALIDATION_ERROR = '@@r1:ValidationError@@'
    def uploadDocumentService

    /**
     * Validation before uploading document
     * @param dataMap
     */
    def preCreateValidation(domainModelOrMap) {
        UploadDocument ud = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as UploadDocument
        if (!ud.validate()) {
            def errorCodes = ud.errors.allErrors.codes[0]
            if (errorCodes.contains('uploadDocument.documentname.nullable')) {
                throw new ApplicationException(UploadDocument, NO_NAME_ERROR, 'uploadDocument.documentname.nullable.error')
            } else if (errorCodes.contains('uploadDocument.max.file.size')) {
                throw new ApplicationException(UploadDocument, MAX_SIZE_ERROR, 'uploadDocument.max.file.size.error')
            } else if (errorCodes.contains('uploadDocument.file.type')) {
                throw new ApplicationException(UploadDocument, FILE_TYPE_ERROR, 'uploadDocument.file.type.error')
            }
        }
        if (UploadDocument.existsSameDocumentName(ud.documentName)) {
            throw new ApplicationException(UploadDocument, UNIQUE_DOCUMENT_NAME_ERROR, 'uploadDocument.documentname.unique.error')
        }
    }

    /**
     * Validation for FileType and FileSize
     * @param dataMap
     */
    def fileValidation(fileName, fileContent) {
        def configureddocumentType = uploadDocumentService.uploadDocumentType();
        def configureddocumentSize = uploadDocumentService.uploadDocumentSize();
        def documentSizeInKB = (fileContent.length / 1024);
        if (getFileExtension(fileName).equals(configureddocumentType.documentType)) {
            throw new ApplicationException(UploadDocumentService, FILE_TYPE_ERROR, 'uploadDocument.file.type.error')
        }
        if (documentSizeInKB > configureddocumentType.documentSize) {
            throw new ApplicationException(UploadDocumentService, MAX_SIZE_ERROR, 'uploadDocument.max.file.size.error')
        }
    }
    /**
     * This method is responsible for getting list is attached documents for a response.
     * @param params
     * @return
     */
    def fetchDocuments(params) {

        UploadDocument.fetchDocuments(params)
    }
    /**
     *
     * @param param
     * @return
     */
    def fetchDocumentsCount(params) {
        UploadDocument.fetchDocumentsCount(params)
    }
    /**
     *
     * @param id,pidm
     * @return
     */
    def fetchFileLocationById(id) {
        UploadDocument.fetchFileLocationById(id)
    }
}
