/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import groovy.test.GroovyAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import static groovy.test.GroovyAssert.shouldFail


@Integration
@Rollback
class ActionItemGroupIntegrationTests extends BaseIntegrationTestCase {


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
    void testFetchActionItemGroups() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        assertFalse actionItemGroups.isEmpty()
    }


    @Test
    void testFetchActionItemGroupById() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroupId = actionItemGroups[0].id
        def actionItemGroupTitle = actionItemGroups[0].title
        def actionItemGroup = ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
        assertEquals( actionItemGroupTitle, actionItemGroup.title )
    }


    @Test
    void testActionItemGroupToString() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups(  )
        def actionItemGroupId = actionItemGroups[0].id
        def expected = actionItemGroups[0].title
        ActionItemGroup actionItemGroupById = ActionItemGroup.fetchActionItemGroupById( actionItemGroupId )
        assertNotNull( actionItemGroups.toString() )
        assertFalse actionItemGroups.isEmpty()
        assertNotNull( actionItemGroupById.toString() )
        assertEquals( expected, actionItemGroupById.title )
    }


    @Test
    void testActionItemGroupHashCode() {
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def hashCode = actionItemGroups.hashCode()
        assertNotNull hashCode
    }


    @Test // duplicate group name in a folder
    void testActionItemGroupConstraint() { //title and folder pair must be unique
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        def actionItemGroupNewList = new ActionItemGroup()

        actionItemGroupNewList.title = actionItemGroup.title
        actionItemGroupNewList.folderId = actionItemGroup.folderId
        actionItemGroupNewList.description = actionItemGroup.description
        actionItemGroupNewList.postingInd = actionItemGroup.postingInd
        actionItemGroupNewList.status = actionItemGroup.status
        actionItemGroupNewList.version = actionItemGroup.version
        actionItemGroupNewList.dataOrigin = actionItemGroup.dataOrigin

        //shouldFail { actionItemGroupNewList.save(failOnError: true, flush: false) }
        // FIXME: Verify something
        //println actionItemGroupNewList.save(failOnError: true, flush: false)
    }


    @Test
    void testActionItemGroupConstraintDifferentFolder() { //title can be the same if in a different folder
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]

        def z = 0
        def keepGoing = true
        def otherFolderId
        while (keepGoing) {
            z++
            keepGoing = actionItemGroups[z].folderId == actionItemGroup.folderId
            if (!keepGoing) {
                otherFolderId = actionItemGroups[z].folderId
                break
            }
        }

        def actionItemGroupNewList = new ActionItemGroup()

        actionItemGroupNewList.title = actionItemGroup.title
        actionItemGroupNewList.name = actionItemGroup.name
        actionItemGroupNewList.folderId = otherFolderId
        actionItemGroupNewList.description = actionItemGroup.description
        actionItemGroupNewList.postingInd = actionItemGroup.postingInd
        actionItemGroupNewList.status = actionItemGroup.status
        actionItemGroupNewList.version = actionItemGroup.version
        actionItemGroupNewList.dataOrigin = actionItemGroup.dataOrigin

        def didSave = actionItemGroupNewList.save(failOnError: true, flush: false)
        assertEquals( actionItemGroup.title, didSave.title )
        assertEquals( otherFolderId, didSave.folderId )
        // version update?
    }


    @Test
    void testActionItemGroupStatusMaxSize() { //title and folder pair must be unique
        List<ActionItemGroup> actionItemGroups = ActionItemGroup.fetchActionItemGroups()
        def actionItemGroup = actionItemGroups[0]
        def actionItemGroupNewList = new ActionItemGroup()

        actionItemGroupNewList.title = "a unique title oifvh43"
        actionItemGroupNewList.folderId = actionItemGroup.folderId
        actionItemGroupNewList.description = actionItemGroup.description
        actionItemGroupNewList.postingInd = actionItemGroup.postingInd
        actionItemGroupNewList.status = "pendingstatusoverthe30characterlimit"
        actionItemGroupNewList.version = actionItemGroup.version
        actionItemGroupNewList.dataOrigin = actionItemGroup.dataOrigin

        shouldFail { actionItemGroupNewList.save( failOnError: true, flush: false ) }
    }
}
