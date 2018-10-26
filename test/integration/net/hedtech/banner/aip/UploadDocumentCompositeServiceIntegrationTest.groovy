/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.configuration.ConfigProperties
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

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU004', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        assertNotNull auth
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
        configProperties.configValue = 'AIP'
        configProperties.save(flush: true, failOnError: true)
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
    void testTextSaveUploadDocumentServiceWithBDM() {
        if (bdmEnabled) {
            ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
            assertNotNull configProperties
            configProperties.configValue = 'BDM'
            configProperties.save(flush: true, failOnError: true)
            Long actionItemId = getActionItemId()
            assertNotNull actionItemId
            Long responseId = getResponseIdByActionItemId(actionItemId)
            assertNotNull responseId
            def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true
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
        assert saveResult.message == "Empty Document cannot be uploaded to Banner Document Management."
    }

    //@Test
    void testBDMNotInstalled() {
        if (!bdmEnabled) {
            ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
            assertNotNull configProperties
            configProperties.configValue = 'BDM'
            configProperties.save(flush: true, failOnError: true)
            Long actionItemId = getActionItemId()
            assertNotNull actionItemId
            Long responseId = getResponseIdByActionItemId(actionItemId)
            assertNotNull responseId

            def saveResult = saveUploadDocumentService(actionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == false
            assert saveResult.message == "BDM integration is not available. Please contact your administrator."
        }
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
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.restricted.attachment.type', 'GENERAL_SS')
        assertNotNull configProperties
        configProperties.configValue = '[EXE, ZIP]'
        configProperties.save(flush: true, failOnError: true)
        def result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes == '[EXE, ZIP]'
    }

    @Test
    void testMaxFileSize() {
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.allowed.attachment.max.size', 'GENERAL_SS')
        assertNotNull configProperties
        configProperties.configValue = '1024'
        configProperties.save(flush: true, failOnError: true)
        def result = uploadDocumentCompositeService.getMaxFileSize();
        assertNotNull result
        assert result.maxFileSize == '1024'
    }

    @Test
    void testUploadAttachmentStorageLocation() {
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
        assertNotNull configProperties
        configProperties.configValue = 'AIP'
        configProperties.save(flush: true, failOnError: true)
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
            ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
            assertNotNull configProperties
            configProperties.configValue = 'BDM'
            configProperties.save(flush: true, failOnError: true)
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
            //TODO: Call BDMDeleteDocument
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
            ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
            assertNotNull configProperties
            configProperties.configValue = 'BDM'
            configProperties.save(flush: true, failOnError: true)
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
