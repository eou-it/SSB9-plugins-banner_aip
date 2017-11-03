/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.aip.ActionItemGroupAssign
import net.hedtech.banner.aip.post.ActionItemErrorCode
import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.general.communication.folder.CommunicationFolder
import net.hedtech.banner.general.communication.population.CommunicationPopulation
import net.hedtech.banner.general.communication.population.query.CommunicationPopulationQuery
import net.hedtech.banner.general.communication.population.query.CommunicationPopulationQueryVersion
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken

class ActionItemPostServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemPostService
    def communicationPopulationQueryCompositeService
    def actionItemProcessingCommonService
    def communicationFolderService


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
    void preCreateValidationNullEmptyInput() {
        try {
            actionItemPostService.preCreateValidation( null )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.insufficient.request' )
        }
        try {
            actionItemPostService.preCreateValidation( [:] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.insufficient.request' )
        }
    }


    @Test
    void preCreateValidationNoJobName() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   // name                  : "some name"
        ]
        try {
            actionItemPostService.preCreateValidation( map )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.no.posting.job.name' )
        }
    }


    protected def newPopulationQuery( String queryName, int maxRows = 5 ) {
        def defaultFolder = CommunicationFolder.findByName( "ActionItemPostCompositeServiceTests" )
        if (!defaultFolder) {
            defaultFolder = new CommunicationFolder( name: "ActionItemPostCompositeServiceTests", description: "integration test" )
            defaultFolder = communicationFolderService.create( defaultFolder )
        }
        def populationQuery = new CommunicationPopulationQuery(
                // Required fields
                folder: defaultFolder,
                name: queryName,
                description: "test description",
                queryString: "select spriden_pidm from spriden where rownum <= ${maxRows} and spriden_change_ind is null"
        )
        return populationQuery
    }


    @Test
    void preCreateValidationDuplicateJobName() {
        loginSSB( 'CSRADM001', '111111' )
        def population = actionItemProcessingCommonService.fetchPopulationListForSend( '%p', [max: 10, offset: 0] )
        def actionItemGroups = ActionItemGroup.findAll()
        def actionItemGroup = actionItemGroups[0]
        def requestMap = [populationId                 : population.id[0],
                          name                         : "some name",
                          postingDisplayStartDate      : new Date(),
                          postingDisplayEndDate        : new Date(),
                          postingCreatorId             : 'me',
                          postingScheduleDateTime      : new Date(),
                          populationRegenerateIndicator: false,
                          postingCurrentState          : ActionItemPostExecutionState.New,
                          postingStartedDate           : null,
                          postingStopDate              : null,
                          postingJobId                 : "la43j45h546k56g6f6r77a7kjfn",
                          populationCalculationId      : 1L,
                          postingErrorCode             : ActionItemErrorCode.DATA_FIELD_SQL_ERROR,
                          postingErrorText             : null,
                          postGroupId                  : actionItemGroup.id,
                          postingParameterValues       : null,
                          postNow                      : true,
                          lastModified                 : new Date(),
                          postingCreationDateTime      : new Date(),
                          lastModifiedBy               : 'testUser',
                          dataOrigin                   : 'BANNER']
        def obj = new ActionItemPost(
                populationListId: requestMap.populationId,
                postingActionItemGroupId: requestMap.postGroupId,
                postingName: requestMap.name,
                postingDisplayStartDate: new Date(),
                postingDisplayEndDate: new Date(),
                postingScheduleDateTime: requestMap.scheduledStartDate ? new Date() : null,
                postingCreationDateTime: new Date(),
                populationRegenerateIndicator: false,
                postingDeleteIndicator: false,
                postingCreatorId: 'ORACLE_ID',
                postingCurrentState: requestMap.postNow ? ActionItemPostExecutionState.Queued : (requestMap.scheduled ? ActionItemPostExecutionState.Scheduled : ActionItemPostExecutionState.New),
                )
        try {
            actionItemPostService.create( obj )
            actionItemPostService.preCreateValidation( requestMap )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.job.name.already.defined' )
        }
    }


    @Test
    void preCreateValidationNoGroup() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   name               : "some name",
                   postGroupId        : null,
        ]
        try {
            actionItemPostService.preCreateValidation( map )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.no.group' )
        }
    }


    @Test
    void preCreateValidationNoActionItemIds() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   name               : "some name",
                   postGroupId        : 1,
                   actionItemIds      : []

        ]
        try {
            actionItemPostService.preCreateValidation( map )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.no.action.item' )
        }
    }


    @Test
    void preCreateValidationNoPopulationName() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   name               : "some name",
                   postGroupId        : 1,
                   actionItemIds      : [1, 2],
                   populationId       : null

        ]
        try {
            actionItemPostService.preCreateValidation( map )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.no.population.name' )
        }
    }


    @Test
    void preCreateValidationNoDisplayStartDate() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   name               : "some name",
                   postGroupId        : 1,
                   actionItemIds      : [1, 2],
                   populationId       : 2,
                   displayStartDate   : null

        ]
        try {
            actionItemPostService.preCreateValidation( map )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.no.display.start.date' )
        }
    }


    @Test
    void preCreateValidationNoDisplayEndDate() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   name               : "some name",
                   postGroupId        : 1,
                   actionItemIds      : [1, 2],
                   populationId       : 2,
                   displayStartDate   : new Date(),
                   displayEndDate     : null


        ]
        try {
            actionItemPostService.preCreateValidation( map )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.no.display.end.date' )
        }
    }


    @Test
    void preCreateValidationNoSchedule() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   name               : "some name",
                   postGroupId        : 1,
                   actionItemIds      : [1, 2],
                   populationId       : 2,
                   displayStartDate   : new Date(),
                   displayEndDate     : new Date(),
        ]
        try {
            actionItemPostService.preCreateValidation( map )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'preCreate.validation.no.schedule' )
        }
    }


    @Test
    void preCreateValidationPassed() {
        def map = [populationId       : 1L,
                   populationVersionId: 1L,
                   name               : "some name",
                   postGroupId        : 1,
                   actionItemIds      : [1, 2],
                   populationId       : 2,
                   displayStartDate   : new Date(),
                   displayEndDate     : new Date(),
                   postNow            : true
        ]
        actionItemPostService.preCreateValidation( map )
        assert 1 == 1
    }
}
