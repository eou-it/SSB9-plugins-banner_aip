/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import org.apache.commons.io.IOUtils
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder


class UploadDocumentCompositeServiceIntegrationTest extends BaseIntegrationTestCase {

    def uploadDocumentCompositeService
    def selfServiceBannerAuthenticationProvider

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        def auth =selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU004','111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        assertNotNull auth
    }

    @After
    void tearDown() {
        super.tearDown()
    }

    @Test
    void testTextsaveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject()
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFile.txt', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testPdfsaveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject('AIPTestFile.pdf')
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFile.pdf', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testXlssaveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject('AIPTestFileXLS.xlsx')
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFileXLS.xlsx', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testPptsaveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject('AIPTestFilePPT.pptx')
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFilePPT.pptx', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testZipsaveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject('AIPTestFileZip.zipx')
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFileZip.zipx', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testMp3saveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject('AIPTestFileMp3.mp3')
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFileMp3.mp3', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testJpgsaveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject('AIPTestFileJpg.jpg')
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFileJpg.jpg', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testDocsaveUploadDocumentService() {
        println "service"
        MockMultipartFile multipartFile = formFileObject('AIPTestFileDoc.docx')
        println "mpf $multipartFile"
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFileDoc.docx', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    @Test
    void testuploadDocumentContent() {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileXLS.xlsx')
        def result =  uploadDocumentCompositeService.addUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: 'AIPTestFileXLS.xlsx', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        def doccontent =  uploadDocumentCompositeService.uploadDocumentContent(122,multipartFile);

    }

    @Test
    void testUploadAttachmentType() {
        def result =  uploadDocumentCompositeService.uploadDocumentType();
        println 'result' $result
        assertequals result.documentType 'EXE'
    }

    @Test
    void testUploadAttachmentSize() {
        def result =  uploadDocumentCompositeService.uploadDocumentSize();
        println 'result' $result

    }

    @Test
    void testFetchDocuments() {
        List documents = uploadDocumentCompositeService.fetchDocuments( [actionItemId: 132, responseId: 105] )
        assert documents.isEmpty() == true
    }

    /**
     * Form file Object
     */
    private MockMultipartFile formFileObject(filename) {
        File testFile
        try {
            String data = " Test data for integration testing"
            String tempPath = "test/Files"
            testFile = new File( tempPath, filename )
            if (!testFile.exists()) {
                testFile.createNewFile()
                FileWriter fileWritter = new FileWriter( testFile, true )
                BufferedWriter bufferWritter = new BufferedWriter( fileWritter )
                bufferWritter.write( data )
                bufferWritter.close()
            }
        } catch (IOException e) {
            throw e
        }
        FileInputStream input = new FileInputStream( testFile );
        MultipartFile multipartFile = new MockMultipartFile( "file",
                testFile.getName(), "text/plain", IOUtils.toByteArray( input ) )
        multipartFile
    }
}
