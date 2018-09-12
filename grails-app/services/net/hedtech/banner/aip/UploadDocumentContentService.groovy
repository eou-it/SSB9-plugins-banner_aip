/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 ********************************************************************************* */

package net.hedtech.banner.aip

import net.hedtech.banner.exceptions.ApplicationException
import net.hedtech.banner.exceptions.BusinessLogicValidationException
import net.hedtech.banner.service.ServiceBase

/**
 * Manages document upload instances.
 */
class UploadDocumentContentService extends ServiceBase {

    /**
     * Validation before uploading document
     * @param dataMap
     */
    def preCreateValidation( domainModelOrMap ) {
        UploadDocumentContentService udc = (domainModelOrMap instanceof Map ? domainModelOrMap?.domainModel : domainModelOrMap) as UploadDocumentContentService
        println "upd $udc"
        }
    }


