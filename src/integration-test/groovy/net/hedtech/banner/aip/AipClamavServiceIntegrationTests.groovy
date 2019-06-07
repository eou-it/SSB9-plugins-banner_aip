/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import grails.util.Holders
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.commons.io.IOUtils
import org.jenkinsci.plugins.clamav.scanner.ClamAvScanner
import org.jenkinsci.plugins.clamav.scanner.ScanResult
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.mock.web.MockMultipartFile
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.multipart.MultipartFile
@Integration
@Rollback
class AipClamavServiceIntegrationTests extends BaseIntegrationTestCase {

    def aipClamavService
    def clamavEnabled = false

    @Before
    void setUp() {
        formContext = ['GUAGMNU','SELFSERVICE']
        super.setUp()
        def auth = selfServiceBannerAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken('CSRSTU004', '111111'))
        SecurityContextHolder.getContext().setAuthentication(auth)
        assertNotNull auth
        if (clamavEnabled) {
            Holders.config.clamav.enabled = true
            Holders.config.clamav.host = "149.24.38.80"
            Holders.config.clamav.port = 3310
            Holders.config.clamav.connectionTimeout = 10000
        } else {
            Holders.config.clamav.enabled = false
        }
    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testIsClamavEnabledTrue() {
        Holders.config.clamav.enabled = true
        boolean result = aipClamavService.isClamavEnabled()
        assertTrue result
    }

    @Test
    void testIsClamavEnabledFalse() {
        Holders.config.clamav.enabled = false
        boolean result = aipClamavService.isClamavEnabled()
        assertFalse result
    }

    @Test
    void testInitScannerWithConfigValue() {
        if (clamavEnabled) {
            Holders.config.clamav.host = "149.24.38.80"
            Holders.config.clamav.port = 3310
            Holders.config.clamav.connectionTimeout = 10000
            ClamAvScanner scanner = aipClamavService.initScanner()
            assertTrue scanner.ping()
        }
    }

    @Test
    void testInitScannerWithDefaultValue() {
        if (clamavEnabled) {
            Holders.config.clamav.host = null
            Holders.config.clamav.port = null
            ClamAvScanner scanner = aipClamavService.initScanner()
            assertNotNull scanner
        }
    }

    @Test
    void testFileScannerSuccess() {
        if (clamavEnabled) {
            String tempPath = "test" + File.separator + "data"
            File testFile = new File(tempPath, 'AIPTestFileTXT.txt')
            FileInputStream input = new FileInputStream(testFile);
            ClamAvScanner scanner = aipClamavService.initScanner()
            ScanResult scanResult = aipClamavService.fileScanner(scanner, input)
            assert scanResult?.getStatus() == ScanResult.Status.PASSED
        }
    }

}
