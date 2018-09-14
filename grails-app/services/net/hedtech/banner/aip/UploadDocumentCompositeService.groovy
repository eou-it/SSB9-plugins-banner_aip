/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile
import net.hedtech.banner.general.configuration.ConfigProperties
import net.hedtech.banner.exceptions.ApplicationException
import java.io.File;

/**
 * Class for UploadDocumentCompositeService.
 */
class UploadDocumentCompositeService {

    def springSecurityService
    def uploadDocumentService
    def uploadDocumentContentService
    static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'
    static final String FILE_TYPE_ERROR = '@@r1:FileTypeError@@'

    /**
     * Save uploaded document details in GCRAFLU
     * @param dataMap
     */
    def addUploadDocument(map) {
        println "Entry"
        def user = springSecurityService.getAuthentication() ?.user
        def success = false
        def message = null
        UploadDocument saveUploadDocument
        def fileStorageLocation=uploadDocumentStorageLocation()
        def aipUser = AipControllerUtils.getPersonForAip([studentId: map.studentId], user.pidm)
        println "aipUser $aipUser"
        fileValidation(map.documentName, map.file)
        if (aipUser) {
            UploadDocument ud = new UploadDocument(
                    actionItemId: map.actionItemId,
                    responseId: map.responseId,
                    documentName: map.documentName,
                    documentUploadedDate: new Date(),
                    fileLocation:fileStorageLocation.documentStorageLocation,
                    pidm: aipUser.pidm
            )
            println "ud $ud"
            try {
                println "Inside Try block"
                saveUploadDocument = uploadDocumentService.create(ud)
                println "saveUploadDocument $saveUploadDocument"
                println "Map file $map.file"
                println "FilLoc $fileStorageLocation.documentStorageLocation,"
                if( fileStorageLocation.documentStorageLocation.equals('AIP')) {
                    println "FilLoc1 $fileStorageLocation.documentStorageLocation,"
                    uploadDocumentContent(saveUploadDocument.id, map.file)
                }
                success = true
            } catch (Exception e) {
                println "Exception $e"
                message = "Error"
            }
        }
        [
                success: success,
                message: message
        ]
    }

    /**
     * Save uploaded document in GCRAFCT
     *
     */
    def uploadDocumentContent(id, MultipartFile file) {

        UploadDocumentContent saveUploadDocumentContent
        try {
            println "id $id"
            byte[] bFile = file.getBytes();
            println "bFile $bFile"
            UploadDocumentContent udc = new UploadDocumentContent(
                    fileUploadId: id,
                    documentContent: bFile
            )
            println "udc $udc"
            saveUploadDocumentContent = uploadDocumentContentService.create(udc)
            println "sudc $saveUploadDocumentContent"
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Fetch configured restricted attachment type val
     *
     */
    def uploadDocumentType() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.restricted.attachment.type', 'GENERAL_SS')
        println "configpro $configProperties"
        results = [documentType: configProperties ? configProperties.configValue : null]
        results
    }

    /**
     * Fetch configured allowed attachment max size val
     *
     */
    def uploadDocumentSize() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.allowed.attachment.max.size', 'GENERAL_SS')
        println "configpro $configProperties"
        results = [documentSize: configProperties ? configProperties.configValue : null]
        results
    }
    /**
     * Fetch configured allowed attachment max size val
     *
     */
    def uploadDocumentStorageLocation() {
        def results
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
        println "configpro $configProperties"
        results = [documentStorageLocation: configProperties ? configProperties.configValue : null]
        results
    }
/**
 * Validation for FileType and FileSize
 * @param dataMap
 */
    def fileValidation(fileName,fileContent) {
        println "FileValid $fileName "
        def configureddocumentType = uploadDocumentType();
        def configureddocumentSize = uploadDocumentSize();
        Long convertedDocumentSize=Long.valueOf(configureddocumentSize.documentSize)
        def documentSizeInKB= (fileContent.size / 1024);
        println "size $documentSizeInKB"
        String[] extnArray = fileName.split("\\.")
        println "extnArray $extnArray"
        def extn=extnArray[extnArray.length-1]
        println "file exten $extn"

        if (extn.equals(configureddocumentType.documentType)) {
            throw new ApplicationException( FILE_TYPE_ERROR, 'uploadDocument.file.type.error')
        }
        if (documentSizeInKB > convertedDocumentSize) {
            throw new ApplicationException( MAX_SIZE_ERROR, 'uploadDocument.max.file.size.error')
        }
    }
}