/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip


import net.hedtech.banner.service.ServiceBase

/**
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
        String reviewStateName
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale(code, locale)
        reviewStateName = reviewStateResult?.reviewStateName
    }

}
