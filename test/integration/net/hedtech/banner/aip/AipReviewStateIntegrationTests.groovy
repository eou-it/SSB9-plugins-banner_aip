/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
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
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")
        assert reviewStateResult.reviewStateName == "Review needed"
        assert reviewStateResult.reviewOngoingInd == "Y"
        assert reviewStateResult.reviewSuccessInd == "N"
    }

    @Test
    void testFetchReviewStateByCodeAndLocaleUppercase() {
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "EN_US")
        assert reviewStateResult.reviewStateName == "Review needed"
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
