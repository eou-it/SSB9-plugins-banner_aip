/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class UserActionItemReadOnlyCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def userActionItemReadOnlyCompositeService


    @Before
    void setUp() {
        formContext = ['GUAGMNU']
        super.setUp()
        loginSSB( 'CSRSTU001', '111111' )
    }


    @After
    void tearDown() {
        super.tearDown()
        logout()
    }


    @Test
    void listActionItemByPidmWithinDate() {
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        result.groups.items.each {
            def o = it.find {it.name == 'Drug and Alcohol Policy'}
            if (o) {
                assert o.name == 'Drug and Alcohol Policy'
            }
        }
        assert result.header == ["title", "state", "completedDate", "description"]
    }
    @Test
    void testActionItemWithHaltProcess() {
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        result.groups.items.each {
            def o = it.find {it.name == 'Drug and Alcohol Policy'}
            if (o) {
                assert o.actionItemHalted == true
                assert o.haltProcesses.size() > 0
            }
        }

    }
    @Test
    void testActionItemWithOutHaltProcess() {
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        result.groups.items.each {
            def o = it.find {it.name == 'Personal Information'}
            if (o) {
                assert o.actionItemHalted == null
            }
        }
    }

    @Test
    void actionItemOrGroupInfoByGroupSearch() {
        def result1 = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'group', groupId: "${ActionItemGroup.findByName( 'Enrollment' ).id}"] )
        def result = result1.find {
            it.title == 'Enrollment'
        }
        assert result.id != null
        assert result.title == 'Enrollment'
        assert result.status == 'Active'
        assert result.userId != null
        assert result.text != null
        assert result.activity != null
        assert result.version >= 0

    }


    @Test
    void actionItemOrGroupInfoByActionItemSearch() {
        List result1 = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'actionItem', actionItemId: "${ActionItem.findByName( 'Drug and Alcohol Policy' ).id}"] )
        def result = result1.find {
            it instanceof ActionItemReadOnly && it.actionItemName == 'Drug and Alcohol Policy'
        }
        assert result.actionItemId != null
        assert result.actionItemName == 'Drug and Alcohol Policy'
        assert result.actionItemTitle == 'Drug and Alcohol Policy'
        assert result.folderId != null
        assert result.folderName != null
    }


    @Test
    void actionItemOrGroupInfoByGroupNoDescInTableSearch() {
        sessionFactory.currentSession.createSQLQuery( """UPDATE gcbagrp set GCBAGRP_INSTRUCTION = null where GCBAGRP_NAME='Enrollment'""" ).executeUpdate()
        def result1 = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'group', groupId: "${ActionItemGroup.findByName( 'Enrollment' ).id}"] )
        def result = result1.find {
            it.title == 'Enrollment'
        }
        assert result.id != null
        assert result.title == 'Enrollment'
        assert result.status == 'Active'
        assert result.userId != null
        assert result.text == 'There is no description for this group.'
        assert result.activity != null
        assert result.version >= 0
    }
}
