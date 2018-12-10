/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.general.configuration.ConfigProperties
import net.hedtech.banner.i18n.MessageHelper
import org.apache.log4j.Logger
import org.springframework.web.multipart.MultipartFile
import net.hedtech.banner.imaging.BdmUtility
import javax.xml.ws.WebServiceException
import org.apache.commons.codec.binary.Base64
import org.springframework.web.context.request.RequestContextHolder
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.bdm.services.BDMManager
import org.json.JSONObject

/**
 * UploadDocumentCompositeService Class for adding, preview and deleting of uploaded files.
 */
class UploadDocumentCompositeService {

    def springSecurityService
    def uploadDocumentService
    def uploadDocumentContentService
    def actionItemStatusRuleReadOnlyService
    def bdmAttachmentService

    private static final def LOGGER = Logger.getLogger(net.hedtech.banner.aip.UploadDocumentCompositeService.class)

    /**
     * Save uploaded document details in GCRAFLU
     * @param map
     */
    @Transactional
    def addDocument(map) {
        def user = springSecurityService.getAuthentication()?.user
        def success = false
        def message = null
        UploadDocument saveUploadDocument
        def fileStorageLocation = getDocumentStorageSystem()

        UploadDocument ud = new UploadDocument(
                userActionItemId: map.userActionItemId,
                responseId: map.responseId,
                documentName: map.documentName,
                documentUploadedDate: new Date(),
                fileLocation: fileStorageLocation.documentStorageLocation
        )
        try {
            def bdmInstalled = BdmUtility.isBDMInstalled()
            if (!bdmInstalled && fileStorageLocation.documentStorageLocation == AIPConstants.FILE_STORAGE_SYSTEM_BDM ) {
                LOGGER.error('BDM is not installed.')
                message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_BDM_NOT_INSTALLED)
                throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(message, []))
            }
            if (map.file?.isEmpty()) {
                LOGGER.error('File is empty.')
                message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_FILE_EMPTY)
                throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(message, []))
            }
            saveUploadDocument = uploadDocumentService.create(ud)
            switch (ud.fileLocation) {
                case AIPConstants.FILE_STORAGE_SYSTEM_AIP:
                    uploadDocumentContent(saveUploadDocument.id, map.file)
                    success = true
                    message = MessageHelper.message(AIPConstants.MESSAGE_SAVE_SUCCESS)
                    break
                case AIPConstants.FILE_STORAGE_SYSTEM_BDM:
                    addDocumentToBDMServer(map, saveUploadDocument)
                    success = true
                    message = MessageHelper.message(AIPConstants.MESSAGE_SAVE_SUCCESS)
                    break
                default:
                    LOGGER.error('File upload is not configured correctly')
                    success = false
                    message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_UNSUPPORTED_FILE_STORAGE)
            }
        } catch (ApplicationException e) {
            success = false
            message = e.message
        }

        [
                success: success,
                message: message
        ]
    }

    /**
     * View documents uploaded to BDM server
     * @param documentId
     * @return list of documents
     */
    def getBDMDocumentById(def documentId) {
        def message
        def bdmInstalled = BdmUtility.isBDMInstalled()
        LOGGER.debug('vpdiCode: $vpdiCode bdmInstalled: $bdmInstalled')
        if (!bdmInstalled) {
            LOGGER.error('BDM is not installed')
            message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_BDM_NOT_INSTALLED)
            throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(message, []))
        }
        def criteria = [:]
        LOGGER.debug('documentId ' + documentId)
        criteria.put(AIPConstants.DOCUMENT_ID, documentId)
        def documentList
        def bdm = new BDMManager()
        try {
            JSONObject criteriaJson = new JSONObject(criteria)
            JSONObject bdmParams = new JSONObject(BdmUtility.getBdmServerConfigurations())
            documentList = bdm.getDocuments(bdmParams, criteriaJson, vpdiCode)
        } catch (ApplicationException ae) {
            message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_BDM)
            throw new ApplicationException(UploadDocumentCompositeService,
                    new BusinessLogicValidationException(message, []))
        }
        if (documentList.isEmpty()) {
            LOGGER.error("Document not found.")
            message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_BDM_DOCUMENT_NOT_FOUND)
            throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(message, []))
        }
        documentList[0]

    }

    /**
     *   Uploading document to BDM server.
     * @param map
     * @param saveUploadDocument
     */
    private addDocumentToBDMServer(map, UploadDocument saveUploadDocument) {
        def success, message
        String docType = AIPConstants.DEFAULT_DOCTYPE
        String vpdiCode = getVpdiCode()
        try {
            def resultMap = bdmAttachmentService.createBDMLocation(map.file)
            uploadDocToBdmServer(saveUploadDocument.id, docType, resultMap.fileName, resultMap.absoluteFileName, vpdiCode)
            resultMap.userDir.deleteDir()
        } catch (FileNotFoundException e) {
            LOGGER.error('File Not found')
            throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(e.getMessage(), []))
        }
    }

    /**
     * Uploads document to BDM server
     * @param documentId
     * @param docType
     * @param fileName
     * @param absoluteFileName
     * @param vpdiCode
     * @return
     * @throws ApplicationException
     */
    private
    def uploadDocToBdmServer(documentId, docType, fileName, absoluteFileName, vpdiCode) throws ApplicationException {
        def documentAttributes = [:]
        documentAttributes.put(AIPConstants.DOCUMENT_ID, documentId)
        documentAttributes.put(AIPConstants.DOCUMENT_TYPE, docType)
        documentAttributes.put(AIPConstants.DOCUMENT_NAME, fileName)
        if (vpdiCode != null) {
            documentAttributes.put(AIPConstants.VPDI_CODE, vpdiCode)
        }
        try {
            bdmAttachmentService.createDocument(BdmUtility.getBdmServerConfigurations(), absoluteFileName, encodeDocumentAttributes(documentAttributes), vpdiCode)
        } catch (ApplicationException | WebServiceException ae) {
            LOGGER.error('Error while uploading document $ae.message')
            def message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_BDM)
            throw new ApplicationException(UploadDocumentCompositeService,
                    new BusinessLogicValidationException(message, []))
        }
    }

    /**
     * Save uploaded document in GCRAFCT table
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
            LOGGER.error('Error while uploading document content. $e.message')
            throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(e.getMessage(), []))
        }
    }

    /**
     * Gets configured  restricted file types list.
     *
     */
    def getRestrictedFileTypes() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.restricted.attachment.type', 'GENERAL_SS')
        results = [restrictedFileTypes: configProperties ? configProperties.configValue : AIPConstants.DEFAULT_RESTRICTED_FILE_LIST]
    }

    /**
     * Gets configured file size value.
     *
     */
    def getMaxFileSize() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.allowed.attachment.max.size', 'GENERAL_SS')
        results = [maxFileSize: configProperties ? configProperties.configValue : null]
    }

    /**
     * Gets configured file storage system.
     *
     */
    def getDocumentStorageSystem() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
        results = [documentStorageLocation: configProperties ? configProperties.configValue : AIPConstants.FILE_STORAGE_SYSTEM_AIP]
    }

    /**
     * This method is responsible for getting list is attached documents for a response.
     * @param paramsObj
     * @return
     */
    def fetchDocuments(paramsObj) {
        List<UploadDocument> results = uploadDocumentService.fetchDocuments(paramsObj)
        def resultCount = uploadDocumentService.fetchDocumentsCount(paramsObj)
        results = results.collect { actionItem ->
            [
                    id                  : actionItem.id,
                    documentName        : actionItem.documentName,
                    documentUploadedDate: actionItem.documentUploadedDate,
                    fileLocation        : actionItem.fileLocation
            ]
        }
        [result: results, length: resultCount]
    }

    /**
     * Delete's attached document and its content
     * @param documentId
     * @return
     */
    def deleteDocument(documentId) {
        boolean success
        def message
        try {
            def user = springSecurityService.getAuthentication()?.user
            UploadDocument uploadDocument = uploadDocumentService.get(documentId)
            if (uploadDocument.fileLocation == AIPConstants.FILE_STORAGE_SYSTEM_BDM) {
                def documentAttributes = [:]
                documentAttributes.put(AIPConstants.DOCUMENT_ID, documentId)
                bdmAttachmentService.deleteDocument(BdmUtility.getBdmServerConfigurations(), documentAttributes, getVpdiCode())
            } else {
                UploadDocumentContent uploadDocumentContent = UploadDocumentContent.fetchContentByFileUploadId(documentId.longValue())
                uploadDocumentContentService.delete(uploadDocumentContent)
            }
            uploadDocumentService.delete(uploadDocument)
            success = true
            message = MessageHelper.message('uploadDocument.delete.success')
        } catch (ApplicationException e) {
            success = false
            message = e.message
        }
        [
                success: success,
                message: message
        ]

    }

    /**
     * Maxumum attachments validation
     * @param paramsMap [responseId , userActionItemId]
     * @return validation flag
     */
    public boolean validateMaxAttachments(paramsMapObj) {
        ActionItemStatusRuleReadOnly actionItemStatusRule = actionItemStatusRuleReadOnlyService.getActionItemStatusRuleROById(Long.parseLong(paramsMapObj.responseId))
        if (actionItemStatusRule?.statusAllowedAttachment > 0) {
            def resultCount = uploadDocumentService.fetchDocumentsCount(paramsMapObj)
            return resultCount <= actionItemStatusRule.statusAllowedAttachment
        }
        return false
    }

/**
 * This method is reponsible for getting MEP code from session attribute.
 * @return
 */
    private def getVpdiCode() {
        def session = RequestContextHolder?.currentRequestAttributes()?.request?.session
        session.getAttribute('mep')
    }

    /**
     * This method is responsible for encoding document attributes
     * @param documentAttributes
     * @return
     */
    def encodeDocumentAttributes(documentAttributes) {
        documentAttributes.each { entry ->
            if (entry.value && entry.value instanceof String) {
                entry.value = entry.value.encodeAsHTML()
            }
        }
        documentAttributes
    }
    /**
     * Preview of Document
     * @param paramsObj
     * @return
     */
    def previewDocument(paramsObj) {
        def fileLocation
        if (paramsObj.fileLocation) {
            fileLocation = paramsObj.fileLocation
        } else {
            fileLocation = uploadDocumentService.fetchFileLocationById(paramsObj.documentId)
        }
        def documentDetails = [:]
        try {
            if (fileLocation == AIPConstants.FILE_STORAGE_SYSTEM_BDM) {
                documentDetails.bdmDocument = getBDMDocumentById(paramsObj.documentId)
            } else {
                UploadDocumentContent result = uploadDocumentContentService.fetchContentByFileUploadId(paramsObj.documentId)
                def base64EncodedDocContent = Base64.encodeBase64String(result.documentContent)
                documentDetails.id = result.id
                documentDetails.fileUploadId = result.fileUploadId
                documentDetails.documentContent = base64EncodedDocContent
            }
            documentDetails.success = true
        } catch (ApplicationException e) {
            documentDetails.success = false
            documentDetails.message = e.message
        }
        documentDetails
    }

}
