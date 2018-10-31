/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.configuration.ConfigProperties
import net.hedtech.banner.general.configuration.ConfigApplication
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.MultipartFile
import net.hedtech.banner.general.person.PersonUtility
import grails.util.Holders

class UploadDocumentCompositeServiceIntegrationTest extends BaseIntegrationTestCase {

    def uploadDocumentCompositeService
    def selfServiceBannerAuthenticationProvider
    def userActionItemReadOnlyCompositeService
    def bdmEnabled = false
    def pidm

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU004', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        assertNotNull auth
        pidm = auth.pidm
        assertNotNull pidm
        setConfigProperties('aip.attachment.file.storage.location', 'AIP', 'string')
        if (bdmEnabled) {
            setConfigProperties('bdmServer.Username', 'BGAIP', 'string')
            setConfigProperties('bdmServer.AXWebServicesURL', 'http://149.24.38.80/AppXtenderServices/axservicesinterface.asmx', 'string')
            setConfigProperties('bdmserver.file.location', 'C:/BDM_DOCUMENTS_FOLDER', 'string')
            setConfigProperties('bdmServer.AXWebAccessURL', 'http://149.24.38.80/appxtender/', 'string')
            setConfigProperties('bdmServer.defaultfile.ext', '[EXE]', 'list')
            setConfigProperties('bdmServer.defaultFileSize', '3', 'string')
            setConfigProperties('bdmserver.BdmDataSource', 'B80H', 'string')
            Holders.config.bdmserver.file.location = 'C:/BDM_DOCUMENTS_FOLDER'
            Holders.config.bdmserver.defaultFileSize='3'
            Holders.config.bdmserver.defaultfile_ext = ['EXE']
        }

    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testTextSaveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
    }

    @Test
    void testAIPFileLocationForDocumentUpload() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                actionItemId : actionItemId.toString(),
                responseId   : responseId.toString(),
                sortColumn   : "id",
                sortAscending: false,
                pidm         : pidm
        ]
        List<UploadDocument> results = UploadDocument.fetchDocuments(paramsObj)
        assert results.size() > 0
        assert results[0].fileLocation == 'AIP'

    }

    @Test
       void testTextSaveUploadDocumentServiceWithBDM() {
           if (bdmEnabled) {
               setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
               Long actionItemId = getActionItemId()
               assertNotNull actionItemId
               Long responseId = getResponseIdByActionItemId(actionItemId)
               assertNotNull responseId
               def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
               assert saveResult.success == true

               def paramsObj = [
                       actionItemId : actionItemId.toString(),
                       responseId   : responseId.toString(),
                       sortColumn   : "id",
                       sortAscending: false,
                       pidm         : pidm
               ]
               List<UploadDocument> results = UploadDocument.fetchDocuments(paramsObj)
               assert results.size() > 0
               //TODO:Uncomment below line when BDM delete is working
               //def deleteResponse = uploadDocumentCompositeService.deleteDocument(results[0].id)
               //assertTrue deleteResponse.success
           }
       }

    @Test
    void testBDMFileLocationForDocumentUpload() {
        if (bdmEnabled) {
            setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
            Long actionItemId = getActionItemId()
            assertNotNull actionItemId
            Long responseId = getResponseIdByActionItemId(actionItemId)
            assertNotNull responseId
            def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true

            def paramsObj = [
                    actionItemId : actionItemId.toString(),
                    responseId   : responseId.toString(),
                    sortColumn   : "id",
                    sortAscending: false,
                    pidm         : pidm
            ]
            List<UploadDocument> results = UploadDocument.fetchDocuments(paramsObj)
            assert results.size() > 0
            assert results[0].fileLocation == 'BDM'
            //TODO:Uncomment below line when BDM delete is working
            //def deleteResponse = uploadDocumentCompositeService.deleteDocument(results[0].id)
            //assertTrue deleteResponse.success
        }
    }
    @Test
    void testAddDocumentEmptyFile() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId

        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIP_Empty_Text_File.txt')
        assert saveResult.success == false
        assert saveResult.message == "Save failed. Empty document can not be uploaded."
    }

    @Test
    void testPdfSaveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFilePdf.pdf')
        assert saveResult.success == true
    }

    @Test
    void testXlssaveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileXLS.xlsx')
        assert saveResult.success == true
    }

    @Test
    void testPptSaveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFilePPT.pptx')
        assert saveResult.success == true
    }

    @Test
    void testZipSaveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileZip.zipx')
        assert saveResult.success == true
    }

    @Test
    void testMp3saveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileMp3.mp3')
        assert saveResult.success == true
    }

    @Test
    void testJpgsaveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileJpg.jpg')
        assert saveResult.success == true
    }

    @Test
    void testDocsaveUploadDocumentService() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileDoc.docx')
        assert saveResult.success == true

    }

    @Test
    void testRestrictedFileTypes() {
        setConfigProperties('aip.restricted.attachment.type', '[EXE, ZIP]', 'list')
        def result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes == '[EXE, ZIP]'
    }

    @Test
    void testMaxFileSize() {
        setConfigProperties('aip.allowed.attachment.max.size', '1024', 'string')
        def result = uploadDocumentCompositeService.getMaxFileSize();
        assertNotNull result
        assert result.maxFileSize == '1024'
    }

    @Test
    void testUploadAttachmentStorageLocation() {
        setConfigProperties('aip.attachment.file.storage.location', 'AIP', 'string')
        def result = uploadDocumentCompositeService.getDocumentStorageSystem();
        assertNotNull result
        assert result.documentStorageLocation == 'AIP'
    }

    @Test
    void testMaximumAttachmentsValidation() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def person = PersonUtility.getPerson("CSRSTU002")
        Map paramsMapObj = [
                actionItemId: "" + actionItemId,
                responseId  : "" + responseId,
                pidm        : person.pidm
        ]
        boolean isMaxAttachmentValidation = uploadDocumentCompositeService.validateMaxAttachments(paramsMapObj)
        assertEquals false, isMaxAttachmentValidation

    }

    @Test
    void testFetchDocuments() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                actionItemId : actionItemId.toString(),
                responseId   : responseId.toString(),
                sortColumn   : "id",
                sortAscending: false
        ]
        def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
        assert response.result.size() > 0
        assert response.length > 0
    }

    @Test
    void testPreviewDocumentsWithAIP() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                actionItemId : actionItemId.toString(),
                responseId   : responseId.toString(),
                sortColumn   : "id",
                sortAscending: false
        ]
        def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
        assert response.result.size() > 0
        assert response.length > 0
        def viewResponse = uploadDocumentCompositeService.previewDocument(response.result[0].id)
        assert response.result[0].id == viewResponse.fileUploadId
        assertNotNull viewResponse.documentContent
        assertTrue viewResponse.success
    }

    @Test
    void testPreviewDocumentsWithBDM() {
        if (bdmEnabled) {
            setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
            Long actionItemId = getActionItemId()
            assertNotNull actionItemId
            Long responseId = getResponseIdByActionItemId(actionItemId)
            assertNotNull responseId
            def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true
            def paramsObj = [
                    actionItemId : actionItemId.toString(),
                    responseId   : responseId.toString(),
                    sortColumn   : "id",
                    sortAscending: false
            ]
            def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
            assert response.result.size() > 0
            assert response.length > 0
            def viewResponse = uploadDocumentCompositeService.previewDocument(response.result[0].id)
            assertNotNull viewResponse.bdmDocument
            assertNotNull viewResponse.bdmDocument.viewURL
            assertTrue viewResponse.success

            //TODO:Uncomment below line when BDM delete is working
            //def deleteResponse = uploadDocumentCompositeService.deleteDocument(response.result[0].id)
            //assertTrue deleteResponse.success
        }
    }

    @Test
    void testDeleteDocument() {
        Long actionItemId = getActionItemId()
        assertNotNull actionItemId
        Long responseId = getResponseIdByActionItemId(actionItemId)
        assertNotNull responseId
        def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                actionItemId : actionItemId.toString(),
                responseId   : responseId.toString(),
                sortColumn   : "id",
                sortAscending: false
        ]
        def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
        assert response.result.size() > 0
        assert response.length > 0
        def deleteResponse = uploadDocumentCompositeService.deleteDocument(response.result[0].id)
        assertTrue deleteResponse.success
    }

    @Test
    void testDeleteDocumentWithBDM() {
        if (bdmEnabled) {
            setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
            Long actionItemId = getActionItemId()
            assertNotNull actionItemId
            Long responseId = getResponseIdByActionItemId(actionItemId)
            assertNotNull responseId
            def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true
            def paramsObj = [
                    actionItemId : actionItemId.toString(),
                    responseId   : responseId.toString(),
                    sortColumn   : "id",
                    sortAscending: false
            ]
            def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
            assert response.result.size() > 0
            assert response.length > 0
            def deleteResponse = uploadDocumentCompositeService.deleteDocument(response.result[0].id)
            assertTrue deleteResponse.success
        }
    }

    private void setConfigProperties(String configName, String configValue, String configType) {
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId(configName, 'GENERAL_SS')
        if (configProperties) {
            configProperties.configValue = configValue
            configProperties.save(flush: true, failOnError: true)
        } else {
            ConfigApplication configApplication = ConfigApplication.fetchByAppId('GENERAL_SS')
            if (!configApplication) {
                configApplication = new ConfigApplication(
                        lastModified: new Date(),
                        appName: 'BannerGeneralSsb',
                        appId: 'GENERAL_SS'
                )
                configApplication.save(failOnError: true, flush: true)
                configApplication = configApplication.refresh()
            }
            configProperties = new ConfigProperties(
                    configName: configName,
                    configType: configType,
                    configValue: configValue,
                    configComment: 'TEST_COMMENT'
            )
            configProperties.setConfigApplication(configApplication)
            configProperties.save(failOnError: true, flush: true)
        }

    }

    private def saveUploadDocumentService(actionItemId, responseId, fileName) {
        MockMultipartFile multipartFile = formFileObject(fileName)
        def result = uploadDocumentCompositeService.addDocument(
                [actionItemId: actionItemId, responseId: responseId, documentName: fileName, documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        result
    }

    private Long getActionItemId() {
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        def group = result.groups.find { it.title == 'Enrollment' }
        def item = group.items.find { it.name == 'Personal Information' }
        Long actionItemId = item.id
        actionItemId
    }

    private Long getResponseIdByActionItemId(Long actionItemId) {
        List<ActionItemStatusRule> responseList = ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId(actionItemId)
        Long responseId = responseList[0].id
        responseId
    }

    /**
     * Form file Object
     */
    private MockMultipartFile formFileObject(filename) {
        File testFile
        try {
            String data = " Test data for integration testing"
            String tempPath = "test/data"
            testFile = new File(tempPath, filename)
            if (!testFile.exists()) {
                testFile.createNewFile()
                FileWriter fileWritter = new FileWriter(testFile, true)
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter)
                bufferWritter.write(data)
                bufferWritter.close()
            }
        } catch (IOException e) {
            throw e
        }
        FileInputStream input = new FileInputStream(testFile);
        MultipartFile multipartFile = new MockMultipartFile("file",
                testFile.getName(), "text/plain", IOUtils.toByteArray(input))
        multipartFile
    }
}
