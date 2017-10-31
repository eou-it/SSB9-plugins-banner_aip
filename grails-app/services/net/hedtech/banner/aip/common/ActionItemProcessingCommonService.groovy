/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.aip.common

import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import net.hedtech.banner.aip.post.grouppost.ActionItemPost
import net.hedtech.banner.aip.post.grouppost.ActionItemPostService
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

    /**
     * Get locale bases current system date
     * @return
     */
    def getLocaleBasedCurrentDate() {
        Calendar calendar = Calendar.getInstance( new ULocale( dateConverterService.getDefaultCalendarULocaleString() ) )
        calendar.set( Calendar.HOUR_OF_DAY, 0 )
        calendar.set( Calendar.MINUTE, 0 )
        calendar.set( Calendar.SECOND, 0 )
        calendar.set( Calendar.MILLISECOND, 0 )
        calendar.time
    }

    /**
     * List Admin Group/Action Item Status
     * @return
     */
    def listStatus() {
        [
                [
                        "id"   : 1,
                        "value": "Draft"
                ], [
                        "id"   : 2,
                        "value": "Active"
                ], [
                        "id"   : 3,
                        "value": "Inactive"
                ]
        ]
    }

}
