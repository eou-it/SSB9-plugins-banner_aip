/*******************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemPostRecurringDetailsServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemPostRecurringDetailsService

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
    void testSave() {
        def actionItemPostRerurringDetails = newRecurringDetails()
        actionItemPostRerurringDetails = actionItemPostRecurringDetailsService.create(actionItemPostRerurringDetails)
        assert actionItemPostRerurringDetails.id
    }

    @Test
    void testFetch() {
        def actionItemPostRerurringDetails = newRecurringDetails()
        actionItemPostRerurringDetails = actionItemPostRecurringDetailsService.create(actionItemPostRerurringDetails)
        assert actionItemPostRerurringDetails.id
        def actionItemPostRerurringDetailsFetch = actionItemPostRecurringDetailsService.get(actionItemPostRerurringDetails.id)
        assert actionItemPostRerurringDetailsFetch.id

    }

    private def newRecurringDetails() {
        new ActionItemPostRecurringDetails(
                recurFrequency: 2L,
                recurFrequencyType: 'days',
                postingDispStartDays: 2L,
                postingDispEndDays: 10L,
                postingDisplayEndDate: new Date(),
                recurStartDate: new Date(),
                recurEndDate: new Date(),
                recurStartTime: new Date(),
                recurPostTimezone: 'timeZone',
                lastModified: new Date(),
                lastModifiedBy: 'testUser',
                dataOrigin: 'BANNER'
        )
    }

    @Test
    void testObjectValidation() {
        //preCreate.validation.insufficient.request
        def requestMap = null
        def exception = false
        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.insufficient.request', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testRecurFrequencyGreaterThanZero() {
        //preCreate.validation.recurrence.frequency.greater.zero
        def requestMap = [:]
        def exception = false
        requestMap.recurFrequency = 0
        requestMap.recurFrequencyType = 'DAYS'
        requestMap.postingDispStartDays = 10
        requestMap.postingDispEndDays = 10
        requestMap.recurStartDate = new Date()
        requestMap.recurEndDate = new Date() + 10
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.frequency.greater.zero', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testDisplayStartDaysGreaterThanZero() {
        //map && map.recurr && map.displayStartDateOffset && map.displayStartDateOffset <= 0
        def requestMap = [:]
        def exception = false
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'DAYS'
        requestMap.postingDispStartDays = -1
        requestMap.postingDispEndDays = 10
        requestMap.recurStartDate = new Date()
        requestMap.recurEndDate = new Date() + 10
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.displayStartDateOffset.zero', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testDisplayEndDaysGreaterThanZero() {
        //map && map.recurr && map.displayStartDateOffset && map.displayStartDateOffset <= 0
        def requestMap = [:]
        def exception = false
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'DAYS'
        requestMap.postingDispStartDays = 1
        requestMap.postingDispEndDays = -1
        requestMap.recurStartDate = new Date()
        requestMap.recurEndDate = new Date() + 10
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.postingDispEndDays.zero', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testDisplayEndDaysGreaterThanStartDate() {
        //map  && map.postingDispEndDays && !map.postingDisplayEndDate && !(map.postingDispEndDays >= map.postingDispStartDays )
        def requestMap = [:]
        def exception = false
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'DAYS'
        requestMap.postingDispStartDays = 3
        requestMap.postingDispEndDays = 2
        requestMap.recurStartDate = new Date()
        requestMap.recurEndDate = new Date() + 10
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.postingDisplayEndDate.greater.postingDispStartDays', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testDisplayEndDateGreaterThanStartDate() {
        //map  && map.postingDispEndDays && !map.postingDisplayEndDate && !(map.postingDispEndDays >= map.postingDispStartDays )
        def requestMap = [:]
        def exception = false
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'DAYS'
        requestMap.postingDispStartDays = 2
        requestMap.postingDisplayEndDate = new Date() + 20
        requestMap.recurStartDate = new Date() + 21
        requestMap.recurEndDate = new Date() + 22
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.postingDisplayEndDate.greater.than.recurStartDate', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testDisplayEndDateGreaterThanRecurEndDate() {
        //map  && map.postingDispEndDays && !map.postingDisplayEndDate && !(map.postingDispEndDays >= map.postingDispStartDays )
        def requestMap = [:]
        def exception = false
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'DAYS'
        requestMap.postingDispStartDays = 2
        requestMap.postingDisplayEndDate = new Date() + 20
        requestMap.recurStartDate = new Date() + 21
        requestMap.recurEndDate = new Date() + 22
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.postingDisplayEndDate.greater.than.recurStartDate', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testRecurEndDateGreaterThanStartDate() {
        //map  && map.postingDispEndDays && !map.postingDisplayEndDate && !(map.postingDispEndDays >= map.postingDispStartDays )

        def exception = false
        def requestMap = [:]
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'DAYS'
        requestMap.postingDispStartDays = 2
        requestMap.postingDisplayEndDate = new Date() + 5
        requestMap.recurStartDate = new Date()
        requestMap.recurEndDate = new Date()
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.recurStartDate.less.than.recurEndDate', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testRecurEndDateDateGreaterThanStartDateHoursSadPath() {
        //map  && map.postingDispEndDays && !map.postingDisplayEndDate && !(map.postingDispEndDays >= map.postingDispStartDays )

        def exception = false
        def requestMap = [:]
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'HOURS'
        requestMap.postingDispStartDays = 2
        requestMap.postingDisplayEndDate = new Date() + 5
        requestMap.recurStartDate = new Date()
        requestMap.recurEndDate = new Date() - 1
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.recurStartDate.less.than.or.equals.recurEndDate', ex.getMessage()
        }
        assertTrue exception
        assertNull resp

    }

    @Test
    void testRecurEndDateDateGreaterThanStartDateHoursHappyPath() {
        //map  && map.postingDispEndDays && !map.postingDisplayEndDate && !(map.postingDispEndDays >= map.postingDispStartDays )

        def exception = false
        def requestMap = [:]
        requestMap.recurFrequency = 1
        requestMap.recurFrequencyType = 'HOURS'
        requestMap.postingDispStartDays = 2
        requestMap.postingDisplayEndDate = new Date() + 5
        requestMap.recurStartDate = new Date()
        requestMap.recurEndDate = new Date()
        requestMap.recurStartTime = new Date()


        def resp
        try {
            resp = actionItemPostRecurringDetailsService.preCreateValidate(requestMap)
        }
        catch (Exception ex) {
            exception = true
            assertNotNull ex
            assertEquals 'preCreate.validation.recurrence.recurStartDate.equal.to.recurEndDate', ex.getMessage()
        }
        assertNull resp
        assertFalse exception

    }


    @Test
    void testGetDaysBetweenRecurStartAndEndDate() {
        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails(
                recurStartDate: new Date(2019, 03, 22),
                recurEndDate: new Date(2019, 03, 31))
        def days = actionItemPostRecurringDetailsService.getDaysBetweenRecurStartAndEndDate(actionItemPostRecurringDetails)
        assertEquals 9, days

        actionItemPostRecurringDetails.recurEndDate = new Date(2019, 03, 22)
        days = actionItemPostRecurringDetailsService.getDaysBetweenRecurStartAndEndDate(actionItemPostRecurringDetails)
        assertEquals 0, days

        actionItemPostRecurringDetails.recurEndDate = new Date(2019, 03, 21)
        days = actionItemPostRecurringDetailsService.getDaysBetweenRecurStartAndEndDate(actionItemPostRecurringDetails)
        assertNotEquals 0, days

    }

    @Test
    void getNuberOfJobsBasedOnDays() {

        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails()
        actionItemPostRecurringDetails.recurStartDate = new Date(2019, 03, 22)
        actionItemPostRecurringDetails.recurEndDate = new Date(2019, 03, 28)
        actionItemPostRecurringDetails.recurFrequencyType = "DAYS"
        actionItemPostRecurringDetails.recurFrequency = 1L
        ActionItemPost actionItemPost = new ActionItemPost(
                postingDisplayDateTime: new Date(2019, 03, 22)
        )

        def numberOfObjects = actionItemPostRecurringDetailsService.getNumberOfJobs(actionItemPostRecurringDetails,new Date(2019, 03, 22))
        assertEquals 6, numberOfObjects

        actionItemPostRecurringDetails.recurEndDate = new Date(2019, 03, 29)
        actionItemPostRecurringDetails.recurFrequency = 2L

        numberOfObjects = actionItemPostRecurringDetailsService.getNumberOfJobs(actionItemPostRecurringDetails,new Date(2019, 03, 22))
        assertEquals 3, numberOfObjects


    }

    @Test
    void testGetHoursBetweenRecurStartAndEndDate() {
        ActionItemPost actionItemPost = new ActionItemPost(postingDisplayDateTime:new Date(2019, 03, 22) )

        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails(
                    recurStartDate: new Date(2019, 03, 22),
                    recurEndDate: new Date(2019, 03, 23),
                    recurStartTime: new Date(2019, 03, 22, 8, 0))


        def hours = actionItemPostRecurringDetailsService.getHoursBetweenRecurStartAndEndDate(actionItemPostRecurringDetails,new Date(2019, 03, 22, 8, 0))
        assertEquals 39, hours

        actionItemPostRecurringDetails.recurStartTime = new Date(2019, 03, 23, 0, 0)
        actionItemPost.postingDisplayDateTime =  new Date(2019, 03, 23, 0, 0)
        hours = actionItemPostRecurringDetailsService.getHoursBetweenRecurStartAndEndDate(actionItemPostRecurringDetails,new Date(2019, 03, 23, 0, 0))
        assertEquals 23, hours
    }

    @Test
    void getNuberOfJobsBasedOnHours() {
        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails()
        actionItemPostRecurringDetails.recurStartTime = new Date(2019, 03, 22, 0, 0)
        ActionItemPost actionItemPost = new ActionItemPost(
                postingDisplayDateTime: new Date(2019, 03, 22, 0, 0)
        )
        actionItemPostRecurringDetails.recurEndDate = new Date(2019, 03, 23)
        actionItemPostRecurringDetails.recurFrequencyType = "HOURS"
        actionItemPostRecurringDetails.recurFrequency = 1L

        def numberOfObjects = actionItemPostRecurringDetailsService.getNumberOfJobs(actionItemPostRecurringDetails, new Date(2019, 03, 22, 0, 0))
        assertEquals 47, numberOfObjects

        actionItemPostRecurringDetails.recurFrequency = 2L
        numberOfObjects = actionItemPostRecurringDetailsService.getNumberOfJobs(actionItemPostRecurringDetails, new Date(2019, 03, 22, 0, 0))
        assertEquals 23, numberOfObjects

        actionItemPost.postingDisplayDateTime = new Date(2019, 03, 22, 9, 0)
        actionItemPostRecurringDetails.recurFrequency = 3L
        numberOfObjects = actionItemPostRecurringDetailsService.getNumberOfJobs(actionItemPostRecurringDetails,new Date(2019, 03, 22, 9, 0))
        assertEquals 12, numberOfObjects

    }

    @Test
    void testResolveStartDate() {
        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails()
        actionItemPostRecurringDetails.postingDispStartDays = 3L
        actionItemPostRecurringDetails.recurStartDate = new Date(2019, 03, 20)
        actionItemPostRecurringDetails.recurEndDate = new Date(2019, 03, 30)
        actionItemPostRecurringDetails.recurFrequencyType = "DAYS"
        actionItemPostRecurringDetails.recurFrequency = 1L
        //10 days and 10 iteration
        Date scheduledDate = new Date(2019, 03, 20)
        Date result = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDate,actionItemPostRecurringDetails)
        assertEquals new Date(2019, 03, 23), result
        scheduledDate=result
        result = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDate,actionItemPostRecurringDetails)
        assertEquals new Date(2019, 03, 26), result
        scheduledDate=result
        result = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDate,actionItemPostRecurringDetails)
        assertEquals new Date(2019, 03, 29), result
    }

    @Test
    void resolveScheduleDateTimeFreqTypeDays() {
        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails(
                recurStartDate: new Date(2019, 03, 20),
                recurStartTime: new Date(2019, 03, 20, 9, 30),
                recurFrequency: 2L,
                recurFrequencyType: "DAYS"
        )

        Date result = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 1)
        assertEquals new Date(2019, 03, 22, 9, 30), result

        result = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 2)
        assertEquals new Date(2019, 03, 24, 9, 30), result

        result = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 3)
        assertEquals new Date(2019, 03, 26, 9, 30), result
    }

    @Test
    void resolveScheduleDateTimeFreqTypeTime() {
        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails(
                recurStartDate: new Date(2019, 03, 20),
                recurStartTime: new Date(2019, 03, 20, 9, 30),
                recurFrequency: 4L,
                recurFrequencyType: "HOURS"
        )

        Date result = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 1)
        assertEquals new Date(2019, 03, 20, 13, 30), result

        result = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 2)
        assertEquals new Date(2019, 03, 20, 17, 30), result

        result = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 3)
        assertEquals new Date(2019, 03, 20, 21, 30), result

        result = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 4)
        assertEquals new Date(2019, 03, 21, 1, 30), result
    }

    @Test
    void resolveDatesForOffsetForBothStartAndEnd() {

        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails(
                recurStartDate: new Date(2019, Calendar.FEBRUARY, 24),
                recurStartTime: new Date(2019, Calendar.FEBRUARY, 24, 0, 0),
                recurEndDate: new Date(2019, Calendar.MARCH, 7),
                recurFrequency: 2L,
                recurFrequencyType: "DAYS",
                postingDispStartDays: 2,
                postingDispEndDays: 10
        )
        Date scheduledDateTime
        Date resolvedStartDate
        Date resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 0)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 24), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 26), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 6), resolvedEndDate


        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 1)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 26), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 28), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 8), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 2)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 28), scheduledDateTime
        assertEquals new Date(2019, Calendar.MARCH, 2), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 10), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 3)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.MARCH, 2), scheduledDateTime
        assertEquals new Date(2019, Calendar.MARCH, 4), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 12), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 4)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.MARCH, 4), scheduledDateTime
        assertEquals new Date(2019, Calendar.MARCH, 6), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 14), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 5)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.MARCH, 6), scheduledDateTime
        assertEquals new Date(2019, Calendar.MARCH, 8), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 16), resolvedEndDate

    }

    @Test
    void resolveDatesForFixedEndDate() {

        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails(
                recurStartDate: new Date(2019, Calendar.FEBRUARY, 24),
                recurStartTime: new Date(2019, Calendar.FEBRUARY, 24, 0, 0),
                recurEndDate: new Date(2019, Calendar.MARCH, 7),
                recurFrequency: 2L,
                recurFrequencyType: "DAYS",
                postingDispStartDays: 2,
                postingDisplayEndDate: new Date(2019, Calendar.MARCH, 20)
        )


        Date scheduledDateTime
        Date resolvedStartDate
        Date resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 0)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 24), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 26), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 20), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 1)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 26), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 28), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 20), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 2)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 28), scheduledDateTime
        assertEquals new Date(2019, Calendar.MARCH, 02), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 20), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 3)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.MARCH, 02), scheduledDateTime
        assertEquals new Date(2019, Calendar.MARCH, 04), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 20), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 4)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.MARCH, 04), scheduledDateTime
        assertEquals new Date(2019, Calendar.MARCH, 06), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 20), resolvedEndDate

    }

    @Test
    void testHourlyRecurrance() {

        ActionItemPostRecurringDetails actionItemPostRecurringDetails = new ActionItemPostRecurringDetails(
                recurStartDate: new Date(2019, Calendar.FEBRUARY, 24),
                recurStartTime: new Date(2019, Calendar.FEBRUARY, 24, 11, 30),
                recurEndDate: new Date(2019, Calendar.FEBRUARY, 25),
                recurFrequency: 5L,
                recurFrequencyType: "HOURS",
                postingDispStartDays: 2,
                postingDispEndDays: 10
        )


        Date scheduledDateTime
        Date resolvedStartDate
        Date resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 0)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 24,11,30), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 26,0,0), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 6,0,0), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 1)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 24,16,30), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 26,0,0), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 6,0,0), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 2)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 24,21,30), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 26,0,0), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 06,0,0), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 3)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 25,2,30), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 27,0,0 ), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 7,0,0), resolvedEndDate

        scheduledDateTime = actionItemPostRecurringDetailsService.resolveScheduleDateTime(actionItemPostRecurringDetails, 4)
        resolvedStartDate = actionItemPostRecurringDetailsService.resolveDisplayStartDate(scheduledDateTime, actionItemPostRecurringDetails)
        resolvedEndDate = actionItemPostRecurringDetailsService.resolveDiplayEndDate(scheduledDateTime, actionItemPostRecurringDetails)

        assertEquals new Date(2019, Calendar.FEBRUARY, 25,7,30), scheduledDateTime
        assertEquals new Date(2019, Calendar.FEBRUARY, 27,0,0), resolvedStartDate
        assertEquals new Date(2019, Calendar.MARCH, 7,0,0), resolvedEndDate

    }
}
