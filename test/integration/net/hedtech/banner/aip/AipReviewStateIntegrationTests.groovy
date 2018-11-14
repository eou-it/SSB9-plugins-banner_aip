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
    void testfetchReviewStateByCodeAndLocaleLowercase() {
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")
        assert reviewStateResult.reviewStateName == "Review needed"
        assert reviewStateResult.reviewOngoingInd == "Y"
        assert reviewStateResult.reviewSuccessInd == "N"
        assert reviewStateResult.defaultInd == "Y"
        assert reviewStateResult.activeInd == "Y"
    }

    @Test
    void testfetchReviewStateByCodeAndLocaleUppercase() {
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "EN_US")
        assert reviewStateResult.reviewStateName == "Review needed"
    }


}
