/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class ActionItemIntegrationTests extends BaseIntegrationTestCase {


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
        def actionItem = actionItems[0]
        def actionItemNew = new ActionItem(
                status: actionItem.status,
                createDate: actionItem.createDate,
                creatorId: actionItem.creatorId,
                folderId: actionItem.folderId,
                description: actionItem.description,
                postedIndicator: actionItem.postedIndicator,
                lastModified: actionItem.lastModified,
                lastModifiedBy: actionItem.lastModifiedBy,
                dataOrigin: actionItem.dataOrigin,
                version: actionItem.version,
                name: actionItem.name,
                title: actionItem.title)
        long actionItemListId = actionItem.id
        long actionItemListVersion = actionItem.version
        actionItemNew.id = actionItemListId
        actionItemNew.version = actionItemListVersion

        def result = actionItem.equals( actionItem )
        assertTrue result

        result = actionItem.equals( actionItemNew )
        assertTrue result

        result = actionItem.equals( null )
        assertFalse result

        def actionItemsListNull = new ActionItem( null )
        result = actionItem.equals( actionItemsListNull )
        assertFalse result

    }


    @Test
    void testUniqueTitleFolderComboMethod() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.title = actionItem.title
        actionNew.name = actionItem.name
        actionNew.folderId = actionItem.folderId
        actionNew.status = actionItem.status
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertTrue ActionItem.existsSameNameInFolder( actionItem.folderId, actionItem.name )
    }


    @Test
    void testNullTitleError() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.title = null
        actionNew.folderId = actionItem.folderId
        actionNew.status = actionItem.status
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertFalse actionNew.validate()
        // TODO: verify something
        assertTrue( actionNew.errors.allErrors.codes[0].contains( 'actionItem.title.nullable.error' ) )
    }


    @Test
    void testNullStatusError() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.name = 'a title f984h'
        actionNew.title = 'a title f984h'
        actionNew.folderId = actionItem.folderId
        actionNew.status = null
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertFalse actionNew.validate()
        // TODO: verify something
        assertTrue( actionNew.errors.allErrors.codes[0].contains( 'actionItem.status.nullable.error' ) )
    }


    @Test
    void testNullFolderError() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.title = 'a title f984h'
        actionNew.folderId = null
        actionNew.status = 'Pending'
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertFalse actionNew.validate()
        // TODO: verify something
        assertTrue( actionNew.errors.allErrors.codes[0].contains( 'actionItem.folderId.nullable.error' ) )
    }


    @Test
    void testEmptyTitleError() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()

        actionNew.title = ''
        actionNew.folderId = actionItem.folderId
        actionNew.status = actionItem.status
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        //
        assertFalse actionNew.validate()
        // TODO: verify something
        assertTrue( actionNew.errors.allErrors.codes[0].contains( 'actionItem.title.blank.error' ) )
    }
}
