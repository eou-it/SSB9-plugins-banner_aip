/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *********************************************************************************/
package net.hedtech.banner.aip.post

/**
 * An enumeration of error codes for action item jobs and items.
 * These values can never change.
 */
enum ActionItemErrorCode implements Serializable {

    INVALID_DATA_FIELD, //Failure evaluating a data field query
    MISSING_DATA_FIELD, // sql returns no data.
    DATA_FIELD_SQL_ERROR,
    UNKNOWN_ERROR; //Unknown Error

    /**
     * Returns a set of all ActionItemErrorCode enum values.
     * @return Set<ActionItemErrorCode> the set of ActionItemErrorCode
     */
    public Set<ActionItemErrorCode> set() {
        return EnumSet.range( ActionItemErrorCode.INVALID_DATA_FIELD, ActionItemErrorCode.UNKNOWN_ERROR );
    }
}
