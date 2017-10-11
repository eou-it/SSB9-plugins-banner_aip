/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip

import grails.test.*
import org.junit.Test

class ActionItemPostDetailIntegrationTests extends GrailsUnitTestCase {
    def obj


    protected void setUp() {
        super.setUp()
    }


    @Test
    void testNullable() {
        ActionItemPostDetail obj = new ActionItemPostDetail();
        mockDomain( ActionItemPostDetail, [obj] )
        obj.actionItemPostId = null
        obj.actionItemId = null
        obj.lastModified = null
        obj.lastModifiedBy = null

        obj.save()

        assertTrue "nullable" == obj.errors['actionItemPostId']
        assertTrue "nullable" == obj.errors['actionItemId']
        assertTrue "nullable" == obj.errors['lastModified']
        assertTrue "nullable" == obj.errors['lastModifiedBy']
    }


    @Test
    void testMaxSize() {
        ActionItemPostDetail obj = new ActionItemPostDetail();
        mockDomain( ActionItemPostDetail, [obj] )
        obj.actionItemPostId = 0
        obj.actionItemId = 0
        obj.lastModified = new Date()
        obj.lastModifiedBy = "A"
        obj.lastModifiedBy = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.dataOrigin = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.vpdiCode = "AAAAAAA"

        obj.save()

        assertTrue "maxSize" == obj.errors['lastModifiedBy']
        assertTrue "maxSize" == obj.errors['dataOrigin']
        assertTrue "maxSize" == obj.errors['vpdiCode']
    }


    @Test
    void testSave() {
        ActionItemPostDetail obj = new ActionItemPostDetail();
        mockDomain( ActionItemPostDetail, [obj] )
        obj.actionItemPostId = 0
        obj.actionItemId = 0
        obj.lastModified = new Date()
        obj.lastModifiedBy = "A"

        obj.save()

        assertNotNull obj.id
    }


}
