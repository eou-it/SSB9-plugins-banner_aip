/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.general.configuration.ConfigProperties
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.security.BannerGrantedAuthorityService
import org.apache.log4j.Logger
import org.jenkinsci.plugins.clamav.scanner.ScanResult
import org.springframework.web.multipart.MultipartFile
import net.hedtech.banner.imaging.BdmUtility
import javax.xml.ws.WebServiceException
import org.apache.commons.codec.binary.Base64
import org.springframework.web.context.request.RequestContextHolder
import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.bdm.services.BDMManager
import org.json.JSONObject
import org.jenkinsci.plugins.clamav.scanner.ClamAvScanner
import grails.util.Holders

/**
 * UploadDocumentCompositeService Class for adding, preview and deleting of uploaded files.
 */
@Transactional
class UploadDocumentCompositeService {

    def springSecurityService
    def uploadDocumentService
    def uploadDocumentContentService
    def actionItemStatusRuleReadOnlyService
    def bdmAttachmentService
    def aipClamavService
    def userActionItemService

    private static final def LOGGER = Logger.getLogger(net.hedtech.banner.aip.UploadDocumentCompositeService.class)

    /**
     * Save uploaded document details in GCRAFLU
     * @param map
     */

    def addDocument(map) {
        boolean success = false
        String message = null
        UploadDocument saveUploadDocument = null
        def fileStorageLocation = getDocumentStorageSystem()

        UploadDocument ud = new UploadDocument(
                userActionItemId: map.userActionItemId,
                responseId: map.responseId,
                documentName: map.documentName,
                documentUploadedDate: new Date(),
                fileLocation: fileStorageLocation.documentStorageLocation
        )
        try {
            if (!ud.validate() && ud.hasErrors() && ud.errors.hasFieldErrors(AIPConstants.DOCUMENTNAME)) {
                def codes = ud.errors.getFieldError(AIPConstants.DOCUMENTNAME).codes
                if (codes.contains(AIPConstants.ERROR_DOCUMENT_NAME_MAXSIZE_EXCEEDED)) {
                    message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_FILENAME_TOO_LONG)
                    throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(message, []))
                }
            }
            def bdmInstalled = BdmUtility.isBDMInstalled()
            if (!bdmInstalled && fileStorageLocation.documentStorageLocation == AIPConstants.FILE_STORAGE_SYSTEM_BDM) {
                LOGGER.error('BDM is not installed.')
                message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_BDM_NOT_INSTALLED)
                throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(message, []))
            }
            if (!map.file || map.file?.isEmpty()) {
                LOGGER.error('File is null or empty.')
                message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_FILE_EMPTY)
                throw new ApplicationException(UploadDocumentCompositeService, new BusinessLogicValidationException(message, []))
            }
            if (aipClamavService.isClamavEnabled()) {
                scanDocuments(map.file)
            }
            switch (ud.fileLocation) {
                case AIPConstants.FILE_STORAGE_SYSTEM_AIP:
                    saveUploadDocument = uploadDocumentService.create(ud)
                    uploadDocumentContent(saveUploadDocument.id, map.file)
                    success = true
                    message = MessageHelper.message(AIPConstants.MESSAGE_SAVE_SUCCESS)
                    break
                case AIPConstants.FILE_STORAGE_SYSTEM_BDM:
                    saveUploadDocument = uploadDocumentService.create(ud)
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
            if (saveUploadDocument) {
                //delete entry from GCRAFLU
                uploadDocumentService.delete(saveUploadDocument)
            }
            success = false
            message = e.message
        }

        [
                success: success,
                message: message
        ]
    }
    /**
     * Scan documents using ClamAV antivirus scanner
     * @param file
     */
    void scanDocuments(MultipartFile file) {
        ClamAvScanner scanner = aipClamavService.initScanner()
        ScanResult scanResult = aipClamavService.fileScanner(scanner, file.inputStream)
        switch (scanResult?.getStatus()) {
            case ScanResult.Status.INFECTED:
                LOGGER.error('Scan Result:' + scanResult.getMessage())
                String message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_VIRUS_FOUND)
                throw new ApplicationException(UploadDocumentCompositeService,
                        new BusinessLogicValidationException(message, []))
                break
            case ScanResult.Status.WARNING:
                LOGGER.error('Scan Result:' + scanResult.getMessage())
                String message = MessageHelper.message(AIPConstants.ERROR_MESSAGE_VIRUS_SCAN_FAILED)
                throw new ApplicationException(UploadDocumentCompositeService,
                        new BusinessLogicValidationException(message, []))
                break
            case ScanResult.Status.PASSED:
                LOGGER.info('Scan Passed!')
                break
        }
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
        String docType = AIPConstants.DEFAULT_DOCTYPE
        String vpdiCode = getVpdiCode()
        try {
            def resultMap = bdmAttachmentService.createBDMLocation(map.file)
            uploadDocToBdmServer(saveUploadDocument.id, docType, resultMap.fileName, resultMap.absoluteFileName, vpdiCode)
            resultMap.userDir.deleteDir()
        } catch (Exception e) {
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
        } catch (Exception e) {
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
        def mepCode=getVpdiCode()
        def configValue =Holders.config.aip.restricted.attachment.type
        def configValueList=[]

        configValue.each() {
            it = it.replaceAll("\"", "");
            configValueList.push(it.toUpperCase())
        }

        if (configValueList.size() > 0 && !(configValueList.contains(AIPConstants.DEFAULT_RESTRICTED_FILE_TYPE)) ) {
            configValueList.add(configValueList.size(),AIPConstants.DEFAULT_RESTRICTED_FILE_TYPE)
        }
        if (configValueList.size() == 0) {
            configValueList = AIPConstants.DEFAULT_RESTRICTED_FILE_TYPE
        }
        results = [restrictedFileTypes: configValueList]
    }

    /**
     * Gets configured file size value.
     *
     */
    def getMaxFileSize() {
        def results
        def mepCode=getVpdiCode()
        def configValue =Holders.config.aip.allowed.attachment.max.size
        results = [maxFileSize: configValue ? configValue : null]
    }

    /**
     * Gets configured file storage system.
     *
     */
    def getDocumentStorageSystem() {
        def results
        def mepCode=getVpdiCode()
        def configValue =Holders.config.aip.attachment.file.storage.location
        results = [documentStorageLocation: configValue ? configValue :  AIPConstants.FILE_STORAGE_SYSTEM_AIP]
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
            UploadDocument uploadDocument = uploadDocumentService.get(documentId)
            validateUser(uploadDocument.userActionItemId)
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
        ActionItemStatusRuleReadOnly actionItemStatusRule = actionItemStatusRuleReadOnlyService.getActionItemStatusRuleROById(paramsMapObj.responseId)
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

        def documentDetails = [:]
        try {
            UploadDocument uploadDocument = uploadDocumentService.get(paramsObj.documentId)
            def authorities = BannerGrantedAuthorityService.getAuthorities()
            def userAuthorities = authorities?.collect { it.objectName }
            boolean isReviewer = userAuthorities?.contains(AIPConstants.ACTIONITEMREVIEWER_ROLE)
            if (!isReviewer) {
                validateUser(uploadDocument.userActionItemId)
            }
            if (uploadDocument.fileLocation == AIPConstants.FILE_STORAGE_SYSTEM_BDM) {
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
    /**
     * Validate user to ensure user operates only on his documents.
     * @param userActionItemId
     */
    private void validateUser(Long userActionItemId) {
        def pidm = userActionItemService.getUserActionItemPidmById(userActionItemId)
        def user = springSecurityService.getAuthentication()?.user
        if (user.pidm != pidm) {
            throw new ApplicationException(UploadDocumentCompositeService, '@@r1:Invalid user@@')
        }

    }
}
