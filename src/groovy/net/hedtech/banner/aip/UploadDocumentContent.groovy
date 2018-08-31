
/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "UploadDocumentContent.fetchUploadContentById",
                query = """
           FROM UploadDocumentContent a
           WHERE a.fileUploadId = :myId
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
     * Version of the action item
     */
    @Version
    @Column(name = "GCRAFCT_VERSION")
    Long version

    static constraints = {
        fileUploadId( nullable: false, maxSize: 19 )
        documentContent( nullable: false )
        lastModifiedBy( nullable: true, maxSize: 30 )
        lastModified( nullable: true )
        version( nullable: true, maxSize: 30 )
    }

    /**
     *
     * @param id
     * @return
     */
    static def fetchUploadContentById( Long id ) {

        UploadDocumentContent.withSession { session ->
            UploadDocumentContent uploadContentById = session.getNamedQuery('UploadDocumentContent.fetchUploadContentById').setLong('myId', id).list()[0];
            return uploadContentById
        }
    }
}
