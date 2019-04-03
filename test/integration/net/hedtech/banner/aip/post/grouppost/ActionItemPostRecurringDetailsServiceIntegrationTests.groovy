/*******************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostRecurringDetailsServiceIntegrationTests extends BaseIntegrationTestCase {

    def actionItemPostRecurringDetailsService

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
    void testSave() {
        def actionItemPostRerurringDetails = newRecurringDetails()
        actionItemPostRerurringDetails = actionItemPostRecurringDetailsService.create(actionItemPostRerurringDetails)
        assert actionItemPostRerurringDetails.id
    }

    @Test
    void testFetch(){
        def actionItemPostRerurringDetails = newRecurringDetails()
        actionItemPostRerurringDetails = actionItemPostRecurringDetailsService.create(actionItemPostRerurringDetails)
        assert actionItemPostRerurringDetails.id
        def actionItemPostRerurringDetailsFetch = actionItemPostRecurringDetailsService.get(actionItemPostRerurringDetails.id)
        assert actionItemPostRerurringDetailsFetch.id

    }

    private def newRecurringDetails() {
        new ActionItemPostRecurringDetails(
                recurFrequency:2L,
                recurFrequencyType:'days',
                postingDispStartDays:2L,
                postingDispEndtDays:10L,
                postingDisplayEndDate:new Date(),
                recurStartDate:new Date(),
                recurEndDate:new Date(),
                recurStartTime:new Date(),
                recurPostTimezone:'timeZone',
                lastModified: new Date(),
                lastModifiedBy: 'testUser',
                dataOrigin: 'BANNER'
        )
    }

}
