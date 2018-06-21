/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/

package net.hedtech.banner.aip.common

import com.ibm.icu.util.Calendar
import com.ibm.icu.util.ULocale
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.general.communication.population.CommunicationPopulationListView
import net.hedtech.banner.i18n.LocalizeUtil
import net.hedtech.banner.i18n.MessageHelper
import org.apache.log4j.Logger
import org.springframework.context.i18n.LocaleContextHolder

import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


class ActionItemProcessingCommonService {
    def dateConverterService

    def grailsApplication

    def messageSource

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
        CommunicationPopulationListView.findAllForSendByPagination( [params: [name: searchParam ?: '%']], paginationParam )
    }
    /**
     * Converts give date into localized formatted date. If user want to have default Date to current date if date
     * not provide, need to pass needDefaultDate true
     *
     * @param strDate
     * @return
     */
    def convertToLocaleBasedDate( strDate ) {

        if (strDate == "") {
            return strDate
        }
        try {
            if (strDate instanceof String) {
                return LocalizeUtil.parseDate( dateConverterService.parseDefaultCalendarToGregorian( strDate ) )
            }
            return strDate
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
        ].sort { a, b -> a.value <=> b.value }
    }

    /**
     *
     * @param tz
     * @return
     */
    private static String formatGMTString( TimeZone tz ) {
        String result
        long now = new Date().getTime();
        long hours = TimeUnit.MILLISECONDS.toHours( tz.getOffset( now ) );
        long minutes = TimeUnit.MILLISECONDS.toMinutes( tz.getOffset( now ) ) - TimeUnit.HOURS.toMinutes( hours );
        minutes = Math.abs( minutes );

        if (hours >= 0) {
            result = String.format( "(GMT+%02d:%02d)", hours, minutes );
        } else {
            result = String.format( "(GMT%03d:%02d)", hours, minutes );
        }
        return result;
    }

    /**
     *
     * @return
     */
    List<AipTimezone> populateAvailableTimezones() {
        Set<String> keys = messageSource.getMergedProperties( LocaleContextHolder.getLocale() ).properties.keySet()
        Set<String> timezoneKeys = keys.findAll { key -> key.startsWith( 'timezone.' ) }
        Set<String> intendedTimezoneIds = new HashSet<String>()
        timezoneKeys.each {
            intendedTimezoneIds.add( (String) it.tokenize( "." ).toArray()[1] )
        }
        Set<String> timezoneIds = TimeZone.getAvailableIDs()
        timezoneIds.retainAll() {
            intendedTimezoneIds.contains( it )
        }
        List<AipTimezone> commTimezoneList = new LinkedList<AipTimezone>()
        timezoneIds.each {
            TimeZone tz = TimeZone.getTimeZone( it )
            String GMTResult = formatGMTString( tz )
            AipTimezone commTimezone = new AipTimezone()
            commTimezone.setStringOffset( GMTResult )
            commTimezone.setOffset( tz.getOffset( new Date().getTime() ) )
            commTimezone.setTimezoneId( tz.getID() )
            commTimezone.displayName = GMTResult + " " + messageSource.getMessage( "timezone." + it, null, LocaleContextHolder.getLocale() )
            commTimezone.displayNameWithoutOffset = messageSource.getMessage( "timezone." + it, null, LocaleContextHolder.getLocale() )
            commTimezoneList.add( commTimezone )
        }
        commTimezoneList.sort { t1, t2 -> (t1.offset <=> t2.offset) ?: (t1.timezoneId <=> t2.timezoneId) }
        return commTimezoneList;
    }

    /**
     *
     * @param scheduledStartDate
     * @param scheduledStartTime
     * @param timezoneStringOffset
     * @return
     */
    java.util.Calendar getRequestedTimezoneCalendar( scheduledStartDate, scheduledStartTime, timezoneStringOffset ) {
        List<AipTimezone> timezoneList = populateAvailableTimezones();
        AipTimezone communicationTimezone = timezoneList.find {
            it.timezoneId == timezoneStringOffset
        }
        java.util.Calendar scheduledStartDateCalendar = java.util.Calendar.getInstance()
        scheduledStartDateCalendar.setTime( scheduledStartDate )
        scheduledStartDateCalendar.set( java.util.Calendar.HOUR_OF_DAY, scheduledStartTime.substring( 0, 2 ).toInteger() )
        scheduledStartDateCalendar.set( java.util.Calendar.MINUTE, scheduledStartTime.substring( 2 ).toInteger() )
        scheduledStartDateCalendar.set( java.util.Calendar.SECOND, 0 )
        scheduledStartDateCalendar.set( java.util.Calendar.MILLISECOND, 0 )
        scheduledStartDateCalendar.setTimeZone( TimeZone.getTimeZone( communicationTimezone.timezoneId ) )
        scheduledStartDateCalendar
    }

    /**
     *
     * @return
     */
    def fetchCurrentDateInLocaleFormat() {
        [date      : dateConverterService.parseGregorianToDefaultCalendar( LocalizeUtil.formatDate( new Date() ) ),
         dateFormat: LocalizeUtil.dateFormat]
    }

    /**
     *
     * @return
     */
    def is12HourClock() {
        def use12HourClock = MessageHelper.message( 'default.cm.timepicker.12hourclock' ) == '1'
        [use12HourClock: use12HourClock]
    }

    /**
     *
     * Fetch Current Date,Time,TimeZone
     */
    def fetchCurrentDateTimeZone() {
        Date currentDate = new Date()
        SimpleDateFormat timeFormat = new SimpleDateFormat( MessageHelper.message( "default.time.format" ) )
        if (is12HourClock().use12HourClock) {
            timeFormat = new SimpleDateFormat( "hh:mm a" )
        }
        [
                ServerDate    : currentDate,
                ServerTime    : timeFormat.format( currentDate ),
                ServerTimeZone: TimeZone.getDefault()
        ]
    }

    /**
     *
     * @param requestMap
     * @return
     */
    def fetchProcessedServerDateTimeAndTimezone( requestMap ) {
        Date scheduledStartDate = convertToLocaleBasedDate( requestMap.userEnterDate )
        String scheduledStartTime = requestMap.userEnterTime
        String timezoneStringOffset = requestMap.userEnterTimeZone
        java.util.Calendar scheduledStartDateCalendar = getRequestedTimezoneCalendar( scheduledStartDate, scheduledStartTime, timezoneStringOffset );
        List timeZoneList = populateAvailableTimezones()
        TimeZone timezone = TimeZone.getDefault();
        int defaultRowOffset = timezone.getRawOffset()
        AipTimezone serverDefaultTimeZone = timeZoneList.find {
            it.offset == defaultRowOffset //Getting the time zone of the server
        }
        SimpleDateFormat hr24TimeFormat = new SimpleDateFormat( MessageHelper.message( "default.time.format" ) )
        SimpleDateFormat hr12TimeFormat = new SimpleDateFormat( "HH:mm a" )
        [serverDate    : scheduledStartDateCalendar.getTime(),
         serverTime    : is12HourClock().use12HourClock ? hr12TimeFormat.format( scheduledStartDateCalendar.getTime() ) : hr24TimeFormat.format( scheduledStartDateCalendar.getTime() ),
         serverTimeZone: serverDefaultTimeZone.timezoneId
        ]
    }
}
