/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemGroupServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemGroupService


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
    void testFetchActionItemGroupsService() {
        List<ActionItemGroup> actionItemGroups = actionItemGroupService.listActionItemGroups()
        assertFalse actionItemGroups.isEmpty()
    }


    @Test
    void testFetchActionItemGroupByIdService() {
        List<ActionItemGroup> actionItemGroups = actionItemGroupService.listActionItemGroups()
        def actionItemGroupId = actionItemGroups[0].id
        def actionItemGroupTitle = actionItemGroups[0].title
        def actionItemGroup = actionItemGroupService.getActionItemGroupById( actionItemGroupId )
        assertEquals( actionItemGroupTitle, actionItemGroup.title )
    }


    @Test
    void testValidateSuccessCases() {
        ActionItemGroup actionItemGroup = new ActionItemGroup(
                title: 'title',
                name: 'name',
                description: 'description',
                postingInd: 'N',
                folderId: 1,
                status: 'P'
        )
        actionItemGroupService.preCreate( actionItemGroup )
        actionItemGroupService.preCreate( [domainModel: [
                title      : 'title',
                name       : 'name',
                description: 'description',
                postingInd : 'N',
                folderId   : 1,
                status     : 'P'
        ]] )

    }


    @Test
    void testValidateNullTitle() {

        try {
            actionItemGroupService.preCreate( [domainModel: [
                    title      : null,
                    name       : 'name',
                    description: 'description',
                    postingInd : 'N',
                    folderId   : 1,
                    status     : 'P'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'TitleCanNotBeNullError' )
        }
    }


    @Test
    void testValidateNullFolder() {

        try {
            actionItemGroupService.preCreate( [domainModel: [
                    title      : 'title',
                    name       : 'name',
                    description: 'description',
                    postingInd : 'N',
                    folderId   : null,
                    status     : 'P'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'FolderCanNotBeNullError' )
        }
    }


    @Test
    void testValidateNullStatus() {

        try {
            actionItemGroupService.preCreate( [domainModel: [
                    title      : 'title',
                    name       : 'name',
                    description: 'description',
                    postingInd : 'N',
                    folderId   : 1,
                    status     : null
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'StatusCanNotBeNullError' )
        }
    }


    @Test
    void testValidateMaxSize() {

        try {
            actionItemGroupService.preCreate( [domainModel: [
                    title      : 'title',
                    name       : 'name',
                    description: 'description',
                    postingInd : 'N',
                    folderId   : 1,
                    status     : 'MORETHANONECHAR'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'MaxSizeError' )
        }
    }


    @Test
    void testValidateInvalidInput() {

        try {
            actionItemGroupService.preCreate( [domainModel: [
                    title      : 'title',
                    name       : 'name',
                    description: 'description',
                    postingInd : 'N',
                    folderId   : '1XYZ', // Should be long
                    status     : 'P'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'ValidationError' )
        }
    }


    @Test
    void testValidateInvalidFolder() {

        try {
            actionItemGroupService.preCreate( [domainModel: [
                    title      : 'title',
                    name       : 'name',
                    description: 'description',
                    postingInd : 'N',
                    folderId   : -1,/// Invalid folder
                    status     : 'P'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'FolderDoesNotExist' )
        }
    }


    @Test
    void testValidateDuplicateGroup() {
        ActionItemGroup actionItemGroup = actionItemGroupService.listActionItemGroups()[0]
        try {
            actionItemGroupService.preCreate( [domainModel: [
                    title      : 'title',
                    name       : actionItemGroup.name,
                    postingInd : 'N',
                    description: 'description',
                    folderId   : actionItemGroup.folderId,
                    status     : 'P'
            ]] )
        } catch (ApplicationException e) {
            assertApplicationException( e, 'UniqueNameInFolderError' )
        }
    }
}
