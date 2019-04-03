/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class AipReviewStateIntegrationTests extends BaseIntegrationTestCase {


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
    void testFetchReviewStateByCodeAndLocaleLowercase() {
        List<AipReviewState> reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")
        assertEquals 1, reviewStateResult.size()
        assertEquals "Review needed", reviewStateResult[0].reviewStateName
        assertEquals "Y", reviewStateResult[0].reviewOngoingInd
        assertEquals "N", reviewStateResult[0].reviewSuccessInd
    }

    @Test
    void testFetchReviewStateByCodeAndLocale() {
        //when ReviewState is fetched for a locale configured in database (for example "es") other than default locale(en_US),
        //ReviewState of both the locales (es and en_US) is returned
        List<AipReviewState> reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "es")
        assertEquals 2,  reviewStateResult.size()
        assertEquals "Review needed",  reviewStateResult[0].reviewStateName
        assertEquals "Revisión necesaria", reviewStateResult[1].reviewStateName
    }

    @Test
    void testFetchReviewStateByDefaultLocale() {
        //when for the specified locale ReviewState is not available, ReviewState of default locale (en_US) is returned
        List<AipReviewState> reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "ABC")
        assertEquals 1, reviewStateResult.size()
        assertEquals "Review needed", reviewStateResult[0].reviewStateName

    }

    @Test
    void testFetchReviewStateByCodeAndLocaleUppercase() {
        List<AipReviewState>  reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "EN_US")
        assertEquals "Review needed", reviewStateResult[0].reviewStateName
    }

    @Test
    void testFetchNonDefaultReviewStates() {
        List<AipReviewState> reviewStateResult = AipReviewState.fetchNonDefaultReviewStates("en_US")
        assert reviewStateResult.size() > 0
        def reviewStateCodes = reviewStateResult.collect{it.reviewStateCode }
        assert reviewStateCodes.containsAll(['20', '30', '40', '50', '60', '70'])
        assertFalse(reviewStateCodes.contains('10'))
    }


}
