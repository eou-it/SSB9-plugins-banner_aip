/*******************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback
import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationVersion
import net.hedtech.banner.general.communication.population.query.CommunicationPopulationQuery
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

import java.util.concurrent.TimeUnit
@Integration
@Rollback
class ActionItemPostSelectionDetailReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostSelectionDetailReadOnlyService
    def communicationFolderService
    def actionItemPostService


    @Before
    void setUp() {
        formContext = ['SELFSERVICE']
        super.setUp()
        loginSSB( 'AIPADM001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void testFetchSelectionIds() {
        CommunicationPopulation population = CommunicationPopulation.findAllByPopulationName( 'AIP Student Population 1' )[0]
        CommunicationPopulationVersion populationVersion = CommunicationPopulationVersion.findByPopulationId( population.id )[0]
        ActionItemPost aip = newAIP( populationVersion.id )
        aip = actionItemPostService.create( aip )
        def result = actionItemPostSelectionDetailReadOnlyService.fetchSelectionIds( aip.id )
        assert result.size() > 0
    }


    private def newAIP( populationVersionId ) {
        new ActionItemPost(
                populationListId: 1L,
                populationVersionId: populationVersionId,
                postingName: "some name",
                postingActionItemGroupId: ActionItemGroup.findByName( 'Enrollment' ).id,
                postingDeleteIndicator: false,

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


    private void assertTrueWithRetry( Closure booleanClosure, Object arguments, long maxAttempts, int pauseBetweenAttemptsInSeconds = 5 ) {
        boolean result = false
        for (int i = 0; i < maxAttempts; i++) {
            result = booleanClosure.call( arguments )
            if (result) {
                break
            } else {
                TimeUnit.SECONDS.sleep( pauseBetweenAttemptsInSeconds )
            }
        }
        assertTrue( result )
    }


    private CommunicationFolder setUpDefaultFolder() {
        CommunicationFolder defaultFolder = CommunicationFolder.findByName( "ActionItemPostSelectionDetailReadOnlyServiceIntegrationTests" )
        if (!defaultFolder) {
            defaultFolder = new CommunicationFolder( name: "ActionItemPostSelectionDetailReadOnlyServiceIntegrationTests", description: "integration test" )
            defaultFolder = communicationFolderService.create( defaultFolder )
        }
        return defaultFolder
    }


    private def newPopulationQuery( String queryName, int maxRows = 5 ) {
        def populationQuery = new CommunicationPopulationQuery(
                // Required fields
                folder: setUpDefaultFolder(),
                name: queryName,
                description: "test description",
                queryString: "select spriden_pidm from spriden where rownum <= ${maxRows} and spriden_change_ind is null"
        )

        return populationQuery
    }

}
