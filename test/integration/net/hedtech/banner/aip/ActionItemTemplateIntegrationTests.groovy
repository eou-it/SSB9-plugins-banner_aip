/********************************************************************************
  Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemTemplateIntegrationTests extends BaseIntegrationTestCase {

    def actionItemTemplateService


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
    void testFetchActionItemTemplatesString() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        assertNotNull( actionItemTemplate.toString() )
        assertFalse actionItemTemplates.isEmpty()
    }


    @Test
    void testFetchActionItemTemplates() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        assertFalse actionItemTemplates.isEmpty()
    }


    @Test
    void testActionItemTemplatesHashCode() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        def result = actionItemTemplate.hashCode()
        assertNotNull result

        def actionItemTemplateListObj = new ActionItemTemplate()
        assertNotNull actionItemTemplateListObj
    }


    @Test
    void testNullTitleError() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        def actionItemTemplateNew = new ActionItemTemplate()

        actionItemTemplateNew.title = null
        actionItemTemplateNew.sourceInd = actionItemTemplate.sourceInd
        actionItemTemplateNew.lastModifiedBy = actionItemTemplate.lastModifiedBy
        actionItemTemplateNew.systemRequired = actionItemTemplate.systemRequired
        actionItemTemplateNew.activeInd = actionItemTemplate.activeInd
        actionItemTemplateNew.lastModified = actionItemTemplate.lastModified
        actionItemTemplateNew.dataOrigin = actionItemTemplate.dataOrigin
        actionItemTemplateNew.vpdiCode = actionItemTemplate.vpdiCode

        assertFalse actionItemTemplateNew.validate()
        assertTrue( actionItemTemplateNew.errors.allErrors.codes[0].contains( 'actionItemTemplate.title.nullable.error' ) )
    }


    @Test
    void testNullSourceIndError() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        def actionItemTemplateNew = new ActionItemTemplate()

        actionItemTemplateNew.title = actionItemTemplate.title
        actionItemTemplateNew.sourceInd = null
        actionItemTemplateNew.lastModifiedBy = actionItemTemplate.lastModifiedBy
        actionItemTemplateNew.systemRequired = actionItemTemplate.systemRequired
        actionItemTemplateNew.activeInd = actionItemTemplate.activeInd
        actionItemTemplateNew.lastModified = actionItemTemplate.lastModified
        actionItemTemplateNew.dataOrigin = actionItemTemplate.dataOrigin
        actionItemTemplateNew.vpdiCode = actionItemTemplate.vpdiCode

        assertFalse actionItemTemplateNew.validate()
        assertTrue( actionItemTemplateNew.errors.allErrors.codes[0].contains( 'actionItemTemplate.sourceInd.nullable.error' ) )
    }


    @Test
    void testNullSystemReqError() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        def actionItemTemplateNew = new ActionItemTemplate()

        actionItemTemplateNew.title = actionItemTemplate.title
        actionItemTemplateNew.sourceInd = actionItemTemplate.sourceInd
        actionItemTemplateNew.lastModifiedBy = actionItemTemplate.lastModifiedBy
        actionItemTemplateNew.systemRequired = null
        actionItemTemplateNew.activeInd = actionItemTemplate.activeInd
        actionItemTemplateNew.lastModified = actionItemTemplate.lastModified
        actionItemTemplateNew.dataOrigin = actionItemTemplate.dataOrigin
        actionItemTemplateNew.vpdiCode = actionItemTemplate.vpdiCode

        assertFalse actionItemTemplateNew.validate()
        assertTrue( actionItemTemplateNew.errors.allErrors.codes[0].contains( 'actionItemTemplate.systemRequired.nullable.error' ) )
    }


    @Test
    void testNullActiveIndError() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        def actionItemTemplateNew = new ActionItemTemplate()

        actionItemTemplateNew.title = actionItemTemplate.title
        actionItemTemplateNew.sourceInd = actionItemTemplate.sourceInd
        actionItemTemplateNew.lastModifiedBy = actionItemTemplate.lastModifiedBy
        actionItemTemplateNew.systemRequired = actionItemTemplate.systemRequired
        actionItemTemplateNew.activeInd = null
        actionItemTemplateNew.lastModified = actionItemTemplate.lastModified
        actionItemTemplateNew.dataOrigin = actionItemTemplate.dataOrigin
        actionItemTemplateNew.vpdiCode = actionItemTemplate.vpdiCode

        assertFalse actionItemTemplateNew.validate()
        assertTrue( actionItemTemplateNew.errors.allErrors.codes[0].contains( 'actionItemTemplate.activeInd.nullable.error' ) )
    }
}
