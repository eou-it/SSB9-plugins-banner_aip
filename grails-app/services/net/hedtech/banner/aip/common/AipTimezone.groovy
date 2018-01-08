/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.common

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * AIP TimeZone class
 */
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
class AipTimezone implements Serializable {
    String stringOffset;
    int offset;
    String displayName
    String displayNameWithoutOffset
    String timezoneId;
}
