/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.gorm.transactions.Transactional
import net.hedtech.banner.service.ServiceBase

/**
 * Service class for Action Item Template
 */
@Transactional
class ActionItemTemplateService extends ServiceBase {
    def listActionItemTemplates() {
        return ActionItemTemplate.fetchActionItemTemplates()
    }
}
