/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip.post.grouppost.automation

/**
 * Interface for objects that perform teardown activities in support of unit testing.
 *
 */
public interface TearDownPerformer {

    /**
     * Performs teardown activities.
     */
    public void execute() throws Exception
}
