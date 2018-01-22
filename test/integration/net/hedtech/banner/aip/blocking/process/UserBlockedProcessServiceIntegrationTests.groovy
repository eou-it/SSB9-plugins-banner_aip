/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.blocking.process

import net.hedtech.banner.aip.ActionItem
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserBlockedProcessServiceIntegrationTests extends BaseIntegrationTestCase {
    def userBlockedProcessReadOnlyService


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
    void getBlockedProcessesByPidmAndActionItemId() {
        def actionItemId = ActionItem.findByName( 'Please Review the Attendance Policy' ).id
        def pidm = PersonUtility.getPerson( "CSRSTU002" ).pidm
        sessionFactory.currentSession.createSQLQuery( """UPDATE gcvasts set GCVASTS_BLOCK_PROCESS_IND = 'Y' where gcvasts_status_rule_NAME='Pending'""" ).executeUpdate()
        UserBlockedProcessReadOnly processReadOnly = userBlockedProcessReadOnlyService.getBlockedProcessesByPidmAndActionItemId( pidm, actionItemId )
        assertEquals( processReadOnly.actionItemId, actionItemId )
        assertEquals( processReadOnly.pidm, pidm )
    }
}
