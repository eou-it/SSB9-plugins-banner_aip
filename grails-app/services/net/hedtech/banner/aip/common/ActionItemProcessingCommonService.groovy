/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.aip.common

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.i18n.LocalizeUtil
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import org.apache.log4j.Logger

class ActionItemProcessingCommonService {
    def dateConverterService

    private static final def LOGGER = Logger.getLogger( this.getClass() )

    /**
     * Gets Communication folders
     * @return
     */
    def fetchCommunicationFolders() {
        CommunicationFolder.list( sort: "name", order: "asc" )
    }
    /**
     * Gets Population List For Send
     * @return
     */
    def fetchPopulationListForSend( searchParam, paginationParam ) {
        println 'search Param' + searchParam
        CommunicationPopulationListView.findAllForSendByPagination( [params: [name: searchParam]], paginationParam )
    }
    /**
     * Converts give date into localized formatted date. If user want to have default Date to current date if date
     * not provide, need to pass needDefaultDate true
     *
     * @param strDate
     * @return
     */
    def convertToLocaleBasedDate( strDate ) {
        try {
            return LocalizeUtil.parseDate( dateConverterService.parseDefaultCalendarToGregorian( strDate ) )
        }
        catch (ApplicationException e) {
            LOGGER.error( 'Error while parsing date' + e )
            throw new ApplicationException( ActionItemProcessingCommonService, new BusinessLogicValidationException( 'invalid.date.format', [] ) )
        }
    }

}
