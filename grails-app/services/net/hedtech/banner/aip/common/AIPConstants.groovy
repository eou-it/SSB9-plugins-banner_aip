/*********************************************************************************
 Copyright 2018-2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip.common
/**
 * Constant filed for AIP
 */
class AIPConstants {
    public static final String YES_IND = 'Y'
    public static final String NO_IND = 'N'
    public static final Map STATUS_MAP = ['Draft': 'D', 'Active': 'A', 'Inactive': 'I']
    public static final String ACTIONITEMADMIN_ROLE = 'SELFSERVICE-ACTIONITEMADMIN'

    public static final int SIZE_IN_KB = 1024
    public static final String FILE_STORAGE_SYSTEM_AIP = 'AIP'
    public static final String FILE_STORAGE_SYSTEM_BDM = 'BDM'
    public static final String MAX_SIZE_ERROR = '@@r1:MaxSizeError@@'
    public static final String FILE_TYPE_ERROR = '@@r1:FileTypeError@@'
    public static final String DEFAULT_RESTRICTED_FILE_TYPE = 'EXE'
    public static final String DEFAULT_DOCTYPE = 'AIP'
    public static final String ERROR_MESSAGE_BDM_NOT_INSTALLED = 'uploadDocument.bdm.not.installed'
    public static final String ERROR_MESSAGE_FILE_EMPTY = 'uploadDocument.document.empty.error'
    public static final String ERROR_MESSAGE_BDM = 'uploadDocument.bdm.error'
    public static final String ERROR_MESSAGE_UNSUPPORTED_FILE_STORAGE = 'uploadDocument.invalid.file.storage.error'
    public static final String MESSAGE_SAVE_SUCCESS = 'uploadDocument.save.success'
    public static final String ERROR_MESSAGE_BDM_DOCUMENT_NOT_FOUND = 'uploadDocument.bdm.document.not.found'
    public static final String ERROR_MESSAGE_FILENAME_TOO_LONG = 'aip.uploadDocument.file.name.length.error'

    public static final String DOCUMENT_ID = 'DOCUMENT ID'
    public static final String DOCUMENT_TYPE = 'DOCUMENT TYPE'
    public static final String DOCUMENT_NAME = 'DOCUMENT NAME'
    public static final String VPDI_CODE = 'VPDI CODE'
    public static final String DOCUMENTNAME ='documentName'
    public static final String ERROR_DOCUMENT_NAME_MAXSIZE_EXCEEDED ='uploadDocument.documentName.maxSize.exceeded.documentName'
}
