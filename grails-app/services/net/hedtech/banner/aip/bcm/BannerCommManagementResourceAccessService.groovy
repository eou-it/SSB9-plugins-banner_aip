/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
package net.hedtech.banner.aip.bcm

import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.hibernate.HibernateException

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
        try {
            def clob = (Clob) sessionFactory.currentSession.createSQLQuery(
                    """SELECT GUROCFG_VALUE 
                          FROM GUROCFG 
                            WHERE GUROCFG_NAME='BCMLOCATION' 
                            AND GUROCFG_TYPE ='location' 
                            AND GUROCFG_GUBAPPL_APP_ID = 'GENERAL_SS'""" ).
                    uniqueResult()
            clob ? clob.asciiStream.text : ''
        } catch (HibernateException he) {
            log.error( 'Error while querying BCM location' )
        }
    }
}
