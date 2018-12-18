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
    def userActionItemId
    def responseId

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
            Holders.config.bdmserver.defaultFileSize = '3'
            Holders.config.bdmserver.defaultfile_ext = ['EXE']
        }
        getUserActionItemIdAndResponseId()

    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testTextSaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
    }

    @Test
    void testAIPFileLocationForDocumentUpload() {

        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId.toString(),
                responseId      : responseId.toString(),
                sortColumn      : "id",
                sortAscending   : false
        ]
        List<UploadDocument> results = UploadDocument.fetchDocuments(paramsObj)
        assert results.size() > 0
        assert results[0].fileLocation == 'AIP'
    }

    @Test
    void testTextSaveUploadDocumentServiceWithBDM() {
        if (bdmEnabled) {
            setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true

            def paramsObj = [
                    userActionItemId: userActionItemId.toString(),
                    responseId      : responseId.toString(),
                    sortColumn      : "id",
                    sortAscending   : false
            ]
            List<UploadDocument> results = UploadDocument.fetchDocuments(paramsObj)
            assert results.size() > 0

            def deleteResponse = uploadDocumentCompositeService.deleteDocument(results[0].id)
            assertTrue deleteResponse.success
        }
    }

    @Test
    void testBDMFileLocationForDocumentUpload() {
        if (bdmEnabled) {
            setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true

            def paramsObj = [
                    userActionItemId: userActionItemId.toString(),
                    responseId      : responseId.toString(),
                    sortColumn      : "id",
                    sortAscending   : false
            ]
            List<UploadDocument> results = UploadDocument.fetchDocuments(paramsObj)
            assert results.size() > 0
            assert results[0].fileLocation == 'BDM'

            def deleteResponse = uploadDocumentCompositeService.deleteDocument(results[0].id)
            assertTrue deleteResponse.success
        }
    }

    @Test
    void testAddDocumentEmptyFile() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIP_Empty_Text_File.txt')
        assert saveResult.success == false
        assert saveResult.message == "Save failed. Empty document can not be uploaded."
    }

    @Test
    void testPdfSaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFilePdf.pdf')
        assert saveResult.success == true
    }

    @Test
    void testXlssaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileXLS.xlsx')
        assert saveResult.success == true
    }

    @Test
    void testPptSaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFilePPT.pptx')
        assert saveResult.success == true
    }

    @Test
    void testZipSaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileZip.zipx')
        assert saveResult.success == true
    }

    @Test
    void testMp3saveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileMp3.mp3')
        assert saveResult.success == true
    }

    @Test
    void testJpgsaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileJpg.jpg')
        assert saveResult.success == true
    }

    @Test
    void testDocsaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileDoc.docx')
        assert saveResult.success == true

    }

    @Test
    void testRestrictedFileTypes() {
        setConfigProperties('aip.restricted.attachment.type', '[EXE, ZIP]', 'list')
        def result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes == '[EXE, ZIP]'

        //when EXE is not in config, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', '[ZIP]', 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes == '[ZIP, EXE]'

        //when config value is null, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', null, 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes == '[EXE]'

        //when config does not have any value, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', '[]', 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes == '[, EXE]'

        //when config does not have any value, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', "", 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes == '[EXE]'
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
        Map paramsMapObj = [
                userActionItemId: "" + userActionItemId,
                responseId      : "" + responseId
        ]
        boolean isMaxAttachmentValidation = uploadDocumentCompositeService.validateMaxAttachments(paramsMapObj)
        assertEquals false, isMaxAttachmentValidation

    }

    @Test
    void testFetchDocuments() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId.toString(),
                responseId      : responseId.toString(),
                sortColumn      : "id",
                sortAscending   : false
        ]
        def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
        assert response.result.size() > 0
        assert response.length > 0
    }

    @Test
    void testPreviewDocumentsWithAIP() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId.toString(),
                responseId      : responseId.toString(),
                sortColumn      : "id",
                sortAscending   : false
        ]
        def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
        assert response.result.size() > 0
        assert response.length > 0
        def inputParams = [
                documentId: response.result[0].id
        ]
        def viewResponse = uploadDocumentCompositeService.previewDocument(inputParams)
        assert response.result[0].id == viewResponse.fileUploadId
        assertNotNull viewResponse.documentContent
        assertTrue viewResponse.success
    }

    @Test
    void testPreviewDocumentsWithBDM() {
        if (bdmEnabled) {
            setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true
            def paramsObj = [
                    userActionItemId: userActionItemId.toString(),
                    responseId      : responseId.toString(),
                    sortColumn      : "id",
                    sortAscending   : false
            ]
            def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
            assert response.result.size() > 0
            assert response.length > 0
            def inputParams = [
                    documentId: response.result[0].id
            ]
            def viewResponse = uploadDocumentCompositeService.previewDocument(inputParams)
            assertNotNull viewResponse.bdmDocument
            assertNotNull viewResponse.bdmDocument.viewURL
            assertTrue viewResponse.success

            def deleteResponse = uploadDocumentCompositeService.deleteDocument(response.result[0].id)
            assertTrue deleteResponse.success
        }
    }

    @Test
    void testDeleteDocument() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId.toString(),
                responseId      : responseId.toString(),
                sortColumn      : "id",
                sortAscending   : false
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
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true
            def paramsObj = [
                    userActionItemId: userActionItemId.toString(),
                    responseId      : responseId.toString(),
                    sortColumn      : "id",
                    sortAscending   : false
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

    private def saveUploadDocumentService(userActionItemId, responseId, fileName) {
        MockMultipartFile multipartFile = formFileObject(fileName)
        def result = uploadDocumentCompositeService.addDocument(
                [userActionItemId: userActionItemId, responseId: responseId, documentName: fileName, documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        result
    }

    private void getUserActionItemIdAndResponseId() {
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        def group = result.groups.find { it.title == 'Enrollment' }
        def item = group.items.find { it.name == 'Personal Information' }
        Long actionItemId = item.id

        List<UserActionItem> gcraactIdList = UserActionItem.fetchUserActionItemsByPidm(pidm.longValue())
        UserActionItem gcraact = gcraactIdList.find { it.actionItemId = actionItemId }
        userActionItemId = gcraact.id

        List<ActionItemStatusRule> responsesList = ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId(actionItemId)
        ActionItemStatusRule response = responsesList.find { it.labelText == 'Not in my life time.' }
        responseId = response.id
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
