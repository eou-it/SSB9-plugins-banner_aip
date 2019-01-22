/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.transaction.Transactional
import grails.util.Holders
import net.hedtech.banner.aip.common.AIPConstants
import org.jenkinsci.plugins.clamav.scanner.ClamAvScanner
import org.jenkinsci.plugins.clamav.scanner.ScanResult

@Transactional
class AipClamavService {

    /**
     * Method to initialize clamAV scanner
     */
    public ClamAvScanner initScanner() {
        ClamAvScanner scanner
        def hostIP = (Holders.config.clamav?.host)?Holders.config.clamav.host: AIPConstants.DEFAULT_CLAMAV_HOST
        def port = (Holders.config.clamav?.port)?Holders.config.clamav.port: AIPConstants.DEFAULT_CLAMAV_PORT
        def connectionTimeout = Holders.config.clamav?.connectionTimeout
        scanner = new ClamAvScanner(hostIP, port, connectionTimeout)
    }

    /**
     * Method to scan files for virus
     * @param scanner
     * @param fileInputStream
     * @return
     */
    public ScanResult fileScanner(ClamAvScanner scanner, InputStream file){
        ScanResult resScan
        resScan = scanner.scan(file)
    }
    /**
     * Check if clamav virus scanning is enabled
     * @return
     */
    public boolean isClamavEnabled(){
        boolean clamavEnabled
        clamavEnabled = (Holders.config.clamav?.enabled)?Holders.config.clamav?.enabled:false
    }
}
