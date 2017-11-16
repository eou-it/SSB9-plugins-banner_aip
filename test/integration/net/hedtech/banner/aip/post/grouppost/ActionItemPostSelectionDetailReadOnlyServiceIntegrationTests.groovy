/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 *******************************************************************************/
package net.hedtech.banner.aip.post.grouppost

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class ActionItemPostSelectionDetailReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {
    def actionItemPostSelectionDetailReadOnlyService


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
    void fetchSelectionIds() {
        def result = actionItemPostSelectionDetailReadOnlyService.fetchSelectionIds( 1 )
        assert result.size() > 0

    }
}
