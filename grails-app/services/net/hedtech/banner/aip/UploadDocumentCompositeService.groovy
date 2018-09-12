/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.converters.JSON
import org.springframework.web.multipart.MultipartFile
import net.hedtech.banner.general.configuration.ConfigProperties

/**
 * Class for UploadDocumentCompositeService.
 */
class UploadDocumentCompositeService {

    def springSecurityService
    def uploadDocumentService
    def uploadDocumentContentService

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
        def aipUser = AipControllerUtils.getPersonForAip([studentId: map.studentId], user.pidm)
        println "aipUser $aipUser"
        if (aipUser) {
            UploadDocument ud = new UploadDocument(
                    actionItemId: map.actionItemId,
                    responseId: map.responseId,
                    documentName: map.documentName,
                    documentUploadedDate: new Date(),
                    fileLocation: map.fileLocation,
                    pidm: aipUser.pidm
            )
            println "ud $ud"
            try {
                println "Inside Try block"
                saveUploadDocument = uploadDocumentService.create(ud)
                println "saveUploadDocument $saveUploadDocument"
                println "Map file $map.file"
                uploadDocumentContent(saveUploadDocument.id, map.documentContent)
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
        println 'configpro' $configProperties
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
        println 'configpro' $configProperties
        results = [documentSize: configProperties ? configProperties.configValue : null]
        results
    }

    /**
     * This method is responsible for getting list is attached documents for a response.
     * @param map
     * @return
     */
    def fetchDocuments( map ) {
        def user = springSecurityService.getAuthentication()?.user
        if (!user) {
            throw new ApplicationException( UploadDocumentCompositeService, new BusinessLogicValidationException( 'user.id.not.valid', [] ) )
        }
        List<UploadDocument> results = uploadDocumentService.fetchDocuments( user.pidm, map.actionItemId, map.responseId )
        results = results.collect {actionItem ->
            [
                    id                  : actionItem.id,
                    documentName        : actionItem.documentName,
                    documentUploadedDate: actionItem.documentUploadedDate
            ]
        }
        results
    }

}