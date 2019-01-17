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
        assert reviewStateResult.size() == 1
        assert reviewStateResult[0].reviewStateName == "Review needed"
        assert reviewStateResult[0].reviewOngoingInd == "Y"
        assert reviewStateResult[0].reviewSuccessInd == "N"
    }

    @Test
    void testFetchReviewStateByCodeAndLocale() {
        //when ReviewState is fetched for a locale configured in database (for example "es") other than default locale(en_US),
        //ReviewState of both the locales (es and en_US) is returned
        List<AipReviewState> reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "es")
        assert reviewStateResult.size() == 2
        assert reviewStateResult[0].reviewStateName == "Review needed"
        assert reviewStateResult[1].reviewStateName == "Revisi��n necesaria"
    }

    @Test
    void testFetchReviewStateByDefaultLocale() {
        //when for the specified locale ReviewState is not available, ReviewState of default locale (en_US) is returned
        List<AipReviewState> reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "fr_CA")
        assert reviewStateResult.size() == 1
        assert reviewStateResult[0].reviewStateName == "Review needed"

    }

    @Test
    void testFetchReviewStateByCodeAndLocaleUppercase() {
        List<AipReviewState>  reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "EN_US")
        assert reviewStateResult[0].reviewStateName == "Review needed"
    }

    @Test
    void testFetchNonDefaultReviewStates() {
        List<AipReviewState> reviewStateResult = AipReviewState.fetchNonDefaultReviewStates("en_US")
        assert reviewStateResult.size() == 4
        def reviewStateCodes = reviewStateResult.collect{it.reviewStateCode }
        def list = ['20', '30', '40', '50']
        assert reviewStateCodes==list
    }


}
