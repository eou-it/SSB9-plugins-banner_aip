/** *****************************************************************************
 © 2017 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */
package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * BlockedProcessReadOnlyIntegrationTests.
 *
 * Date: 1/3/2017
 * Time: 8:36 AM
 */
class BlockedProcessReadOnlyIntegrationTests extends BaseIntegrationTestCase {
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
    void testBlockedProcessROToString() {
        List<BlockedProcessReadOnly> blockedProcessesRO = BlockedProcessReadOnly.findAll()

        assertNotNull( blockedProcessesRO[0].toString() )
        assertFalse blockedProcessesRO.isEmpty()
    }


    @Test
    void testBlockedProcessROHashCode() {
        List<BlockedProcessReadOnly> blockedProcessRO = BlockedProcessReadOnly.findAll()

        def firstItem = blockedProcessRO[0]
        def result = firstItem.hashCode()
        assertNotNull result

        def blockedProcessRONew = new BlockedProcessReadOnly()
        blockedProcessRONew.id = firstItem.id
        blockedProcessRONew.blockConfigName = firstItem.blockConfigName
        blockedProcessRONew.blockConfigType = firstItem.blockConfigType
        blockedProcessRONew.blockLastModified = firstItem.blockLastModified
        blockedProcessRONew.blockLastModifiedBy = firstItem.blockLastModifiedBy
        blockedProcessRONew.configLastModified = firstItem.configLastModified
        blockedProcessRONew.configLastModifiedBy = firstItem.configLastModifiedBy
        blockedProcessRONew.value = "{'aipBlock': {'processNamei18n':'aip.blocked.process.name.plan.ahead', 'urls': ['/ssb/planningStuff','/ssb/term/termSelection?mode=plan'] }}"
        def result2 = blockedProcessRONew.hashCode()
        assertNotNull result2

        assertNotEquals( result, result2 )
    }


    @Test
    void testBlockedProcessROEquals() {
        List<BlockedProcessReadOnly> blockedProcessesRO = BlockedProcessReadOnly.findAll()

        def blockedProcessRO = blockedProcessesRO[0]
        def blockedProcessRONew = new BlockedProcessReadOnly()

        blockedProcessRONew.id = blockedProcessRO.id
        blockedProcessRONew.blockConfigName = blockedProcessRO.blockConfigName
        blockedProcessRONew.blockConfigType = blockedProcessRO.blockConfigType
        blockedProcessRONew.blockLastModified = blockedProcessRO.blockLastModified
        blockedProcessRONew.blockLastModifiedBy = blockedProcessRO.blockLastModifiedBy
        blockedProcessRONew.configLastModified = blockedProcessRO.configLastModified
        blockedProcessRONew.configLastModifiedBy = blockedProcessRO.configLastModifiedBy
        blockedProcessRONew.value = blockedProcessRO.value

        def result = blockedProcessRONew.equals( blockedProcessRO )
        assertTrue result

        result = blockedProcessRO.equals( null )
        assertFalse result

        def blockedProcessNull = new BlockedProcessReadOnly( null )
        result = blockedProcessRO.equals( blockedProcessNull )
        assertFalse result
    }
}
