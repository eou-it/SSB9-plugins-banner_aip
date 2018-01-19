/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.blocking.process

import net.hedtech.banner.aip.blocking.process.UserBlockedProcessReadOnly
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test


class UserBlockedProcessReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void fetchBlockingProcessesROByPidm() {
        def pidm = PersonUtility.getPerson( "CSRSTU002" ).pidm
        sessionFactory.currentSession.createSQLQuery( """UPDATE gcvasts set GCVASTS_BLOCK_PROCESS_IND = 'Y' where gcvasts_status_rule_NAME='Pending'""" ).executeUpdate()
        UserBlockedProcessReadOnly processReadOnly = UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidm( pidm )
        assertEquals( processReadOnly.pidm, pidm )
    }


    @Test
    void fetchProcessesROByPidm() {
        def pidm = PersonUtility.getPerson( "CSRSTU002" ).pidm
        sessionFactory.currentSession.createSQLQuery( """UPDATE gcvasts set GCVASTS_BLOCK_PROCESS_IND = 'Y' where gcvasts_status_rule_NAME='Pending'""" ).executeUpdate()
        UserBlockedProcessReadOnly processReadOnly = UserBlockedProcessReadOnly.fetchProcessesROByPidm( pidm )
        assertEquals( processReadOnly.pidm, pidm )
    }
}
