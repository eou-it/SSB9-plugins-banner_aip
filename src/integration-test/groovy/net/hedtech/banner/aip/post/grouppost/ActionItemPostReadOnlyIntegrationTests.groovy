/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/

package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.aip.ActionItemGroup
import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostReadOnlyIntegrationTests extends BaseIntegrationTestCase {

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
    void testAllFetchJobs() {
        def ourName = 'kdsfkgeddd'

        ActionItemPost myAip = newActionItemPost( ourName )
        myAip.save()

        assert ActionItemPost.findAllByPostingName( ourName ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( ourName ).size() > 0

        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = ActionItemPostReadOnly.fetchJobs( [searchParam: '%'], [max: 1000, offset: 0] )
        assert actionItemReadPostOnlyList.size() > 0
    }


    @Test
    void testFetchJobByName() {
        def ourName = 'asdfkdsfsskgedddfgr'

        ActionItemPost myAip = newActionItemPost( ourName )
        myAip.save()

        assert ActionItemPost.findAllByPostingName( ourName ).size() > 0
        assert ActionItemPostReadOnly.findAllByPostingName( ourName ).size() > 0

        List<ActionItemPostReadOnly> actionItemReadPostOnlyList = ActionItemPostReadOnly.fetchJobs(
                [searchParam: '%' + ourName.toUpperCase(  ) + '%'], [max: 1000, offset: 0] )

        assert actionItemReadPostOnlyList.size() > 0
        assertEquals( ourName, actionItemReadPostOnlyList[0].postingName )
    }


    private ActionItemPost newActionItemPost( String name ) {
            ActionItemPost aip = new ActionItemPost()
            aip.populationVersionId = 32548072L
            aip.populationListId = 43987234L
            aip.postingName = name
            aip.postingActionItemGroupId = ActionItemGroup.fetchActionItemGroups()[0]?.id
            aip.postingDeleteIndicator = 'N'
            aip.postingDisplayStartDate = new Date()
            aip.postingDisplayEndDate = new Date() + 1
            aip.postingCreatorId = 'rgre'
            aip.postingCreationDateTime = new Date()
            aip.populationRegenerateIndicator = 'N'
            aip.lastModified = new Date()
            aip.lastModifiedBy = 'edffeds'

            return aip
        }

}
