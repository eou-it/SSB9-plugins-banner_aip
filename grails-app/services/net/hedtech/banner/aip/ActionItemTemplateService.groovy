/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Template
 */
class ActionItemTemplateService extends ServiceBase {
    def listActionItemTemplates() {
        return ActionItemTemplate.fetchActionItemTemplates()
    }
}
