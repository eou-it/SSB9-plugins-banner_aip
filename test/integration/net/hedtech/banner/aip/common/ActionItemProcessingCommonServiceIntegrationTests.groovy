/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.common

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.i18n.LocalizeUtil
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat


class ActionItemProcessingCommonServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemProcessingCommonService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        loginSSB( 'AIPADM001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void testFetchCommunicationFolders() {
        assert actionItemProcessingCommonService.fetchCommunicationFolders().size() > 0
    }


    @Test
    void testFetchPopulationListForSend() {
        assert actionItemProcessingCommonService.fetchPopulationListForSend( 'p', [max: 10, offset: 0] ).size() > 0
    }


    @Test
    void testConvertToLocaleBasedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat( 'MM/dd/yyyy' )
        String str = sdf.format( new Date() )
        def date = actionItemProcessingCommonService.convertToLocaleBasedDate( str )
        assert str == sdf.format( date )
    }


    @Test
    void testConvertToLocaleBasedDateFailedCase() {
        try {
            actionItemProcessingCommonService.convertToLocaleBasedDate( 'XYZ' )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'invalid.date.format' )
        }
    }


    @Test
    void testGetLocaleBasedCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat( 'MM/dd/yyyy' )
        String str = sdf.format( new Date() )
        def date = actionItemProcessingCommonService.getLocaleBasedCurrentDate()
        assert str == sdf.format( date )
    }


    @Test
    void testListStatus() {
        assert [[id: 2, value: 'Active'], [id: 1, value: 'Draft'], [id: 3, value: 'Inactive']] == actionItemProcessingCommonService.listStatus()
    }


    @Test
    void testIs12HourClock() {
        def result = actionItemProcessingCommonService.is12HourClock()
        assert result.use12HourClock == true
    }


    @Test
    void fetchCurrentDateInLocaleFormat() {
        def result = actionItemProcessingCommonService.fetchCurrentDateInLocaleFormat()
        assert result.dateFormat == LocalizeUtil.dateFormat
    }


    @Test
    void testGetRequestedTimezoneCalendar() {
        SimpleDateFormat testingDateFormat = new SimpleDateFormat( 'MM/dd/yyyy' )
        Date scheduledStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate( testingDateFormat.format( new Date() ) )
        def scheduledStartTime = "2230"
        def timezoneStringOffset = "Asia/Kolkata"
        def result = actionItemProcessingCommonService.getRequestedTimezoneCalendar( scheduledStartDate, scheduledStartTime, timezoneStringOffset )
        assert result != null
    }


    @Test
    void populateAvailableTimezones() {
        def result = actionItemProcessingCommonService.populateAvailableTimezones()
        assert result.size() > 0
    }


    @Test
    void testFetchProcessedServerDateTimeAndTimezone() {
        Map map = ["userEnterDate"    : "06/21/2018",
                   "userEnterTime"    : "1330",
                   "userEnterTimeZone": "US/Alaska"]
        Date scheduledStartDate = actionItemProcessingCommonService.convertToLocaleBasedDate( map.userEnterDate )
        String scheduledStartTime = map.userEnterTime
        String timezoneStringOffset = map.userEnterTimeZone
        java.util.Calendar scheduledStartDateCalendar = actionItemProcessingCommonService.getRequestedTimezoneCalendar( scheduledStartDate, scheduledStartTime, timezoneStringOffset );
        SimpleDateFormat hr24TimeFormat = new SimpleDateFormat( MessageHelper.message( "default.time.format" ) )
        SimpleDateFormat hr12TimeFormat = new SimpleDateFormat( "HH:mm a" )
        def serverTime = actionItemProcessingCommonService.is12HourClock().use12HourClock ? hr12TimeFormat.format( scheduledStartDateCalendar.getTime() ) : hr24TimeFormat.format( scheduledStartDateCalendar.getTime() )
        def result = actionItemProcessingCommonService.fetchProcessedServerDateTimeAndTimezone( map )
        assert result.serverDate == scheduledStartDateCalendar.getTime()
        assert result.serverTime == serverTime
        TimeZone timezone = TimeZone.getDefault();
        int defaultRowOffset = timezone.getRawOffset()
        List timeZoneList = populateAvailableTimezones()
        AipTimezone serverDefaultTimeZone = timeZoneList.find {
            it.offset == defaultRowOffset //Getting the time zone of the server
        }
        assert result.serverTimeZone != serverDefaultTimeZone.timezoneId
    }


    @Test
    void testFetchCurrentDateTimeZone() {
        Date currentDate = new Date()
        def result = actionItemProcessingCommonService.fetchCurrentDateTimeZone()
        assert currentDate.compareTo( result.ServerDate ) <= 0
        assert result.ServerTime != null
        assert result.ServerTimeZone == TimeZone.getDefault()
    }
}
