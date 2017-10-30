/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post.grouppost.automation

import net.hedtech.banner.general.asynchronous.task.AsynchronousTaskProcessingEngine
import org.springframework.beans.factory.annotation.Required

/**
 * Teardown performer for shutting down the notification manager while
 * table teardown is in progress.
 *
 *
 */
public class ActionItemPostProcessingEngineStartupPerformer implements TearDownPerformer {

    private AsynchronousTaskProcessingEngine jobProcessingEngine


    /**
     * Performs teardown activities.
     */
    public void execute() throws Exception {
        jobProcessingEngine.startRunning()
    }


    /**
     * Sets the job processing engine to shutdown.
     */
    @Required
    public void setJobProcessingEngine( AsynchronousTaskProcessingEngine jobProcessingEngine ) {
        this.jobProcessingEngine = jobProcessingEngine
    }

}
