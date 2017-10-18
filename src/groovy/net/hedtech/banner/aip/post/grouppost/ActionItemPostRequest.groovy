/*******************************************************************************
 Copyright 2014 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString


@EqualsAndHashCode
@ToString
class ActionItemPostRequest implements Serializable {
    String name
    Long populationId
    String referenceId
    Long postGroupId
    Boolean recalculateOnPost
    Date scheduledStartDate
    Date displayStartDate
    Date displayEndDate
    List<Long> actionItemIds
}
