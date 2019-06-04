/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.text.SimpleDateFormat
@Integration
@Rollback
class ActionItemPostIntegrationTests extends BaseIntegrationTestCase {
    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }

    def dateFormat = "dd-MM-yyyy"




    @Test
    void testSave() {
        def actionItemPost = newAIP()
        actionItemPost.save()
    }


    @Test
    void testSaveWithCustomTimeAndTimeZone12HrFormat() {
        Map map = [time: '2314', date: '19-06-2018', timeZone: 'Asia/Singapore']
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormat)
        def actionItemPost = newAIPWithCustomTimeAndTimeZone(map)
        actionItemPost.save(flush: true)
        ActionItemPost actionItemPostRetrieved = ActionItemPost.findByPostingName('some name')
        assert actionItemPostRetrieved != null
        boolean _12_HourTimeFormat = true
        assert map.date == dateFormat.format(actionItemPostRetrieved.postingDisplayDateTime)
        assert map.timeZone == actionItemPostRetrieved.postingTimeZone
        assert '11:14 PM' == retTimeFormat(_12_HourTimeFormat, actionItemPostRetrieved)
    }

    @Test
    void testSaveWithCustomTimeAndTimeZone24HrFormat() {
        Map map = [time: '2314', date: '19-06-2018', timeZone: 'Asia/Singapore']
        SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormat)
        def actionItemPost = newAIPWithCustomTimeAndTimeZone(map)
        actionItemPost.save(flush: true)
        ActionItemPost actionItemPostRetrieved = ActionItemPost.findByPostingName('some name')
        assert actionItemPostRetrieved != null
        boolean _12_HourTimeFormat = false
        assert map.date == dateFormat.format(actionItemPostRetrieved.postingDisplayDateTime)
        assert map.timeZone == actionItemPostRetrieved.postingTimeZone
        assert '23:14' == retTimeFormat(_12_HourTimeFormat, actionItemPostRetrieved)
    }

    private retTimeFormat(_12_HourTimeFormat, actionItemPostRetrieved) {
        SimpleDateFormat _24_hr_timeFormat = new SimpleDateFormat("HH:mm")
        SimpleDateFormat _12_hr_timeFormat = new SimpleDateFormat("hh:mm a")
        _12_HourTimeFormat ? _12_hr_timeFormat.format(actionItemPostRetrieved.postingDisplayDateTime) : _24_hr_timeFormat.format(actionItemPostRetrieved.postingDisplayDateTime)
    }

    private def newAIPWithCustomTimeAndTimeZone(map) {
        Calendar scheduledStartDateCalendar = getRequestedTimezoneCalendar(map.date, map.time)
        new ActionItemPost(
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingActionItemGroupId: ActionItemGroup.findByName('Enrollment').id,
                postingDeleteIndicator: false,
                postingScheduleType: "some type",
                postingCreationDateTime: new Date(),
                postingDisplayStartDate: new Date(),
                postingDisplayEndDate: new Date(),
                postingCreatorId: 'me',
                postingScheduleDateTime: new Date(),
                postingDisplayDateTime: scheduledStartDateCalendar.getTime(),
                postingTimeZone: map.timeZone,
                populationRegenerateIndicator: false,
                postingCurrentState: ActionItemPostExecutionState.New,
                postingStartedDate: null,
                postingStopDate: null,
                aSyncJobId: "la43j45h546k56g6f6r77a7kjfn",
                populationCalculationId: 1L,
                postingErrorCode: ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                postingErrorText: null,
                aSyncGroupId: null,
                postingParameterValues: null,
                lastModified: new Date(),
                lastModifiedBy: 'testUser',
                dataOrigin: 'BANNER'
        )
    }

    java.util.Calendar getRequestedTimezoneCalendar(scheduledStartDate, scheduledStartTime) {
        java.util.Calendar scheduledStartDateCalendar = java.util.Calendar.getInstance()
        scheduledStartDateCalendar.setTime(new SimpleDateFormat(dateFormat).parse(scheduledStartDate))
        scheduledStartDateCalendar.set(java.util.Calendar.HOUR_OF_DAY, scheduledStartTime.substring(0, 2).toInteger())
        scheduledStartDateCalendar.set(java.util.Calendar.MINUTE, scheduledStartTime.substring(2).toInteger())
        scheduledStartDateCalendar.set(java.util.Calendar.SECOND, 0)
        scheduledStartDateCalendar.set(java.util.Calendar.MILLISECOND, 0)
        scheduledStartDateCalendar
    }


    private def newAIP() {
        new ActionItemPost(
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingActionItemGroupId: ActionItemGroup.findByName('Enrollment').id,
                postingDeleteIndicator: false,
                postingScheduleType: "some type",
                postingCreationDateTime: new Date(),
                postingDisplayStartDate: new Date(),
                postingDisplayEndDate: new Date(),
                postingCreatorId: 'me',
                postingScheduleDateTime: new Date(),
                populationRegenerateIndicator: false,
                postingCurrentState: ActionItemPostExecutionState.New,
                postingStartedDate: null,
                postingStopDate: null,
                aSyncJobId: "la43j45h546k56g6f6r77a7kjfn",
                populationCalculationId: 1L,
                postingErrorCode: ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                postingErrorText: null,
                aSyncGroupId: null,
                postingParameterValues: null,
                lastModified: new Date(),
                lastModifiedBy: 'testUser',
                dataOrigin: 'BANNER'
        )
    }

}
