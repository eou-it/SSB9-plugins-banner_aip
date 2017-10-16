/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

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
    void testDummy() {
        println "CRR: test ran"
        assertTrue 1 == 1
    }

    @Test
    void testActionPostConstraints() {
        println 'CRR finish tests'
        def actionItemPost = newAIP(  )
        println 'CRR finish tests'
        println actionItemPost
        //actionItemPost.save()

    }

    private def newAIP(  ) {
        def aigs = new ActionItemPost(
                //id: 1L,
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingDeleteIndicator: false,
                postingScheduleType: "some type",
                postingDisplayStartDate: new Date(),
                postingDisplayEndDate: new Date(),
                postingScheduleDeleteTime: new Date(), // What is this?
                postingCreatorId: 'me',
                postingScheduleDateTime: new Date(),
                populationRegenerateIndicator: false,
                postingCurrentState: ActionItemPostExecutionState.New,
                postingStartedDate: null,
                postingStopDate: null,
                postingJobId: "la43j45h546k56g6f6r77a7kjfn",
                populationCalculationId: 1L,
                postingErrorCode: ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                postingErrorText: null,
                postingGroupId: null,
                postingParameterValues: null,
                lastModified: new Date(),
                lastModifiedBy: 'testUser',
                version: 1L,
                dataOrigin: 'BANNER',
                vpdiCode: null
        )
        return aigs
    }
}