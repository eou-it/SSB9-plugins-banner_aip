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
 * UserBlockedProcessReadOnlyServiceIntegrationTests.
 *
 * Date: 12/16/2016
 * Time: 3:02 PM
 */
class UserBlockedProcessReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def userBlockedProcessReadOnlyService

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
    void testFetchBlockingByPidmAndActionItemId() {
        def myData = fetchBlockingActionItemsForMyTestUser( 'CSRSTU012' )
        def actionItemPidm = myData.ACTION_ITEM_PIDM.longValue()
        def actionItemId = myData.ACTION_ITEM_ID.longValue()

        List<UserBlockedProcessReadOnly> blockedItems = userBlockedProcessReadOnlyService.getBlockedProcessesByPidmAndActionItemId(
                actionItemPidm, actionItemId )

        assertFalse blockedItems.isEmpty()
        assertEquals( actionItemId, blockedItems[0].id )
        assertEquals( actionItemPidm, blockedItems[0].pidm )
        assertTrue( blockedItems[0].isBlocking )
    }


    @Test
    void testFetchBlockingByPidmAndActionItemIdNoResults() {
        def myData = fetchBlockingActionItemsForMyTestUser( 'CSRSTU012' )
        def actionItemPidm = myData.ACTION_ITEM_PIDM.longValue()
        def actionItemId = 9487533
        List<UserBlockedProcessReadOnly> blockedItems = userBlockedProcessReadOnlyService.getBlockedProcessesByPidmAndActionItemId(
                actionItemPidm, actionItemId )
        assertEquals( 0, blockedItems.size() )
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
