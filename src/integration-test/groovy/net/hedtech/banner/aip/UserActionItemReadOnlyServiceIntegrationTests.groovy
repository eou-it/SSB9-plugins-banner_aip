/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import grails.testing.mixin.integration.Integration
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.test.annotation.Rollback

@Integration
@Rollback
@Transactional
class UserActionItemReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def userActionItemReadOnlyService


    @Before
    void setUp() {
        formContext = ['SELFSERVICE']
        super.setUp()
    }


	@After
    void tearDown() {
        super.tearDown()
    }


    @Test
    void testFetchUserActionItemByROPidmNoResults() {
        def actionItemPidm = PersonUtility.getPerson( "STUADV425" ).pidm
        List<UserActionItemReadOnly> userActionItems = userActionItemReadOnlyService.listActionItemByPidmWithinDate( actionItemPidm )
        assertEquals( 0, userActionItems.size() )
    }


    @Test
    void checkIfActionItemPresent() {
        def actionItemPidm = PersonUtility.getPerson( "CSRSTU005" ).pidm
        assert userActionItemReadOnlyService.checkIfActionItemPresent( actionItemPidm ) == true
    }


}
