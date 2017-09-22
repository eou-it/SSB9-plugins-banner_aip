package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import javax.persistence.Column
import javax.persistence.Version

class ActionItemTemplateIntegrationTests extends BaseIntegrationTestCase {

    def actionItemTemplateService


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
    void testActionItemTemplateEquals() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        def actionItemTemplateNew = new ActionItemTemplate(
                title: actionItemTemplate.title,
                pageId: actionItemTemplate.pageId,
                sourceInd: actionItemTemplate.sourceInd,
                userId: actionItemTemplate.userId,
                systemRequired: actionItemTemplate.systemRequired,
                activeInd: actionItemTemplate.activeInd,
                activityDate: actionItemTemplate.activityDate,
                dataOrigin: actionItemTemplate.dataOrigin,
                vpdiCode: actionItemTemplate.vpdiCode
        )
        long actionItemTemplateId = actionItemTemplate.id
        long actionItemTemplateVersion = actionItemTemplate.version
        actionItemTemplateNew.id = actionItemTemplateId
        actionItemTemplateNew.version = actionItemTemplateVersion

        def result = actionItemTemplate.equals( actionItemTemplate )
        assertTrue result

        result = actionItemTemplate.equals( actionItemTemplateNew )
        assertTrue result

        result = actionItemTemplate.equals( null )
        assertFalse result

        def actionItemTemplateNull = new ActionItemTemplate( null )
        result = actionItemTemplate.equals( actionItemTemplateNull )
        assertFalse result
    }


    @Test
    void testNullTitleError() {
        List<ActionItemTemplate> actionItemTemplates = ActionItemTemplate.fetchActionItemTemplates()
        def actionItemTemplate = actionItemTemplates[0]
        def actionItemTemplateNew = new ActionItemTemplate()

        actionItemTemplateNew.title = null
        actionItemTemplateNew.sourceInd = actionItemTemplate.sourceInd
        actionItemTemplateNew.userId = actionItemTemplate.userId
        actionItemTemplateNew.systemRequired = actionItemTemplate.systemRequired
        actionItemTemplateNew.activeInd = actionItemTemplate.activeInd
        actionItemTemplateNew.activityDate = actionItemTemplate.activityDate
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
        actionItemTemplateNew.userId = actionItemTemplate.userId
        actionItemTemplateNew.systemRequired = actionItemTemplate.systemRequired
        actionItemTemplateNew.activeInd = actionItemTemplate.activeInd
        actionItemTemplateNew.activityDate = actionItemTemplate.activityDate
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
        actionItemTemplateNew.userId = actionItemTemplate.userId
        actionItemTemplateNew.systemRequired = null
        actionItemTemplateNew.activeInd = actionItemTemplate.activeInd
        actionItemTemplateNew.activityDate = actionItemTemplate.activityDate
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
        actionItemTemplateNew.userId = actionItemTemplate.userId
        actionItemTemplateNew.systemRequired = actionItemTemplate.systemRequired
        actionItemTemplateNew.activeInd = null
        actionItemTemplateNew.activityDate = actionItemTemplate.activityDate
        actionItemTemplateNew.dataOrigin = actionItemTemplate.dataOrigin
        actionItemTemplateNew.vpdiCode = actionItemTemplate.vpdiCode

        assertFalse actionItemTemplateNew.validate()
        assertTrue( actionItemTemplateNew.errors.allErrors.codes[0].contains( 'actionItemTemplate.activeInd.nullable.error' ) )
    }
}
