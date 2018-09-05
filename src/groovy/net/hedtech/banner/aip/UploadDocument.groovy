/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "UploadDocument.fetchUploadDocumentById", query = """
           FROM UploadDocument a
           WHERE a.actionItemId = :actionItemId
           AND a.responseId = :responseId
        """),
        @NamedQuery(name = "UploadDocument.fetchDocuments", query = """
            FROM UploadDocument ud
            WHERE ud.actionItemId = :actionItemId
            AND ud.responseId = :responseId
            AND ud.pidm = :pidm
        """)
])

@Entity
@Table(name = "GCRAFLU")
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true)
/**
 * Domain class for Upload Document
 */
class UploadDocument implements Serializable {

    /**
     * Surrogate ID for GCRAFLU
     */
    @Id
    @Column(name = "GCRAFLU_SURROGATE_ID")
    @SequenceGenerator(name = "GCRAFLU_SEQ_GEN", allocationSize = 1, sequenceName = "GCRAFLU_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCRAFLU_SEQ_GEN")
    Long id

    /***
     * Related action item id.
     */
    @Column(name = "GCRAFLU_GCBACTM_ID")
    Long actionItemId

    /**
     * Related response id
     */
    @Column(name = "GCRAFLU_GCRAISR_ID")
    Long responseId

    /**
     * Document Name
     */
    @Column(name = "GCRAFLU_DOCUMENT_NAME")
    String documentName

    /**
     * Document Uploaded Date
     */
    @Column(name = "GCRAFLU_DOCUMENT_UPLOADED_DATE")
    Date documentUploadedDate

    /**
     * Document Uploaded  Location
     */
    @Column(name = "GCRAFLU_FILE_LOCATION")
    String fileLocation

    /**
     * User action item pertains to
     */
    @Column(name = "GCRAFLU_USER_ID")
    String lastModifiedBy

    /**
     * Last activity date for the document upload
     */
    @Column(name = "GCRAFLU_ACTIVITY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    Date lastModified

    /**
     * Comment provided by user when file upload
     */
    @Column(name = "GCRAFLU_USER_COMMENTS")
    String userComments

    /**
     * Physical file and database row will be deleted on or after calculated date based date created and store for days value
     */
    @Column(name = "GCRAFLU_DELETE_AFTER_DATE")
    Date deleteAfterDate

    /**
     * Maintain file on disk for number of days after file is created
     */
    @Column(name = "GCRAFLU_STORAGE_DAYS")
    String storageDays

    /**
     * Version of the action item
     */
    @Version
    @Column(name = "GCRAFLU_VERSION")
    Long version

    /**
     * PIDM of the user action item
     */
    @Column(name = "GCRAFLU_PIDM")
    Long pidm


    static constraints = {
        actionItemId( nullable: false, maxSize: 19 )
        responseId( nullable: false, maxSize: 10 )
        pidm( nullable: false, maxSize: 8 )
        documentName( nullable: false, maxSize: 60 )
        documentUploadedDate( nullable: false )
        fileLocation( nullable: false, maxSize: 3 )
        userComments( nullable: true, maxSize: 4000 )
        deleteAfterDate( nullable: true )
        storageDays( nullable: true, maxSize: 4 )
        lastModifiedBy( nullable: true, maxSize: 30 )
        lastModified( nullable: true )
        version( nullable: true, maxSize: 19 )
    }

    /**
     *
     * @param id
     * @return
     */
    static def fetchUploadDocumentById( Long id ) {

        UploadDocument.withSession {session ->
            UploadDocument uploadDocumentById = session.getNamedQuery( 'UploadDocument.fetchUploadDocumentById' ).setLong( 'actionItemId', actionItemId ).setLong( 'responseId', responseId )?.list()[0];
            return uploadDocumentById
        }
    }

    /**
     * This method is responsible for getting list is attached documents for a response.
     * @param pidm
     * @param actionItemId
     * @param responseId
     * @return List of attached documents
     */
    static def fetchDocuments( pidm, actionItemId, responseId ) {
        UploadDocument.withSession {session ->
            List<UploadDocument> uploadedDocuments = session.getNamedQuery( "UploadDocument.fetchDocuments" )
                    .setLong( "actionItemId", actionItemId )
                    .setLong( "responseId", responseId )
                    .setLong( "pidm", pidm )
                    .list()
            return uploadedDocuments
        }
    }
}
