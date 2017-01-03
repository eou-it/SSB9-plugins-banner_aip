package net.hedtech.banner.aip

import net.hedtech.banner.testing.BaseIntegrationTestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by criddle on 1/3/2017.
 */
class BlockedProcessReadOnlyServiceIntegrationTests extends BaseIntegrationTestCase {

    def blockedProcessReadOnlyService

    @Before
    public void setUp() {
        formContext =['GUAGMNU']
        super.setUp()
    }


    @After
    public void tearDown() {
        super.tearDown()
    }

    @Test
    void testGetAllUrlsAndActionItemIds() {
        def urls = blockedProcessReadOnlyService.getBlockedProcessUrlsAndActionItemIds()
        /*
        println urls
        urls.each { entry ->
            println entry.url
            println entry.actionItemId
        }
        */
        def test1 = urls.findAll { p -> p.url.startsWith( "/ssb/term/termSelection?mode=registration" ) }
        assertFalse( test1.isEmpty() )
        assertTrue( test1.size() > 1 )

        def test2 = urls.findAll { p -> p.url.startsWith( "/ssb/term/termSelection?mode=plan" ) }
        assertFalse( test2.isEmpty(  ) )
        assertTrue( test2.size() > 1 )

        def test3 = urls.findAll { p -> p.url.startsWith( "/ssb/planningStuff" ) }
        assertFalse( test3.isEmpty(  ) )
        assertTrue( test3.size() > 1 )

        def test4 = urls.findAll { p -> p.url.startsWith( "/ssb/term/termSelection?mode=registrationBlah" ) }
        assertTrue( test4.isEmpty(  ) )
    }

}