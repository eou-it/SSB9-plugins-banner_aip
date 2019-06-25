/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.aip.bcm

import grails.gorm.transactions.Transactional


import java.sql.Clob
@Transactional
class BannerCommManagementResourceAccessService {
    def sessionFactory


    /**
     * Get BCM Location
     * @return
     */
    @Transactional(readOnly = true)
    def getBCMLocation() {
        def clob = (Clob) sessionFactory.currentSession.createSQLQuery(
                """SELECT GUROCFG_VALUE 
                          FROM GUROCFG 
                            WHERE GUROCFG_NAME='BCMLOCATION' 
                            AND GUROCFG_GUBAPPL_APP_ID = 'GENERAL_SS'""" ).
                uniqueResult()
        clob ? clob.asciiStream.text : ''
    }
}
