/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip

import net.hedtech.banner.aip.common.AIPConstants
import net.hedtech.banner.i18n.MessageHelper
import net.hedtech.banner.service.ServiceBase

/****
 * AIP Review State Services.
 */
class AipReviewStateService extends ServiceBase {

    /**
     * Returns Review State record for the combination of code and locale.
     * @params code Code of Review State
     * @params locale User locale
     * @returns String Review State Name
     */
     String fetchReviewStateNameByCodeAndLocale(String code, String locale) {
        def primaryLocale
        if(locale.contains("_")) {
            primaryLocale = locale.substring(0, locale.indexOf("_"))
        }

        List<AipReviewState> reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale(code, locale, primaryLocale)
        AipReviewState aipReviewState = reviewStateResult.find {it->
            it.locale.toUpperCase() == locale.toUpperCase()
        }

        if(!aipReviewState) {
            aipReviewState = reviewStateResult.find { it ->
                it.locale.toUpperCase() == primaryLocale.toUpperCase()
            }
        }

        if(!aipReviewState) {
            aipReviewState = reviewStateResult.find { it ->
                it.locale.toUpperCase() == AIPConstants.DEFAULT_LOCALE
            }
        }

        aipReviewState?.reviewStateName
     }

    /**
     * Returns list of Review States without the default code.     *
     * @params locale User locale
     * @returns List of Review States
     */
    List<AipReviewState> fetchNonDefaultReviewStates(String locale) {
        def primaryLocale
        if(locale.contains("_")) {
            primaryLocale = locale.substring(0, locale.indexOf("_"))
        }

        List<AipReviewState> reviewStateResult = AipReviewState.fetchNonDefaultReviewStates(locale, primaryLocale)

        List<AipReviewState> localeReviewStateResult = new ArrayList<AipReviewState>()
        List<AipReviewState> primaryLocaleReviewStateResult = new ArrayList<AipReviewState>()
        List<AipReviewState> defaultLocaleReviewStateResult = new ArrayList<AipReviewState>()

        reviewStateResult.each() { it ->
            if(it.locale.toUpperCase() == locale.toUpperCase()) {
                localeReviewStateResult.add(it)
            }
        }

        if(!localeReviewStateResult && primaryLocale) {
            reviewStateResult.each() { it ->
                if (it.locale.toUpperCase() == primaryLocale.toUpperCase()) {
                    primaryLocaleReviewStateResult.add(it)
                }
            }
        }

        if(!localeReviewStateResult && !primaryLocaleReviewStateResult) {
            reviewStateResult.each() { it ->
                if (it.locale.toUpperCase() == AIPConstants.DEFAULT_LOCALE) {
                    defaultLocaleReviewStateResult.add(it)
                }
            }
        }

        localeReviewStateResult ? localeReviewStateResult : (primaryLocaleReviewStateResult ? primaryLocaleReviewStateResult :
                (defaultLocaleReviewStateResult ? defaultLocaleReviewStateResult : []))
    }
}
