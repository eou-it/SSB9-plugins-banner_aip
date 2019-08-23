/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

@Integration
@Rollback
class UserActionItemReadOnlyCompositeServiceIntegrationTests extends BaseIntegrationTestCase {

    def userActionItemReadOnlyCompositeService
    def configUserPreferenceService

    @Before
    void setUp() {
        formContext = ['SELFSERVICE']
        super.setUp()
    }

    @After
    void tearDown() {
        super.tearDown()
        logout()
    }

    @Test
    void testReviewStateNameInActionItemsList() {
        loginSSB( 'CSRSTU004', '111111' )
        def map = [locale:'en-US']
        def statusMap = configUserPreferenceService.saveLocale(map)
        assert statusMap.status == 'success'
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        def group = result.groups.find{it.title == 'Enrollment'}
        def item = group.items.find {it.name == 'Policy Handbook'}
        assert item.name == 'Policy Handbook'
        assert item.currentReviewState == 'Review needed'
    }

    @Test
    void listActionItemByPidmWithinDate() {
        loginSSB( 'CSRSTU004', '111111' )
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        def group = result.groups.find{it.title == 'Enrollment'}
        def item = group.items.find {it.name == 'Drug and Alcohol Policy'}
        assert item.name == 'Drug and Alcohol Policy'
        assert result.header == ["title", "state", "completedDate", "description"]
    }

    @Test
    void testActionItemWithHaltProcess() {
        loginSSB( 'CSRSTU004', '111111' )
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        def group = result.groups.find{it.title == 'Enrollment'}
        def item = group.items.find {it.name == 'Notice of Scholastic Standards'}
        assert item.actionItemHalted == true
        assert item.haltProcesses.size() > 0
        assert group.groupHalted == true
    }

    @Test
    void testGroupHaltedAndAINotHalted() {
        loginSSB( 'CSRSTU004', '111111' )
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        def group = result.groups.find{it.title == 'Enrollment'}
        def item = group.items.find {it.name == 'Drug and Alcohol Policy'}
        assert item.actionItemHalted == null
        assert group.groupHalted == true
    }

    @Test
    void listActionItemsByPidmNotExists() {
        logout()
        loginSSB( 'CSRSTU026', '111111' )
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assertNull result
    }

    @Test
    void listActionItemsByPidmGroupWithNoDesc() {
        loginSSB( 'CSRSTU001', '111111' )
        sessionFactory.currentSession.createSQLQuery( """UPDATE gcbagrp set GCBAGRP_INSTRUCTION = null where GCBAGRP_NAME='Enrollment'""" ).executeUpdate()
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.isEmpty() == false

        def group = result.groups.find{it.title == 'Enrollment'}
        assert group.discription == 'There is no description for this group.'
    }

    @Test
    void testGroupAndAINotHalted() {
        logout()
        loginSSB( 'CSRSTU001', '111111' )
        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        def group = result.groups.find{it.title == 'Enrollment'}
        def item = group.items.find {it.name == 'Personal Information'}
        assert item.actionItemHalted == null
        assert group.groupHalted == false
    }

    @Test
    void actionItemOrGroupInfoByGroupSearch() {
        logout()
        loginSSB( 'CSRSTU001', '111111' )
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
        logout()
        loginSSB( 'CSRSTU001', '111111' )
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
        logout()
        loginSSB( 'CSRSTU001', '111111' )
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

    @Test
    void actionItemOrGroupInfoNoGroupExist() {
        logout()
        loginSSB( 'CSRSTU001', '111111' )
        def result1 = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'group', groupId: '50'])
        assertNull result1
    }

    @Test
    void actionItemOrGroupInfoNoActionItemExist() {
        logout()
        loginSSB( 'CSRSTU001', '111111' )
        def result1 = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'actionItem', actionItemId: '50'])
        assertNull result1
    }
    @Test
    void actionItemOrGroupInfoInvalidSearchType() {
        logout()
        loginSSB( 'CSRSTU001', '111111' )
        def result1 = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'invalid'])
        assertNull result1
    }

    @Test
    void testReviewStateNameErrorMessage() {
        logout()
        loginSSB( 'CSRSTU004', '111111' )
        def map = [locale:'gb']
        def statusMap = configUserPreferenceService.saveLocale(map)
        assert 'success' == statusMap.status

        //Removing the default Locale en_US temporarily to check the error message scenario
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")[0]
        reviewStateResult.locale = 'AB'
        reviewStateResult.save(flush: true, failOnError: true)

        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        def group = result.groups.find{it.title == 'Enrollment'}
        def item = group.items.find {it.name == 'Policy Handbook'}
        assert 'Policy Handbook' == item.name
        assert 'Review status text unavailable; contact support' == item.currentReviewState
    }

    @Test
    void testCurrentReviewStateIsNull() {
        logout()
        loginSSB( 'CSRSTU004', '111111' )
        def map = [locale:'gb']
        def statusMap = configUserPreferenceService.saveLocale(map)
        assert 'success' == statusMap.status

        //Removing the default Locale en_US temporarily to check the error message scenario
        AipReviewState reviewStateResult = AipReviewState.fetchReviewStateByCodeAndLocale("10", "en_US")[0]
        reviewStateResult.locale = 'AB'
        reviewStateResult.save(flush: true, failOnError: true)

        def result = userActionItemReadOnlyCompositeService.listActionItemByPidmWithinDate()
        assert result.groups.size() > 0
        assert result.groups.items.size() > 0
        def group = result.groups.find{it.title == 'Enrollment'}
        def item = group.items.find {it.name == 'Deans List'}
        assert 'Deans List' == item.name
        assertNull item.currentReviewState
    }
}
