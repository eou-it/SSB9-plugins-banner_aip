/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip

import grails.test.*
import org.junit.Test

class ActionItemPostIntegrationTests extends GrailsUnitTestCase {
    def obj


    protected void setUp() {
        super.setUp()
    }


    @Test
    void testNullable() {
        ActionItemPost obj = new ActionItemPost();
        mockDomain( ActionItemPost, [obj] )
        obj.populationListId = null
        obj.postingActionItemGroupId = null
        obj.postingName = null
        obj.postingDeleteIndicator = null
        obj.postingScheduleType = null
        obj.postingDisplayStartDate = null
        obj.postingDisplayEndDate = null
        obj.postingScheduleDeleteTime = null
        obj.postingCreatorId = null
        obj.postingScheduleDateTime = null
        obj.populationRegenerateIndicator = null
        obj.lastModified = null
        obj.lastModifiedBy = null

        obj.save()

        assertTrue "nullable" == obj.errors['populationListId']
        assertTrue "nullable" == obj.errors['postingActionItemGroupId']
        assertTrue "nullable" == obj.errors['postingName']
        assertTrue "nullable" == obj.errors['postingDeleteIndicator']
        assertTrue "nullable" == obj.errors['postingScheduleType']
        assertTrue "nullable" == obj.errors['postingDisplayStartDate']
        assertTrue "nullable" == obj.errors['postingDisplayEndDate']
        assertTrue "nullable" == obj.errors['postingScheduleDeleteTime']
        assertTrue "nullable" == obj.errors['postingCreatorId']
        assertTrue "nullable" == obj.errors['postingScheduleDateTime']
        assertTrue "nullable" == obj.errors['populationRegenerateIndicator']
        assertTrue "nullable" == obj.errors['lastModified']
        assertTrue "nullable" == obj.errors['lastModifiedBy']
    }


    @Test
    void testMaxSize() {
        ActionItemPost obj = new ActionItemPost();
        mockDomain( ActionItemPost, [obj] )
        obj.populationListId = 0
        obj.postingActionItemGroupId = 0
        obj.postingName = "A"
        obj.postingName = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.postingDeleteIndicator = "A"
        obj.postingDeleteIndicator = "AA"
        obj.postingScheduleType = "A"
        obj.postingScheduleType = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.postingDisplayStartDate = new Date()
        obj.postingDisplayEndDate = new Date()
        obj.postingScheduleDeleteTime = new Date()
        obj.postingCreatorId = "A"
        obj.postingCreatorId = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.postingScheduleDateTime = new Date()
        obj.populationRegenerateIndicator = true
        obj.postingCurrentState = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.postingErrorCode = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.postingGroupId = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.lastModified = new Date()
        obj.lastModifiedBy = "A"
        obj.lastModifiedBy = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.dataOrigin = "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"
        obj.vpdiCode = "AAAAAAA"

        obj.save()

        assertTrue "maxSize" == obj.errors['postingName']
        assertTrue "maxSize" == obj.errors['postingDeleteIndicator']
        assertTrue "maxSize" == obj.errors['postingScheduleType']
        assertTrue "maxSize" == obj.errors['postingCreatorId']
        assertTrue "maxSize" == obj.errors['postingCurrentState']
        assertTrue "maxSize" == obj.errors['postingErrorCode']
        assertTrue "maxSize" == obj.errors['postingGroupId']
        assertTrue "maxSize" == obj.errors['lastModifiedBy']
        assertTrue "maxSize" == obj.errors['dataOrigin']
        assertTrue "maxSize" == obj.errors['vpdiCode']
    }


    @Test
    void testSave() {
        ActionItemPost obj = new ActionItemPost();
        mockDomain( ActionItemPost, [obj] )
        obj.populationListId = 0
        obj.postingActionItemGroupId = 0
        obj.postingName = "A"
        obj.postingDeleteIndicator = "A"
        obj.postingScheduleType = "A"
        obj.postingDisplayStartDate = new Date()
        obj.postingDisplayEndDate = new Date()
        obj.postingScheduleDeleteTime = new Date()
        obj.postingCreatorId = "A"
        obj.postingScheduleDateTime = new Date()
        obj.populationRegenerateIndicator = true
        obj.lastModified = new Date()
        obj.lastModifiedBy = "A"

        obj.save()

        assertNotNull obj.id
    }


}
