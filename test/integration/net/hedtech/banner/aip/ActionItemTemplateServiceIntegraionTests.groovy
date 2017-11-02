package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ActionItemTemplateServiceIntegraionTests extends BaseIntegrationTestCase {

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
    void testFetchActionItemTemplatesService() {
        List<ActionItemTemplate> actionItemTemplates = actionItemTemplateService.listActionItemTemplates()
        assertFalse actionItemTemplates.isEmpty()
    }
}
