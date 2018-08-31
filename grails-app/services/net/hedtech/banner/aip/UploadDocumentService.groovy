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
class UploadDocumentService extends ServiceBase {

    /**
     * Validation before uploading document
     * @param dataMap
     */
    def preCreateValidation( dataMap ) {
        if (!dataMap) {
            throw new ApplicationException( UploadDocumentService, new BusinessLogicValidationException( 'preCreate.validation.insufficient.request', [] ) )
          }
        }
    }


