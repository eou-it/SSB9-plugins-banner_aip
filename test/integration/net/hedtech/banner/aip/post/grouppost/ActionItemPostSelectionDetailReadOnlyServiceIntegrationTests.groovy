/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculation
import net.hedtech.banner.general.communication.population.CommunicationPopulationCalculationStatus
import net.hedtech.banner.general.communication.population.query.CommunicationPopulationQuery
import net.hedtech.banner.general.communication.population.query.CommunicationPopulationQueryVersion
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

import java.util.concurrent.TimeUnit


class ActionItemPostSelectionDetailReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostSelectionDetailReadOnlyService
    def communicationPopulationCompositeService
    def communicationPopulationQueryCompositeService
    def communicationFolderService

    @Before
    void setUp() {
        formContext = ['GUAGMNU', 'SELFSERVICE']
        //formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchSelectionIds() {

        def auth = selfServiceBannerAuthenticationProvider.authenticate( new UsernamePasswordAuthenticationToken( 'BCMADMIN', '111111' ) )
        SecurityContextHolder.getContext().setAuthentication( auth )

        CommunicationPopulationQuery populationQuery = communicationPopulationQueryCompositeService.createPopulationQuery( newPopulationQuery(
                "testPopForFetchSelectionIdsTest" ) )
        CommunicationPopulationQueryVersion queryVersion = communicationPopulationQueryCompositeService.publishPopulationQuery( populationQuery )
        populationQuery = queryVersion.query

        CommunicationPopulation population = communicationPopulationCompositeService.createPopulationFromQuery( populationQuery,
                "testPopulationForFetchSelectionIdsTest" )

        CommunicationPopulationCalculation populationCalculation = CommunicationPopulationCalculation.findLatestByPopulationIdAndCalculatedBy(
                population.id, 'BCMADMIN' )


        def isAvailable = {
            def theCalculation = CommunicationPopulationCalculation.get( it )
            println "in wait calc: " + theCalculation
            theCalculation.refresh()
            return theCalculation.status == CommunicationPopulationCalculationStatus.AVAILABLE
        }

        // Not getting to available. disabling for now
        //assertTrueWithRetry( isAvailable, populationCalculation.id, 15, 5 )
        assertTrueWithRetry( isAvailable, populationCalculation.id, 1, 5 )

        ActionItemPost aip = newAIP()
        aip.save()
        // lookup. save should have worked. Use "stopped" to avoid job getting processed?
        List<ActionItemPost> aipToUse = ActionItemPost.findAll()

        def result = actionItemPostSelectionDetailReadOnlyService.fetchSelectionIds( aipToUse[0]?.id )
        assert result.size() > 0
    }


    private def newAIP() {
        new ActionItemPost(
                populationListId: 1L,
                populationVersionId: 1L,
                postingName: "some name",
                postingActionItemGroupId: ActionItemGroup.findByName( 'Enrollment' ).id,
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
                folder: setUpDefaultFolder(  ),
                name: queryName,
                description: "test description",
                queryString: "select spriden_pidm from spriden where rownum <= ${maxRows} and spriden_change_ind is null"
        )

        return populationQuery
    }

}
