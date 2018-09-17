/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.omg.CORBA.portable.ApplicationException
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import org.apache.commons.io.IOUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import net.hedtech.banner.general.configuration.ConfigProperties
import net.hedtech.banner.exceptions.ApplicationException

class UploadDocumentCompositeServiceIntegrationTest extends BaseIntegrationTestCase {

    def uploadDocumentCompositeService
    def selfServiceBannerAuthenticationProvider

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU004', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        assertNotNull auth
    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testTextsaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileTXT.txt')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFileTXT.txt', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    @Test
    void testPdfsaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFilePdf.pdf')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFilePdf.pdf', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    @Test
    void testXlssaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileXLS.xlsx')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFileXLS.xlsx', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    @Test
    void testPptsaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFilePPT.pptx')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFilePPT.pptx', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    @Test
    void testZipsaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileZip.zipx')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFileZip.zipx', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    @Test
    void testMp3saveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileMp3.mp3')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFileMp3.mp3', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    @Test
    void testJpgsaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileJpg.jpg')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFileJpg.jpg', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    @Test
    void testDocsaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileDoc.docx')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: 15, responseId: 45, documentName: 'AIPTestFileDoc.docx', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }


    @Test
    void testRestrictedFileTypes() {
        ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.restricted.attachment.type', 'GENERAL_SS')
        assertNotNull configProperties
        configProperties.configValue = '[EXE,ZIP]'
        configProperties.save(flush: true, failOnError: true)
        def result = uploadDocumentCompositeService.getRestrictedFileTypes();
        assertNotNull result
        assert result.restrictedFileTypes == '[EXE,ZIP]'
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
    void testFileTypeValidation() {
        try {
            ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.restricted.attachment.type', 'GENERAL_SS')
            assertNotNull configProperties
            configProperties.configValue = '[EXE,MP3]'
            configProperties.save(flush: true, failOnError: true)
            uploadDocumentCompositeService.fileTypeValidation('EXE')
        } catch (ApplicationException e) {
            assertTrue(e.getMessage().toString().contains("@@r1:FileTypeError@@"))
            assertTrue(e.getDefaultMessage().toString().contains('uploadDocument.file.type.error'))
        }
    }

    @Test
    void testFileSizeValidation() {
        try {
            ConfigProperties configProperties = ConfigProperties.fetchByConfigNameAndAppId('aip.allowed.attachment.max.size', 'GENERAL_SS')
            assertNotNull configProperties
            configProperties.configValue = '1077778'
            configProperties.save(flush: true, failOnError: true)
            uploadDocumentCompositeService.fileSizeValidation(233421122)
        } catch (ApplicationException e) {
            assertTrue(e.getMessage().toString().contains("@@r1:MaxSizeError@@"))
            assertTrue(e.getDefaultMessage().toString().contains('uploadDocument.max.file.size.error'))
        }
    }
    /**
     * Form file Object
     */
    private MockMultipartFile formFileObject(filename) {
        File testFile
        try {
            String data = " Test data for integration testing"
            String tempPath = "test/Files"
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