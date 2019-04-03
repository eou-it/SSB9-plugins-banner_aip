/*********************************************************************************
 Copyright 2019 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.NamedQueries
import javax.persistence.NamedQuery
import javax.persistence.SequenceGenerator
import javax.persistence.Table
import javax.persistence.Temporal
import javax.persistence.TemporalType
import javax.persistence.Version


@NamedQueries(value = [
        @NamedQuery(name = "UploadDocumentContent.fetchContentByFileUploadId",
                query = """
           FROM UploadDocumentContent a
           WHERE a.fileUploadId = :fileUploadId
          """)
])

@Entity
@Table(name = "GCRAFCT")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Upload Document Content
 */
class UploadDocumentContent implements Serializable {

    /**
     * Surrogate ID for GCRAFCT
     */

    @Id
    @Column(name = "GCRAFCT_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAFCT_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAFCT_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAFCT_SEQ_GEN")
    Long id

    /***
     * Related File upload id.
     */
    @Column(name = "GCRAFCT_GCRAFLU_ID")
    Long fileUploadId

    /**
     * Blob for Document Content
     */
    @Column(name = "GCRAFCT_DOCUMENT_CONTENT")
    byte[] documentContent

    /**
     * User action item pertains to
     */
    @Column(name = "GCRAFCT_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the document upload
     */
    @Column(name = "GCRAFCT_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModified

    /**
     * Version of the upload content
     */
    @Version
    @Column(name = "GCRAFCT_VERSION")
    Long version

    /**
     * Data origin
     */
    @Column(name = "GCRAFCT_DATA_ORIGIN")
    String dataOrigin

    /**
     * Vpdi code for MEP
     */
    @Column(name = "GCRAFCT_VPDI_CODE")
    String vpdiCode

    static constraints = {
        fileUploadId(nullable: false, maxSize: 19)
        documentContent(nullable: false)
        lastModifiedBy(nullable: true, maxSize: 30)
        lastModified(nullable: true)
        version(nullable: true, maxSize: 30)
        dataOrigin(nullable: true, maxSize: 30)
        vpdiCode(nullable: true, maxSize: 6)
    }

    /**
     *
     * @param id
     * @return
     */
    static def fetchContentByFileUploadId(Long id) {
        UserActionItem.withSession { session ->
            session.getNamedQuery('UploadDocumentContent.fetchContentByFileUploadId').setLong('fileUploadId', id).list()[0]
        }
    }
}
