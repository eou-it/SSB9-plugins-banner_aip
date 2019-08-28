/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
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
@Integration
@Rollback
class UploadDocumentServiceIntegrationTests extends BaseIntegrationTestCase {

    def uploadDocumentCompositeService
    def uploadDocumentService
    def selfServiceBannerAuthenticationProvider
    def userActionItemReadOnlyCompositeService
    def userActionItemId
    def responseId
    def pidm

    @Before
    void setUp() {
        formContext = ['SELFSERVICE']
        super.setUp()
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU004', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        assertNotNull auth
        pidm = auth.pidm
        assertNotNull pidm
        getUserActionItemIdAndResponseId()
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
            String tempPath = System.getProperty("user.dir") + File.separator + "build"+File.separator+"tmp"
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

    private def saveUploadDocumentService(userActionItemId, responseId, fileName) {
        MockMultipartFile multipartFile = formFileObject(fileName)
        def result = uploadDocumentCompositeService.addDocument(
                [userActionItemId: userActionItemId, responseId: responseId, documentName: fileName, documentUploadedDate: new Date(), fileLocation: 'AIP', file: multipartFile])
        return result
    }

    @Test
    void testFetchDocuments() {

        def result = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert result.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
                filterName      : "%",
                sortColumn      : "id",
                sortAscending   : false
        ]
        List<UploadDocument> list = uploadDocumentService.fetchDocuments(paramsObj)
        assert list.size() == 1
    }

    @Test
    void testFetchDocumentsCount() {

        def result = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert result.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
                filterName      : "%",
                sortColumn      : "id",
                sortAscending   : false
        ]
        def count = uploadDocumentService.fetchDocumentsCount(paramsObj)
        assert count == 1
    }

    @Test
    void testFetchFileLocationById() {

        def result = saveUploadDocumentService(userActionItemId, responseId, 'AIPTestFileTXT.txt')
        assert result.success == true
        def paramsObj = [
                userActionItemId: userActionItemId,
                responseId      : responseId,
                filterName      : "%",
                sortColumn      : "id",
                sortAscending   : false
        ]
        List<UploadDocument> list = uploadDocumentService.fetchDocuments(paramsObj)
        assert list.size() == 1

        String fileLocation = uploadDocumentService.fetchFileLocationById(list[0].id)
        assert fileLocation == 'AIP'

    }

}
