/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

@Integration
@Rollback
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
    void testFetchActionItemNames() {
        List<ActionItem> actionItems = ActionItem.fetchActionItemIdAndName()
        assert actionItems.size() > 0
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
        assertFalse actionNew.validate()
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
        assertFalse actionNew.validate()
        println actionNew.errors.allErrors
        assertTrue( actionNew.errors.allErrors.codes[0].contains( 'actionItem.status.nullable.error' ) )
    }


    @Test
    void testNullFolderError() {
        List<ActionItem> actionItems = ActionItem.fetchActionItems()
        def actionItem = actionItems[7]
        def actionNew = new ActionItem()
        actionNew.name = "Action Item name for testNullFolderError"
        actionNew.title = 'a title f984h'
        actionNew.folderId = null
        actionNew.status = 'A'
        actionNew.description = actionItem.description
        actionNew.creatorId = actionItem.creatorId
        actionNew.createDate = actionItem.createDate
        actionNew.dataOrigin = actionItem.dataOrigin
        assertFalse actionNew.validate()
        println actionNew.errors.allErrors
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
        assertFalse actionNew.validate()
        assertTrue( actionNew.errors.allErrors.codes[0].contains( 'actionItem.title.blank.error' ) )
    }
}
