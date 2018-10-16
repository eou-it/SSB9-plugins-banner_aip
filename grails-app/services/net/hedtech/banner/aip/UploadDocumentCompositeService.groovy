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
import grails.util.Holders
import net.hedtech.banner.imaging.BdmUtility
import javax.xml.ws.WebServiceException
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.context.request.RequestContextHolder
import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.aip.common.AIPConstants

/**
 * Class for UploadDocumentCompositeService.
 */
class UploadDocumentCompositeService {

    def springSecurityService
    def uploadDocumentService
    def uploadDocumentContentService
    def actionItemStatusRuleReadOnlyService
    def bdmAttachmentService

    private static final def LOGGER = Logger.getLogger( this.getClass() )

    /**
     * Save uploaded document details in GCRAFLU
     * @param dataMap
     */
    @Transactional
    def addUploadDocument( map ) {
        def user = springSecurityService.getAuthentication()?.user
        def success = false
        def message = null
        UploadDocument saveUploadDocument
        def fileStorageLocation = getDocumentStorageSystem()
        def aipUser = AipControllerUtils.getPersonForAip( [studentId: map.studentId], user.pidm )
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
                saveUploadDocument = uploadDocumentService.create( ud )
                if (AIPConstants.DEFAULT_FILE_STORAGE_SYSTEM_AIP.equals( ud.fileLocation  )) {
                    uploadDocumentContent( saveUploadDocument.id, map.file )
                } else if (AIPConstants.DEFAULT_FILE_STORAGE_SYSTEM_BDM.equals( ud.fileLocation )) {
                    uploadDocumentToBDMServer( map, saveUploadDocument )
                }
                success = true
                message = MessageHelper.message( AIPConstants.MESSAGE_BDM_SAVE)
            } catch (ApplicationException e) {
                success = false
                message = MessageHelper.message( AIPConstants.ERROR_MESSAGE_BDM_SAVE  )
            }
        }
        [
                success: success,
                message: message
        ]
    }

    /**
     *  This method is responsible for uploading document to BDM server.
     * @param map
     * @param saveUploadDocument
     */
    private uploadDocumentToBDMServer( map, UploadDocument saveUploadDocument ) {
        def success, message
        String docType = AIPConstants.DEFAULT_DOCTYPE
        String vpdiCode = getVpdiCode()
        def bdmInstalled = BdmUtility.isBDMInstalled()
        LoggerUtility.debug( LOGGER, 'vpdiCode ' + vpdiCode + 'bdmInstalled ' + bdmInstalled + 'docType ' + docType )
        if (!bdmInstalled) {
            success = false
            message = MessageHelper.message( AIPConstants.ERROR_MESSAGE_BDM_NOT_INSTALLED )
            LoggerUtility.error( LOGGER, 'bdm.not.installed' )
            throw new ApplicationException( UploadDocumentCompositeService, new BusinessLogicValidationException( AIPConstants.ERROR_MESSAGE_BDM_NOT_INSTALLED, [] ) )
        }
        if (map.file?.isEmpty()) {
            success = false
            message = MessageHelper.message( AIPConstants.ERROR_BDM_FILE_UPLOAD )
            LoggerUtility.error( LOGGER, 'Document File to upload Doc type ' + docType + ' is empty' )
            throw new ApplicationException( UploadDocumentCompositeService, new BusinessLogicValidationException( AIPConstants.ERROR_BDM_FILE_UPLOAD, [] ) )
        }
        try {
            def resultMap = bdmAttachmentService.createBDMLocation( map.file )
            uploadDocToBdmServer( saveUploadDocument.id, docType, resultMap.fileName, resultMap.absoluteFileName, vpdiCode )
            resultMap.userDir.deleteDir()
        } catch (FileNotFoundException e) {
            LoggerUtility.error( LOGGER, 'File Not found' )
            success = false
            message = MessageHelper.message( AIPConstants.ERROR_MESSAGE_BDM_SAVE )
            throw new ApplicationException( UploadDocumentCompositeService, new BusinessLogicValidationException( e.getMessage(), [] ) )
        }
    }

    /**
     * Uploads document to BDM server
     * @param saveUploadDocument
     * @param docType
     * @param ownerPidm
     * @param fileName
     * @param absoluteFileName
     * @param vpdiCode
     * @return
     * @throws ApplicationException
     */
    private
    def uploadDocToBdmServer( documentId, docType, fileName, absoluteFileName, vpdiCode ) throws ApplicationException {
        def documentAttributes = [:]
        //Empty value is set to few fields as we need to change them when we create the fields at BDM forms.
        documentAttributes.put( AIPConstants.DOCUMENT_ID, documentId )
        documentAttributes.put( AIPConstants.DOCUMENT_TYPE, docType )
        documentAttributes.put( AIPConstants.DOCUMENT_NAME, fileName )
        if (vpdiCode != null) {
            documentAttributes.put( AIPConstants.VPDI_CODE, vpdiCode )
        }
        try {
            bdmAttachmentService.createDocument( getBdmParams(), absoluteFileName, encodeDocumentAttributes( documentAttributes ), vpdiCode )
        } catch (ApplicationException | WebServiceException ae) {
            LoggerUtility.error( LOGGER, 'Error while uploading document' + ae.message )
            println( ae.message )
            throw new ApplicationException( UploadDocumentCompositeService,
                                            new BusinessLogicValidationException( AIPConstants.ERROR_MESSAGE_BDM, [] ) )
        }
    }

    /**
     * Gets BDM Parameters
     * @return
     */
    private LinkedHashMap getBdmParams() {
        LinkedHashMap bdmParams = new HashMap()
        print( Holders.config.bdmserver )
        Holders.config.bdmserver.each {

            k, v -> bdmParams.put( k, v )
        }
        bdmParams.put( "KeyPassword", getPassword( bdmParams ) )
        LoggerUtility.debug( LOGGER, "BDMParams :: " + bdmParams )
        return bdmParams
    }

    /**
     * Decrypts the BDM encrypted password
     * @param encryptedString
     * @return
     */
    private def decryptString( encryptedString ) {
        try {
            BdmUtility.decryptString( encryptedString )
        }
        catch (ApplicationException ae) {
            LoggerUtility.error( LOGGER, 'Error while getting the decrypted BDM password' + ae.message )
            throw new ApplicationException( UploadDocumentCompositeService,
                                            new BusinessLogicValidationException( AIPConstants.ERROR_MESSAGE_BDM, [] ) )
        }
    }

    /**
     * Gets BDM Crypto Key
     * @return
     */
    private def getPassword( LinkedHashMap bdmConfig ) {
        try {
            BdmUtility.getPassword( bdmConfig )
        }
        catch (ApplicationException ae) {
            LoggerUtility.error( LOGGER, 'Error while getting BDM Crypto key.' + ae.message )
            throw new ApplicationException( UploadDocumentCompositeService,
                                            new BusinessLogicValidationException( AIPConstants.ERROR_MESSAGE_BDM, [] ) )
        }
    }

    /**
     * Save uploaded document in GCRAFCT
     *
     */
    def uploadDocumentContent( id, MultipartFile file ) {

        UploadDocumentContent saveUploadDocumentContent
        try {
            byte[] bFile = file.getBytes();
            UploadDocumentContent udc = new UploadDocumentContent(
                    fileUploadId: id,
                    documentContent: bFile
            )
            saveUploadDocumentContent = uploadDocumentContentService.create( udc )
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
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId( 'aip.restricted.attachment.type', 'GENERAL_SS' )
        results = [restrictedFileTypes: configProperties ? configProperties.configValue : AIPConstants.DEFAULT_RESTRICTED_FILE_LIST]
        results
    }

    /**
     * Fetch configured allowed attachment max size val
     *
     */
    def getMaxFileSize() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId( 'aip.allowed.attachment.max.size', 'GENERAL_SS' )
        results = [maxFileSize: configProperties ? configProperties.configValue : null]
        results
    }

    /**
     * Fetch configured allowed attachment max size val
     *
     */
    def getDocumentStorageSystem() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId( 'aip.attachment.file.storage.location', 'GENERAL_SS' )
        results = [documentStorageLocation: configProperties ? configProperties.configValue : AIPConstants.DEFAULT_FILE_STORAGE_SYSTEM]
        results
    }

    /**
     * Validation of uploaded File Type against configured value in GUROCFG table
     * @return
     */
    def fileTypeValidation( fileType ) {
        def configuredDocumentTypeList = getRestrictedFileTypes();
        configuredDocumentTypeList.each {
            if (it.equals( fileType )) {
                throw new ApplicationException( UploadDocumentCompositeService, AIPConstants.FILE_TYPE_ERROR, 'uploadDocument.file.type.error' )
            }
        }
    }

    /**
     * Validation of uploaded File Size against configured value in GUROCFG table
     * @return
     */
    def fileSizeValidation( fileSize ) {
        def configuredDocumentSize = getMaxFileSize();
        Long convertedconfiguredDocumentSize = Long.valueOf( configuredDocumentSize.maxFileSize )
        Long ConvertedfileSize = fileSize / AIPConstants.SIZE_IN_KB
        if (ConvertedfileSize > convertedconfiguredDocumentSize) {
            throw new ApplicationException( UploadDocumentCompositeService, AIPConstants.MAX_SIZE_ERROR, 'uploadDocument.max.file.size.error' )
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

    /**
     * Maxumum attachments validation
     * @param paramsMap [responseId ,actionItemId and pidm]
     * @return validation flag
     */
    public boolean maximumAttachmentsValidation( paramsMapObj ) {
        def actionItemStatusRule = actionItemStatusRuleReadOnlyService.getActionItemStatusRuleROById( Long.parseLong( paramsMapObj.responseId ) )
        if (actionItemStatusRule?.statusAllowedAttachment > 0) {
            def resultCount = uploadDocumentService.fetchDocumentsCount( paramsMapObj )
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
        def mepCode = session.getAttribute( 'mep' )
        return mepCode;
    }

    /**
     * This method is responsible for encoding document attributes
     * @param documentAttributes
     * @return
     */
    def encodeDocumentAttributes( documentAttributes ) {
        documentAttributes.each {entry ->
            if (entry.value && entry.value instanceof String) {
                entry.value = entry.value.encodeAsHTML()
            }
        }
        return documentAttributes
    }
}
