/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.aip.bcm

import grails.transaction.Transactional
import org.apache.log4j.Logger

import java.sql.Clob

class BannerCommManagementResourceAccessService {
    def sessionFactory
    def log = Logger.getLogger( this.class )

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
