/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItemStatusRule
import net.hedtech.banner.aip.UploadDocument
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.commons.io.IOUtils
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.MultipartFile

class UploadDocumentServiceIntegrationTests extends BaseIntegrationTestCase {

    def uploadDocumentCompositeService
    def uploadDocumentService
    def selfServiceBannerAuthenticationProvider
    def userActionItemReadOnlyCompositeService

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

    void saveUploadDocumentService(actionItemId, responseId) {
        MockMultipartFile multipartFile = formFileObject('AIPTestFileTXT.txt')
        def result = uploadDocumentCompositeService.addUploadDocument(
                [actionItemId: actionItemId, responseId: responseId, documentName: 'AIPTestFileTXT.txt', documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        assert result.success == true
    }

    Long getActionItemId() {
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        def group = result.groups.find { it.title == 'Enrollment' }
        def item = group.items.find { it.name == 'Personal Information' }
        Long actionItemId = item.id
        actionItemId
    }

    Long getResponseIdByActionItemId(Long actionItemId) {
        List<ActionItemStatusRule> responseList = ActionItemStatusRule.fetchActionItemStatusRulesByActionItemId(actionItemId)
        Long responseId = responseList[0].id
        responseId
    }

    @Test
    void fetchDocuments() {
        Long actionItemId = getActionItemId()
        Long responseId = getResponseIdByActionItemId(actionItemId)
        saveUploadDocumentService(actionItemId, responseId)
        def pidm = PersonUtility.getPerson("CSRSTU004").pidm
        def paramsObj = [
                actionItemId : actionItemId.toString(),
                responseId   : responseId.toString(),
                pidm         : pidm,
                filterName   : "%",
                sortColumn   : "id",
                sortAscending: false
        ]
        List<UploadDocument> list = uploadDocumentService.fetchDocuments(paramsObj)
        assert list.size() == 1
    }

    @Test
    void fetchDocumentsCount() {
        Long actionItemId = getActionItemId()
        Long responseId = getResponseIdByActionItemId(actionItemId)
        saveUploadDocumentService(actionItemId, responseId)
        def pidm = PersonUtility.getPerson("CSRSTU004").pidm
        def paramsObj = [
                actionItemId : actionItemId.toString(),
                responseId   : responseId.toString(),
                pidm         : pidm,
                filterName   : "%",
                sortColumn   : "id",
                sortAscending: false
        ]
        def count = uploadDocumentService.fetchDocumentsCount(paramsObj)
        assert count == 1
    }

}
