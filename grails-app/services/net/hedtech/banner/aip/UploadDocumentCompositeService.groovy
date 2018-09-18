/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import org.springframework.web.multipart.MultipartFile
import net.hedtech.banner.general.configuration.ConfigProperties
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.i18n.MessageHelper

/**
 * Class for UploadDocumentCompositeService.
 */
class UploadDocumentCompositeService {

    def springSecurityService
    def uploadDocumentService
    def uploadDocumentContentService
    static final int SIZE_IN_KB = 1024
    static final String DEFAULT_FILE_STORAGE_SYSTEM = 'AIP'
    static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'
    static final String FILE_TYPE_ERROR = '@@r1:FileTypeError@@'


    /**
     * Save uploaded document details in GCRAFLU
     * @param dataMap
     */
    def addUploadDocument(map) {
        def user = springSecurityService.getAuthentication()?.user
        def success = false
        def message = null
        UploadDocument saveUploadDocument
        def fileStorageLocation = getDocumentStorageSystem()
        println "fileStorageLocation->"+fileStorageLocation;
        def aipUser = AipControllerUtils.getPersonForAip([studentId: map.studentId], user.pidm)
        println "aipUser->"+aipUser;
        if (aipUser) {
            UploadDocument ud = new UploadDocument(
                    actionItemId: map.actionItemId,
                    responseId: map.responseId,
                    documentName: map.documentName,
                    documentUploadedDate: new Date(),
                    fileLocation: fileStorageLocation.documentStorageLocation,
                    pidm: aipUser.pidm
            )
            try {
                saveUploadDocument = uploadDocumentService.create(ud)
                if (fileStorageLocation.documentStorageLocation.equals(DEFAULT_FILE_STORAGE_SYSTEM)) {
                    uploadDocumentContent(saveUploadDocument.id, map.file)
                }
                success = true
                message = MessageHelper.message('uploadDocument.save.success')
            } catch (ApplicationException e) {
                success = false
                message = MessageHelper.message('uploadDocument.save.error')
            }
        }
        [
                success: success,
                message: message,
                uploadDocumentObject:saveUploadDocument
        ]
    }

    /**
     * Save uploaded document in GCRAFCT
     *
     */
    def uploadDocumentContent(id, MultipartFile file) {

        UploadDocumentContent saveUploadDocumentContent
        try {
            byte[] bFile = file.getBytes();
            UploadDocumentContent udc = new UploadDocumentContent(
                    fileUploadId: id,
                    documentContent: bFile
            )
            saveUploadDocumentContent = uploadDocumentContentService.create(udc)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fetch configured restricted attachment type val
     *
     */
    def getRestrictedFileTypes() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.restricted.attachment.type', 'GENERAL_SS')
        results = [restrictedFileTypes: configProperties ? configProperties.configValue : null]
        results
    }

    /**
     * Fetch configured allowed attachment max size val
     *
     */
    def getMaxFileSize() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.allowed.attachment.max.size', 'GENERAL_SS')
        results = [maxFileSize: configProperties ? configProperties.configValue : null]
        results
    }

    /**
     * Fetch configured allowed attachment max size val
     *
     */
    def getDocumentStorageSystem() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
        results = [documentStorageLocation: configProperties ? configProperties.configValue : DEFAULT_FILE_STORAGE_SYSTEM]
        results
    }

    /**
     * Validation of uploaded File Type against configured value in GUROCFG table
     * @return
     */
    def fileTypeValidation(fileType) {
        def configuredDocumentTypeList = getRestrictedFileTypes();
        configuredDocumentTypeList.each {
            if (it.equals(fileType)) {
                throw new ApplicationException(UploadDocumentCompositeService, FILE_TYPE_ERROR, 'uploadDocument.file.type.error')
            }
        }
    }
    /**
     * Validation of uploaded File Size against configured value in GUROCFG table
     * @return
     */
    def fileSizeValidation(fileSize) {
        def configuredDocumentSize = getMaxFileSize();
        Long convertedconfiguredDocumentSize = Long.valueOf(configuredDocumentSize.maxFileSize)
        Long ConvertedfileSize = fileSize / SIZE_IN_KB
        if (ConvertedfileSize > convertedconfiguredDocumentSize) {
            throw new ApplicationException(UploadDocumentCompositeService, MAX_SIZE_ERROR, 'uploadDocument.max.file.size.error')
        }
    }
    /**
     * This method is responsible for getting list is attached documents for a response.
     * @param paramsObj
     * @return
     */
    def fetchDocuments( paramsObj ) {
        def user = springSecurityService.getAuthentication()?.user
        paramsObj.pidm = user.pidm
        List<UploadDocument> results = uploadDocumentService.fetchDocuments( paramsObj )
        def resultCount = uploadDocumentService.fetchDocumentsCount( paramsObj )
        results = results.collect {actionItem ->
            [
                    id                  : actionItem.id,
                    documentName        : actionItem.documentName,
                    documentUploadedDate: actionItem.documentUploadedDate
            ]
        }
        [result: results, length: resultCount]

    }
      /**
     * Delete's attached document and its content
     * @param documentId
     * @return
     */
    def deleteDocument( documentId ) {
        boolean success = false
        def message
        try {
            UploadDocumentContent uploadDocumentContent = UploadDocumentContent.fetchContentByFileUploadId( documentId.longValue() )
            uploadDocumentContentService.delete( uploadDocumentContent )
            UploadDocument uploadDocument = uploadDocumentService.get( documentId )
            uploadDocumentService.delete( uploadDocument )
            success = true
            message = MessageHelper.message( 'uploadDocument.delete.success' )
        } catch (ApplicationException e) {
            success = false
            message = e.message
        }
        [
                success: success,
                message: message
        ]

    }
}
