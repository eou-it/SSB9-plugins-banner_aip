/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.converters.JSON
import net.hedtech.banner.aip.common.LoggerUtility
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase
import org.apache.log4j.Logger
import net.hedtech.banner.aip.UploadDocument
import net.hedtech.banner.aip.UploadDocumentContent
import net.hedtech.banner.general.overall.IntegrationConfiguration
import org.springframework.web.multipart.MultipartFile

import java.text.MessageFormat

/**
 * Class for UploadDocumentCompositeService.
 */
class UploadDocumentCompositeService {

    def springSecurityService
    def uploadDocumentService


    def saveUploadDocument( map ) {

        def user = springSecurityService.getAuthentication()?.user
        def success = false
        def message = null
        UploadDocument saveUploadDocument
        def aipUser = AipControllerUtils.getPersonForAip( [studentId: map.studentId], user.pidm )
        if (aipUser) {
            UploadDocument ud = new UploadDocument(
                    actionItemId: map.actionItemId,
                    responseId: map.responseId,
                    documentName: map.documentName,
                    documentUploadedDate: new Date(),
                    fileLocation: map.fileLocation
            )
        }
        try {
            saveUploadDocument = uploadDocumentService.create( ud )
            uploadDocumentContent( saveUploadDocument.id, map.file )
            success = true
        } catch (Exception e) {
            message = "Error"
        }
        [
                success: success,
                message: message
        ]
    }


    def uploadDocumentContent( id, MultipartFile file ) {
        try {
            byte[] bFile = file.inputStream;
            UploadDocumentContent udc = new UploadDocumentContent(
                    fileUploadId: id,
                    documentContent: bFile
            )
            saveUploadDocumentContent = uploadDocumentService.create( udc )
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    def uploadDocumentType() {
        String documentType = session.getAttribute( "documentType" );
        def results
        if (documentType.equals( null )) {
            results.documentType = IntegrationConfiguration.fetchByProcessCodeAndSettingName( 'GENERAL_SSB', 'ACTION.ITEM.FILE.TYPES' ).value
            session.setAttribute( "documentType", results.documentType )
        } else {
            results.documentType = documentType
        }
        render results as JSON
    }


    def uploadDocumentSize() {
        String documentSize = session.getAttribute( "documentSize" );
        def results
        if (documentSize.equals( null )) {
            results.documentSize = IntegrationConfiguration.fetchByProcessCodeAndSettingName( 'GENERAL_SSB', 'ACTION.ITEM.FILE.SIZE' ).value
            session.setAttribute( "documentSize", results.documentSize )
        } else {
            results.documentSize = documentSize
        }
        render results as JSON
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
