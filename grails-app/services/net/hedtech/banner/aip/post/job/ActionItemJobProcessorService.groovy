/********************************************************************************
  Copyright 2017 Ellucian Company L.P. and its affiliates.
********************************************************************************/
package net.hedtech.banner.aip.post.job

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory


/**
 * ActionItemJobProcessorService is responsible for orchestrating the actionItem send in behalf of a actionItem job.
 */
class ActionItemJobProcessorService {
    private final Log log = LogFactory.getLog(this.getClass());
    def actionItemJobService
    def actionItemPerformPostService

    public void performActionItemJob( Long jobId ) {
        log.debug( "performed actionItem job with job id = ${jobId}." )

        ActionItemJob job = actionItemJobService.get( jobId )
        /*
        List<ActionItemRecipientData> recipientDataList = ActionItemRecipientData.fetchByReferenceId( job.referenceId )
        ActionItemRecipientData recipientData = recipientDataList.size() ? recipientDataList[0] : null
        */
        actionItemPerformPostService.postActionItems(  )
    }

}
