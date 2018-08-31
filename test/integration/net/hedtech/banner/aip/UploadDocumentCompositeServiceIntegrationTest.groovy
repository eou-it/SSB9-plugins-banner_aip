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


class UploadDocumentCompositeServiceIntegrationTest extends BaseIntegrationTestCase {

    def uploadDocumentCompositeService

    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }

    @After
    void tearDown() {
        super.tearDown()
    }

    @Test
    void testsaveUploadDocumentService() {
        MockMultipartFile multipartFile = formFileObject()
        def result =  uploadDocumentCompositeService.saveUploadDocument(
                [ actionItemId: 132,responseId: 105, documentName: '1192_Anitha_13565.pdf', documentUploadedDate: new Date(),fileLocation:'AIP',file:multipartFile])
        assert result.success == true
    }

    /**
     * Form file Object
     */
    private MockMultipartFile formFileObject() {
        File testFile
        try {
            String data = " Test data for integration testing"
            String tempPath = "test/Files"
            testFile = new File( tempPath, "AIPTestFile.txt" )
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
