/** *****************************************************************************
 © 2017 SunGard Higher Education.  All Rights Reserved.

 CONFIDENTIAL BUSINESS INFORMATION

 THIS PROGRAM IS PROPRIETARY INFORMATION OF SUNGARD HIGHER EDUCATION
 AND IS NOT TO BE COPIED, REPRODUCED, LENT, OR DISPOSED OF,
 NOR USED FOR ANY PURPOSE OTHER THAN THAT WHICH IT IS SPECIFICALLY PROVIDED
 WITHOUT THE WRITTEN PERMISSION OF THE SAID COMPANY
 ****************************************************************************** */
package net.hedtech.banner.aip

import groovy.json.JsonSlurper
import net.hedtech.banner.service.ServiceBase

/**
 * BlockedProcessReadOnlyService.
 *
 * Date: 1/3/2017
 * Time: 11:45 AM
 */
class BlockedProcessReadOnlyService extends ServiceBase {
    def getBlockedProcessUrlsAndActionItemIds() {
        def jsonSlurper = new JsonSlurper()
        def toBlock = []
        List<BlockedProcessReadOnly> processes = BlockedProcessReadOnly.findAll() // FIXME: dynamic finder
        processes.each { item ->
            def value = jsonSlurper.parseText( item.value.replaceAll( "[\n\r]", "" ) )
            value.aipBlock.urls.each { myUrl ->
                def block = [
                        url         : myUrl,
                        actionItemId: item.blockActionItemId
                ]
                toBlock.push( block )
            }
        }
        return toBlock
    }
}
