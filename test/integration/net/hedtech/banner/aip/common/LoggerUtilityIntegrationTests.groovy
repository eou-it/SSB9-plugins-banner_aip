/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.common

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.apache.log4j.Level
import org.apache.log4j.Logger
import org.junit.After
import org.junit.Before
import org.junit.Test


class LoggerUtilityIntegrationTests extends BaseIntegrationTestCase {
    private static final LOGGER = Logger.getLogger( this.class )


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
    void testLogError() {
        LoggerUtility.error( LOGGER, 'errorMessage' )
    }


    @Test
    void testLogWarn() {
        LoggerUtility.warn( LOGGER, 'warningMessage' )
    }


    @Test
    void testLogDebugIfEnabled() {
        LOGGER.setLevel( Level.DEBUG )
        LoggerUtility.debug( LOGGER, 'debugMessage' )
    }


    @Test
    void testLogDebugIfNotEnabled() {
        LOGGER.setLevel( Level.WARN )
        LoggerUtility.debug( LOGGER, 'debugMessage' )
    }
}
