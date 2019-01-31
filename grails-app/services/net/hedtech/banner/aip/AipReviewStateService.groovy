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
     * Here is how it is evaluated,
     * 1. check if chosen locale is a "two-parter" or "sub-locale" (e.g. fr_CA), then take the initial part of locale as primary locale
     * 2. get Review State records for a given locale, primary locale, default locale (en_US)
     * 3. check if Review State record available for chosen locale, if so return the record
     * 4. if not found for chosen locale, then check for primary locale
     * 5. if not found primary locale as well, then check for default locale
     * 6. finaly if not found for any locale, then return null
     * @params code Code of Review State
     * @params locale User locale
     * @returns String Review State Name
     */
     String fetchReviewStateNameByCodeAndLocale(String code, String locale) {
        def primaryLocale = locale.split("_")[0]

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
     * Here is how it is evaluated,
     * 1. check if chosen locale is a "two-parter" or "sub-locale" (e.g. fr_CA), then take the initial part of locale as primary locale
     * 2. get Review State records for a given locale, primary locale, default locale (en_US)
     * 3. check if Review State records available for chosen locale, if so return the record list
     * 4. if not found for chosen locale, then check for primary locale and return the record list
     * 5. if not found primary locale as well, then check for default locale and return the record list
     * 6. finaly if not found for any locale, then return empty list
     * @params locale User locale
     * @returns List of Review States
     */
    List<AipReviewState> fetchNonDefaultReviewStates(String locale) {
        def primaryLocale =  locale.split("_")[0]

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
