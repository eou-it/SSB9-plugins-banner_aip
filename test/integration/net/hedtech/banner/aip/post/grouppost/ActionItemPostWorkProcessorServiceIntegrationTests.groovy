package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemPostWorkProcessorServiceIntegrationTests extends BaseIntegrationTestCase{

    def actionItemPostWorkProcessorService

    @Before
        public void setUp() {
            formContext = ['GUAGMNU']
            super.setUp()
        }


        @After
        public void tearDown() {
            super.tearDown()
        }

    // TODO: try posting to bad pidm
    // TODO: try posting to date range overlap
    // TODO: try posting with null actionitemid
    // TODO: post a set of valid items and verify logged info
    // TODO: post a set of items, some violating date range rule, and verify logged info

    // TODO: try posting to date range overlap
    @Test
    // FIXME: not working
        void testPerformGroupSendItem() {
        ActionItemPostWork actionItemPostWork = new ActionItemPostWork()
        actionItemPostWork.referenceId = 'somestringfortestinglswefhihvciewranc'
        actionItemPostWork.currentExecutionState = ActionItemPostWorkExecutionState.Ready
        ActionItemPost aip = newAIP(  )
        aip.postingCurrentState = ActionItemPostExecutionState.New
        aip.save()
        actionItemPostWork.actionItemGroupSend = aip
        actionItemPostWork.save()
        println actionItemPostWork
        actionItemPostWorkProcessorService.performPostItem( actionItemPostWork );
    }


    private def newAIP() {
        def aigs = new ActionItemPost(
                //id: 1L,
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingDeleteIndicator: false,
                postingScheduleType: "some type",
                postingDisplayStartDate: new Date(),
                postingDisplayEndDate: new Date(),
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
