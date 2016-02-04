/*********************************************************************************
 Copyright 2016 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.csr
import org.junit.Before
import org.junit.Test
import org.junit.After

import net.hedtech.banner.testing.BaseIntegrationTestCase
import groovy.sql.Sql
import org.hibernate.annotations.OptimisticLock
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException


class ActionItemListIntegrationTests extends BaseIntegrationTestCase {

    @Test
    void testFetchActionItems() {

        def actionItems

        assertEquals 1, ActionItemList.fetchActionItems().size()

    }

}