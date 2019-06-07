/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
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
class ActionItemGroupAssignReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void fetchGroupLookup() {
        def groupFolderList = ActionItemGroupAssignReadOnly.fetchGroupLookup( )
        assertFalse groupFolderList.isEmpty()
    }
}
