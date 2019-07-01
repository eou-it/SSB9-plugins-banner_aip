/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */
package net.hedtech.banner.aip.common

/**
 * Class to log the statements
 */
class LoggerUtility {

    /**
     * @param logger
     * @param msg
     * @return
     */
    def static info( logger, msg ) {
        if (logger?.isInfoEnabled()) {
            logger.info( msg )
        }
    }

    /**
     * @param logger
     * @param msg
     * @return
     */
    def static debug( logger, msg ) {
        if (logger?.isDebugEnabled()) {
            logger.debug( msg )
        }
    }

    /**
     * @param logger
     * @param msg
     * @return
     */
    def static error( logger, msg ) {
        logger.error( msg )
    }

    /**
     * @param logger
     * @param msg
     * @return
     */
    def static warn( logger, msg ) {
        logger.warn( msg )
    }
}
