/** *****************************************************************************
 © 2016 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */
package net.hedtech.banner.aip

import groovy.sql.Sql
import net.hedtech.banner.general.person.PersonUtility
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * UserBlockedProcessReadOnlyIntegrationTests.
 *
 * Date: 12/16/2016
 * Time: 10:06 AM
 */
class UserBlockedProcessReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void testFetchBlockedProcessROTrue() {
        def myData = fetchBlockingActionItemsForMyTestUser( "CSRSTU012" )
        def actionItemId = myData.ACTION_ITEM_ID.longValue()
        def actionItemPidm = myData.ACTION_ITEM_PIDM.longValue()

        List<UserBlockedProcessReadOnly> blockedProcessesRO = UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId(
                actionItemPidm, actionItemId )

        assertNotNull( blockedProcessesRO.toString() )
        assertFalse blockedProcessesRO.isEmpty()
        assertEquals( actionItemId, blockedProcessesRO[0].id )
        assertEquals( actionItemPidm, blockedProcessesRO[0].pidm )
        assertTrue( blockedProcessesRO[0].isBlocking )
    }


    @Test
    void testFetchBlockedProcessROFalse() {
        def myData = fetchBlockingActionItemsForMyTestUser( "CSRSTU012" )
        def actionItemId = 93727643
        def actionItemPidm = myData.ACTION_ITEM_PIDM.longValue()

        List<UserBlockedProcessReadOnly> blockedProcessesRO = UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId(
                actionItemPidm, actionItemId )

        assertTrue blockedProcessesRO.isEmpty(  )
    }


    @Test
    void testBlockedProcessROToString() {
        def myData = fetchBlockingActionItemsForMyTestUser("CSRSTU012")
        def actionItemId = myData.ACTION_ITEM_ID.longValue()
        def actionItemPidm = myData.ACTION_ITEM_PIDM.longValue()

        List<UserBlockedProcessReadOnly> blockedProcessesRO = UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId(
                actionItemPidm, actionItemId )

        assertNotNull( blockedProcessesRO.toString() )
        assertFalse blockedProcessesRO.isEmpty()
        assertEquals( actionItemId, blockedProcessesRO[0].id )
        assertEquals( actionItemPidm, blockedProcessesRO[0].pidm )
        assertTrue( blockedProcessesRO[0].isBlocking )
    }


    @Test
    void testBlockedProcessROHashCode() {
        def myData = fetchBlockingActionItemsForMyTestUser("CSRSTU012")
                def actionItemId = myData.ACTION_ITEM_ID.longValue()
                def actionItemPidm = myData.ACTION_ITEM_PIDM.longValue()
        List<UserBlockedProcessReadOnly> blockedProcessesRO = UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId(
                        actionItemPidm, actionItemId )

        def result = blockedProcessesRO.hashCode()
        assertNotNull result

        def myROObj = new UserBlockedProcessReadOnly()
        def result2 = myROObj.hashCode()
        assertNotNull result2

        assertNotEquals( result, result2 )
    }


    @Test
    void testBlockedProcessROEquals() {
        def myData = fetchBlockingActionItemsForMyTestUser( "CSRSTU012" )
        def actionItemId = myData.ACTION_ITEM_ID.longValue()
        def actionItemPidm = myData.ACTION_ITEM_PIDM.longValue()
        List<UserBlockedProcessReadOnly> blockedProcessesRO = UserBlockedProcessReadOnly.fetchBlockingProcessesROByPidmAndActionItemId(
                actionItemPidm, actionItemId )

        def blockedProcessRO = blockedProcessesRO[0]
        def blockedProcessRONew = new UserBlockedProcessReadOnly()

        blockedProcessRONew.id = blockedProcessRO.id
        blockedProcessRONew.pidm = blockedProcessRO.pidm
        blockedProcessRONew.isBlocking = blockedProcessRO.isBlocking


        def result = blockedProcessRONew.equals( blockedProcessRO )
        assertTrue result

        result = blockedProcessRO.equals( null )
        assertFalse result

        def blockedProcessNull = new UserBlockedProcessReadOnly( null )
        result = blockedProcessRO.equals( blockedProcessNull )
        assertFalse result

    }


    def fetchBlockingActionItemsForMyTestUser( String myUser ) {
        def sql
        Integer myUserPidm = PersonUtility.getPerson( myUser ).pidm
        def idToReturn
        try {
            sql = new Sql( sessionFactory.getCurrentSession().connection() )
            idToReturn = sql.firstRow( "select action_item_id, action_item_pidm from GVQ_GCRABLK where ACTION_ITEM_PIDM = ?", [myUserPidm] )
        } finally {
            sql?.close()
        }
        return idToReturn
    }
}
