package net.hedtech.banner.aip
/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

import net.hedtech.banner.aip.ActionItemStatus
import net.hedtech.banner.aip.UserActionItem
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class AipReviewStateServiceIntegrationTests extends BaseIntegrationTestCase {

    def aipReviewStateService


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
    void testFetchReviewStateNameByCodeAndLocale() {
        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10','EN_US')
        assert reviewStateName == "Review needed"

        reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10','es')
        assert reviewStateName == "Revisi��n necesaria"
    }

    @Test
    void testFetchReviewStateNameOfDefaultLocale() {
        //when for the specified locale ReviewState is not available, ReviewState of default locale (en_US) is returned
        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10','fr_CA')
        assert reviewStateName == "Review needed"
    }

    @Test
    void testReviewStateDoesNotExist() {
        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('11','EN_US')
        assertNull( reviewStateName )

        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")[0]
        reviewStateResult.locale = 'AB'
        reviewStateResult.save(flush: true, failOnError: true)
        reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10','gb')
        assertNull reviewStateName
    }

    @Test
    void testFetchNonDefaultReviewStates() {
        List<AipReviewState> reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("en_US")
        assert reviewStateResult.size() == 4
        def reviewStateCodes = reviewStateResult.collect{it.reviewStateCode }
        def list = ['20', '30', '40', '50']
        assert reviewStateCodes==list
    }


}
