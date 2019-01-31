/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.aip.ActionItemStatus
import net.hedtech.banner.aip.UserActionItem
import net.hedtech.banner.aip.common.AIPConstants
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
        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'EN_US')
        assert "Review needed", reviewStateName

        reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'es')
        assert "Revisi��n necesaria", reviewStateName
    }

    @Test
    void testFetchReviewStateNameOfDefaultLocale() {
        //when for the specified locale ReviewState is not available, ReviewState of default locale (en_US) is returned
        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'ABC')
        assert "Review needed", reviewStateName
    }

    @Test
    void testReviewStateDoesNotExist() {
        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('11', 'EN_US')
        assertNull(reviewStateName)

        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")[0]
        reviewStateResult.locale = 'AB'
        reviewStateResult.save(flush: true, failOnError: true)
        reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'gb')
        assertNull reviewStateName
    }

    @Test
    void testFetchReviewStateNameForFrenchFranceLocale() {
        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'fr_FR')
        assert "Révision requise", reviewStateName

        reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'es_MX')
        assert "Revisi��n necesaria", reviewStateName

        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "fr")[0]
        reviewStateResult.locale = 'AB'
        reviewStateResult.save(flush: true, failOnError: true)

        reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'fr_FR')
        assert "Review needed", reviewStateName
    }

    @Test
    void testFetchReviewStateNameNull() {
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")[0]
        reviewStateResult.locale = 'CD'
        reviewStateResult.save(flush: true, failOnError: true)

        reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "fr")[0]
        reviewStateResult.locale = 'AB'
        reviewStateResult.save(flush: true, failOnError: true)

        String reviewStateName = aipReviewStateService.fetchReviewStateNameByCodeAndLocale('10', 'fr_FR')
        assertNull reviewStateName
    }

    @Test
    void testFetchNonDefaultReviewStates() {
        List<AipReviewState> reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("pt")
        assert reviewStateResult.size() == 6
        def reviewStateCodes = reviewStateResult.collect { it ->
            if (it.locale.toUpperCase() == "PT") {
                it.reviewStateCode
            }
        }
        reviewStateCodes = reviewStateResult.collect { it.reviewStateCode }
        assert reviewStateCodes.containsAll(['20', '30', '40', '50', '60', '70'])
        assertFalse(reviewStateCodes.contains('10'))
    }

    @Test
    void testFetchNonDefaultReviewStatesLocaleNotExists() {
        List<AipReviewState> reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("ABC")
        assert reviewStateResult.size() == 6
        def reviewStateCodes = reviewStateResult.collect { it ->
            if (it.locale.toUpperCase() == AIPConstants.DEFAULT_LOCALE) {
                it.reviewStateCode
            }
        }
        assert reviewStateCodes.containsAll(['20', '30', '40', '50', '60', '70'])
        assertFalse(reviewStateCodes.contains('10'))
    }

    @Test
    void testFetchNonDefaultReviewStatesDefaultLocaleNotExists() {
        List<AipReviewState> reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("en_US")

        reviewStateResult.each() { it ->
            if (it.locale.toUpperCase() == "EN_US") {
                it.locale = 'ABC'
                it.save(flush: true, failOnError: true)
            }
        }

        reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("en_US")
        assert reviewStateResult.isEmpty()
    }

    @Test
    void testFetchNonDefaultReviewStatesFrenchFrance() {

        List<AipReviewState> reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("fr_FR")
        assert reviewStateResult.size() > 0
        def fr_FR_reviewStateCodes = reviewStateResult.collect { it ->
            if (it.locale.toUpperCase() == "FR_FR") {
                it.reviewStateCode
            }
        }
        assert fr_FR_reviewStateCodes.containsAll([null, null, null, null, null, null])

        def fr_reviewStateCodes = reviewStateResult.collect { it ->
            if (it.locale.toUpperCase() == "FR") {
                it.reviewStateCode
            }
        }

        assert fr_reviewStateCodes.containsAll(['20', '30', '40', '50', '70'])
        assertFalse(fr_reviewStateCodes.contains('10'))
    }

    @Test
    void testFetchNonDefaultReviewStatesFrenchFranceDefaultToDefaultLocale() {
        List<AipReviewState> reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("fr")

        reviewStateResult.each() { it ->
            if (it.locale.toUpperCase() == "FR") {
                it.locale = 'ABC'
                it.save(flush: true, failOnError: true)
            }
        }

        reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("fr_FR")
        assert reviewStateResult.size() > 0

        def fr_FR_reviewStateCodes = reviewStateResult.collect { it ->
            if (it.locale.toUpperCase() == "FR_FR") {
                it.reviewStateCode
            }
        }
        assert fr_FR_reviewStateCodes.containsAll([null, null, null, null, null, null])

        def fr_reviewStateCodes = reviewStateResult.collect { it ->
            if (it.locale.toUpperCase() == "FR") {
                it.reviewStateCode
            }
        }
        assert fr_reviewStateCodes.containsAll([null, null, null, null, null, null])


        def default_reviewStateCodes = reviewStateResult.collect { it ->
            if (it.locale.toUpperCase() == AIPConstants.DEFAULT_LOCALE) {
                it.reviewStateCode
            }
        }
        assert default_reviewStateCodes.size() > 0
        assert default_reviewStateCodes.containsAll(['20', '30', '40', '50', '60', '70'])
        assertFalse(default_reviewStateCodes.contains('10'))
    }

    @Test
    void testFetchNonDefaultReviewStatesForFrenchFranceDefaultLocaleNotExists() {
        List<AipReviewState> reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("en_US")

        reviewStateResult.each() { it ->
            if (it.locale.toUpperCase() == "EN_US") {
                it.locale = 'ABC'
                it.save(flush: true, failOnError: true)
            }
        }

        reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("fr")

        reviewStateResult.each() { it ->
            if (it.locale.toUpperCase() == "FR") {
                it.locale = 'XYZ'
                it.save(flush: true, failOnError: true)
            }
        }

        reviewStateResult = aipReviewStateService.fetchNonDefaultReviewStates("fr_FR")
        assert reviewStateResult.isEmpty()
    }

}