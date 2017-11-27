/*********************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
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
        assert result.groups.items[0].find {
            it.name == 'Drug and Alcohol Policy'

        }.name == 'Drug and Alcohol Policy'
        assert result.header == ["title", "state", "completedDate", "description"]
    }


    @Test
    void actionItemOrGroupInfoByGroupSearch() {
        def result = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'group', groupId: "${ActionItemGroup.findByName( 'Enrollment' ).id}"] )
        assert result[0].id != null
        assert result[0].title == 'Enrollment'
        assert result[0].status == 'Active'
        assert result[0].userId != null
        assert result[0].text != null
        assert result[0].activity != null
        assert result[0].version >= 0

    }


    @Test
    void actionItemOrGroupInfoByActionItemSearch() {
        List<ActionItemReadOnly> result = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'actionItem', actionItemId: "${ActionItem.findByName( 'Drug and Alcohol Policy' ).id}"] )
        assert result[1].actionItemId != null
        assert result[1].actionItemName == 'Drug and Alcohol Policy'
        assert result[1].actionItemTitle == 'Drug and Alcohol Policy'
        assert result[1].folderId != null
        assert result[1].folderName != null
    }


    @Test
    void actionItemOrGroupInfoByGroupNoDescInTableSearch() {
        sessionFactory.currentSession.createSQLQuery( """UPDATE gcbagrp set GCBAGRP_INSTRUCTION = null where GCBAGRP_NAME='Enrollment'""" ).executeUpdate()
        def result = userActionItemReadOnlyCompositeService.actionItemOrGroupInfo( [searchType: 'group', groupId: "${ActionItemGroup.findByName( 'Enrollment' ).id}"] )
        assert result[0].id != null
        assert result[0].title == 'Enrollment'
        assert result[0].status == 'Active'
        assert result[0].userId != null
        assert result[0].text == 'There is no description for this group.'
        assert result[0].activity != null
        assert result[0].version >= 0
    }
}
