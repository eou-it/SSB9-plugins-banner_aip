/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemIntegrationTests extends BaseIntegrationTestCase {

    def actionItemService


    @Before
    public void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchActionItemsString() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemList = actionItems[0]
        assertNotNull( actionItemList.toString() )
        assertFalse actionItems.isEmpty()
    }


    @Test
    void testFetchActionItems() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        assertFalse actionItems.isEmpty()
    }


    @Test
    void testActionItemsHashCode() {

        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemList = actionItems[0]

        def result = actionItemList.hashCode()
        assertNotNull result

        def actionItemListObj = new ActionItem()
        assertNotNull actionItemListObj
    }


    @Test
    void testActionItemsEquals() {

        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItemsList = actionItems[0]
        def actionItemNewList = new ActionItem(
                active: actionItemsList.active,
                activityDate: actionItemsList.activityDate,
                createDate: actionItemsList.createDate,
                creatorId: actionItemsList.creatorId,
                dataOrigin: actionItemsList.dataOrigin,
                description: actionItemsList.description,
                title: actionItemsList.title,
                userId: actionItemsList.userId )
        long actionItemListId = actionItemsList.id
        long actionItemListVersion = actionItemsList.version
        actionItemNewList.id = actionItemListId
        actionItemNewList.version = actionItemListVersion

        def result = actionItemsList.equals( actionItemsList )
        assertTrue result

        result = actionItemsList.equals( actionItemNewList )
        assertTrue result

        result = actionItemsList.equals( null )
        assertFalse result

        def actionItemsListNull = new ActionItem( null )
        result = actionItemsList.equals( actionItemsListNull )
        assertFalse result

    }


    @Test
    // duplicate title in a folder
    void testActionItemConstraint() { //title and folder pair must be unique
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[0]
        def actionNew = new ActionItem()

        actionNew.title = actionItem.title
        actionNew.folderId = actionItem.folderId
        actionNew.active = actionItem.active
        actionNew.userId = actionItem.userId
        actionNew.activityDate = actionItem.activityDate
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin

        shouldFail { actionNew.save( failOnError: true, flush: false ) }
    }


    // TODO: more verify constriants
    @Test
    void testUniqueTitleFolderCombo() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.title = actionItem.title
        actionNew.folderId = actionItem.folderId
        actionNew.active = actionItem.active
        actionNew.userId = actionItem.userId
        actionNew.activityDate = actionItem.activityDate
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertFalse actionNew.validate()
        // TODO: verify something
        assertTrue (actionNew.errors.allErrors.codes[0].contains('actionItem.title.unique.error'))
    }


    @Test
    void testNullTitleError() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.title = null
        actionNew.folderId = actionItem.folderId
        actionNew.active = actionItem.active
        actionNew.userId = actionItem.userId
        actionNew.activityDate = actionItem.activityDate
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertFalse actionNew.validate()
        // TODO: verify something
        assertTrue (actionNew.errors.allErrors.codes[0].contains('actionItem.title.nullable.error'))
    }


    @Test
    void testEmptyTitleError() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.title = ''
        actionNew.folderId = actionItem.folderId
        actionNew.active = actionItem.active
        actionNew.userId = actionItem.userId
        actionNew.activityDate = actionItem.activityDate
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertFalse actionNew.validate()
        // TODO: verify something
        assertTrue (actionNew.errors.allErrors.codes[0].contains('actionItem.title.blank.error'))
    }
}