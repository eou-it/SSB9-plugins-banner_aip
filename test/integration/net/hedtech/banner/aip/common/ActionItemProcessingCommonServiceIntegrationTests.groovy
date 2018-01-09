/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.common

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.i18n.LocalizeUtil
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
        assert [
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
        ] == actionItemProcessingCommonService.listStatus()
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
    void getRequestedTimezoneCalendar() {
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
}
