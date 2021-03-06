/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
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
import grails.util.Holders

@Integration
@Rollback
class UploadDocumentCompositeServiceIntegrationTest extends BaseIntegrationTestCase {

    def uploadDocumentCompositeService
    def selfServiceBannerAuthenticationProvider
    def userActionItemReadOnlyCompositeService
    def bdmEnabled = false
    def clamavEnabled = false
    def pidm
    def userActionItemId
    def responseId

    @Before
    void setUp() {
        formContext = ['SELFSERVICE']
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
            Holders.config.bdmserver.defaultFileSize = 3
            Holders.config.bdmserver.defaultfile_ext = ['EXE']
        }
        Holders.config.clamav.enabled = false
        getUserActionItemIdAndResponseId()

    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testFileScanningSuccess() {
        if (clamavEnabled) {
            configureClamAV()
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true
        }
    }

    @Test
    void testFileScanDefaultConfigSuccess() {
        if (clamavEnabled) {
            configureClamAV()
            Holders.config.clamav.host = null
            Holders.config.clamav.port = null
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == true
        }
    }

    @Test
    void testFileScanningFailure() {
        if (clamavEnabled) {
            configureClamAV()
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'eicar_com.zip')
            assert saveResult.success == false
            assert saveResult.message == "Save failed. Virus detected in selected file."
        }
    }

    @Test
    void testFileScanDisabled() {
        if (clamavEnabled) {
            configureClamAV()
            Holders.config.clamav.enabled = false
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'eicar_com.zip')
            assert saveResult.success == true
        }
    }

    @Test
    void testFileScanWithWarning() {
        if (clamavEnabled) {
            configureClamAV()
            Holders.config.clamav.connectionTimeout = 1
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
            assert saveResult.success == false
            assert saveResult.message == "Unable to perform virus scan. Please contact your administrator."
        }
    }

    @Test
    void testTextSaveUploadDocumentService() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
    }

    @Test
    void testUserValidation() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
                sortColumn      : "id",
                sortAscending   : false
        ]
        def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
        assert response.result.size() > 0
        assert response.length > 0
        def documentId1 = response.result[0].id
        logout()
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU001', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        assertNotNull auth

        def inputParams = [
                documentId: documentId1
        ]
        try {
            def viewResponse = uploadDocumentCompositeService.previewDocument(inputParams)
        } catch (ApplicationException ae) {
            assertApplicationException ae, "Invalid user"
        }
    }

    @Test
    void testUserValidationForReviewer() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
                sortColumn      : "id",
                sortAscending   : false
        ]
        def response = uploadDocumentCompositeService.fetchDocuments(paramsObj)
        assert response.result.size() > 0
        assert response.length > 0
        def documentId1 = response.result[0].id
        logout()
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('AIPADM002', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)

        def inputParams = [
                documentId: documentId1
        ]
        def viewResponse = uploadDocumentCompositeService.previewDocument(inputParams)
        assert documentId1 == viewResponse.fileUploadId
        assertNotNull viewResponse.documentContent
        assertTrue viewResponse.success
    }

    @Test
    void testAIPFileLocationForDocumentUpload() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
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
    void testBDMFileSizeLessThanAIP() {
        if (bdmEnabled) {
            setConfigProperties('aip.attachment.file.storage.location', 'BDM', 'string')
            Holders.config.bdmserver.defaultFileSize = 1
            setConfigProperties('aip.allowed.attachment.max.size', '26214400', 'integer')
            def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'BDM Install Guide.pdf')
            assert saveResult.success == false
            assert saveResult.message == "File size exceeding"
        }
    }

    @Test
    void testAddDocumentEmptyFile() {
        def saveResult = saveEmptyFileUploadDocumentService(userActionItemId, responseId, 'AIP_Empty_Text_File.txt')
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
    void testUploadDocumentServiceWithLongFileName() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'SixtyCharacterLongSampleFileToTestAipDocumentUploadFunctionality.txt')
        assert saveResult.success == false
        assert saveResult.message == "aip.uploadDocument.file.name.length.error"
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
        assert result.restrictedFileTypes.toString().indexOf("EXE") != -1
        assert result.restrictedFileTypes.toString().indexOf("ZIP") != -1

        //when EXE is not in config, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', '[ZIP]', 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes.toString().indexOf("EXE") != -1
        assert result.restrictedFileTypes.toString().indexOf("ZIP") != -1

        //when config value is null, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', null, 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes.toString().indexOf("EXE") != -1

        //when config does not have any value, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', '[]', 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes.toString().indexOf("EXE") != -1

        //when config does not have any value, need to add to the list
        setConfigProperties('aip.restricted.attachment.type', "", 'list')
        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes.toString().indexOf("EXE") != -1

        //configuration not done in ConfigProperties
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.restricted.attachment.type', 'GENERAL_SS')
        configProperties?.delete(flush: true)

        result = uploadDocumentCompositeService.getRestrictedFileTypes()
        assertNotNull result
        assert result.restrictedFileTypes.toString().indexOf("EXE") != -1
    }

    @Test
    void testMaxFileSize() {
        setConfigProperties('aip.allowed.attachment.max.size', '1024', 'string')
        def result = uploadDocumentCompositeService.getMaxFileSize()
        assertNotNull result
        assert result.maxFileSize == '1024'
    }

    //TODO: Set default max file size in uploadDocumentCompositeService
    @Test
    void testDefaultMaxFileSize() {
        setConfigProperties('aip.allowed.attachment.max.size', null, 'string')
        def result = uploadDocumentCompositeService.getMaxFileSize()
        assertNotNull result
        assertNull result.maxFileSize

        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.allowed.attachment.max.size', 'GENERAL_SS')
        configProperties?.delete(flush: true)

        result = uploadDocumentCompositeService.getMaxFileSize();
        assertNotNull result
        assertNull result.maxFileSize

    }

    @Test
    void testUploadAttachmentStorageLocation() {
        setConfigProperties('aip.attachment.file.storage.location', 'AIP', 'string')
        def result = uploadDocumentCompositeService.getDocumentStorageSystem()
        assertNotNull result
        assert result.documentStorageLocation == 'AIP'
    }

    @Test
    void testDefaultUploadAttachmentStorageLocation() {
        setConfigProperties('aip.attachment.file.storage.location', null, 'string')
        def result = uploadDocumentCompositeService.getDocumentStorageSystem()
        assertNotNull result
        assertNull result.documentStorageLocation
        //TODO: Set documentStorageLocation to AIP when config property value is null.
        //assert result.documentStorageLocation == 'AIP'

        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.attachment.file.storage.location', 'GENERAL_SS')
        configProperties?.delete(flush: true)

        result = uploadDocumentCompositeService.getDocumentStorageSystem();
        assertNotNull result
        assert result.documentStorageLocation == 'AIP'
    }

    @Test
    void testMaximumAttachmentsValidationTrue() {
        def saveResult1 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFilePdf.pdf')
        assertTrue saveResult1.success
        def saveResult2 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileDoc.docx')
        assertTrue saveResult2.success
        def saveResult3 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileJpg.jpg')
        assertTrue saveResult3.success
        def saveResult4 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileJpg.jpg')
        assertTrue saveResult4.success
        def saveResult5 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFilePPT.pptx')
        assertTrue saveResult5.success

        Map paramsMapObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId
        ]
        boolean isMaxAttachmentValidation = uploadDocumentCompositeService.validateMaxAttachments(paramsMapObj)
        assertEquals true, isMaxAttachmentValidation

    }

    @Test
    void testMaximumAttachmentsValidationFalse() {
        def saveResult1 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFilePdf.pdf')
        assertTrue saveResult1.success
        def saveResult2 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileDoc.docx')
        assertTrue saveResult2.success
        def saveResult3 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileJpg.jpg')
        assertTrue saveResult3.success
        def saveResult4 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assertTrue saveResult4.success
        def saveResult5 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFilePPT.pptx')
        assertTrue saveResult5.success
        def saveResult6 = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileXLS.xlsx')
        assertTrue saveResult6.success
        Map paramsMapObj = [
                userActionItemId:  userActionItemId,
                responseId      :  responseId
        ]
        boolean isMaxAttachmentValidation = uploadDocumentCompositeService.validateMaxAttachments(paramsMapObj)
        assertEquals false, isMaxAttachmentValidation

    }

    @Test
    void testMaximumAttachmentsZero() {
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        def group = result.groups.find { it.title == 'Enrollment' }
        def item = group.items.find { it.name == 'Personal Information' }
        Long actionItemId = item.id

        List<ActionItemStatusRule> responsesList = ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId(actionItemId)
        ActionItemStatusRule response = responsesList.find { it.labelText == 'I have to research this' }
        def respId = response.id
        Map paramsMapObj = [
                responseId:  respId
        ]
        boolean isMaxAttachmentValidation = uploadDocumentCompositeService.validateMaxAttachments(paramsMapObj)
        assertEquals false, isMaxAttachmentValidation
    }

    @Test
    void testFetchDocuments() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
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
                userActionItemId: userActionItemId,
                responseId      : responseId,
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
                    userActionItemId: userActionItemId,
                    responseId      : responseId,
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
    void testSaveDocumentsInvalidFileLocation() {
        setConfigProperties('aip.attachment.file.storage.location', 'XYZ', 'string')
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == false
        assert saveResult.message == "Document management system is not supported. Please contact your administrator."
    }

    @Test
    void testDeleteDocument() {
        def saveResult = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert saveResult.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
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

    private def saveEmptyFileUploadDocumentService(userActionItemId, responseId, fileName) {


        MockMultipartFile multipartFile = createEmpgyFileformFileObject(fileName);
        multipartFile.metaClass.content=[]

        def result = uploadDocumentCompositeService.addDocument(
                [userActionItemId: userActionItemId, responseId: responseId, documentName: fileName, documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        result
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

    private configureClamAV() {
        Holders.config.clamav.enabled = true
        Holders.config.clamav.host = '127.0.0.1'
        Holders.config.clamav.port = 3310
        Holders.config.clamav.connectionTimeout = 10000
    }
    /**
     * Form file Object
     */
    private MockMultipartFile formFileObject(filename) {
        File testFile
        try {
            String data = " Test data for integration testing"
            println "Current working directory is "+System.getProperty("user.dir")
            String tempPath = System.getProperty("user.dir") + File.separator + "build"+File.separator+"tmp"

            testFile = new File(tempPath, filename)
            println "File create path is "+testFile.getPath()
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
        FileInputStream input = new FileInputStream(testFile)
        MultipartFile multipartFile = new MockMultipartFile("file",
                testFile.getName(), "text/plain", IOUtils.toByteArray(input))
        multipartFile
    }

    /**
     * Form file Object
     */
    private MockMultipartFile createEmpgyFileformFileObject(filename) {
        File testFile
        try {
            String data = " Test data for integration testing"
            String tempPath = System.getProperty("user.dir") + File.separator + "build"+File.separator+"tmp"

            testFile = new File(tempPath, filename)
            println "File create path is "+testFile.getPath()
            if (!testFile.exists()) {
                testFile.createNewFile()

            }
        } catch (IOException e) {
            throw e
        }
        FileInputStream input = new FileInputStream(testFile)
        MultipartFile multipartFile = new MockMultipartFile("file",
                testFile.getName(), "text/plain", IOUtils.toByteArray(input))
        multipartFile
    }
}
