/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.aip.post.grouppost.ActionItemPost
import net.hedtech.banner.aip.post.grouppost.ActionItemPostExecutionState
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostIntegrationTests extends BaseIntegrationTestCase {
    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testActionPostConstraints() {
        def actionItemPost = newAIP()
        //actionItemPost.save()

    }


    @Test
    void testDuplicate() {
        def actionItemPost = newAIP()
        actionItemPost.save()
    }


    private def newAIP() {
        new ActionItemPost(
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingActionItemGroupId: ActionItemGroup.findByName('Enrollment').id,
                postingDeleteIndicator: false,
                postingScheduleType: "some type",
                postingCreationDateTime: new Date(),
                postingDisplayStartDate: new Date(),
                postingDisplayEndDate: new Date(),
                postingCreatorId: 'me',
                postingScheduleDateTime: new Date(),
                populationRegenerateIndicator: false,
                postingCurrentState: ActionItemPostExecutionState.New,
                postingStartedDate: null,
                postingStopDate: null,
                aSyncJobId: "la43j45h546k56g6f6r77a7kjfn",
                populationCalculationId: 1L,
                postingErrorCode: ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                postingErrorText: null,
                aSyncGroupId: null,
                postingParameterValues: null,
                lastModified: new Date(),
                lastModifiedBy: 'testUser',
                dataOrigin: 'BANNER'
                )
    }
}
