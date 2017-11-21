/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString


@EqualsAndHashCode
@ToString
class ActionItemPostRequest implements Serializable {
    String postingName
    Long populationId
    String referenceId
    Long postingActionItemGroupId
    Boolean populationRegenerateIndicator
    Date scheduledStartDate
    Date displayStartDate
    Date displayEndDate
    List<Long> actionItemIds
}
