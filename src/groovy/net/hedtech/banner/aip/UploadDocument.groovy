/*********************************************************************************
 Copyright 2018 Ellucian Company L.P. and its affiliates.
 **********************************************************************************/
package net.hedtech.banner.aip

import grails.orm.HibernateCriteriaBuilder
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.criterion.Order
import net.hedtech.banner.general.CommunicationCommonUtility


import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "UploadDocument.existsSameDocumentName",
                query = """ select count(a.id) FROM UploadDocument a
                    WHERE upper(a.documentName) = upper(:documentName)"""),
        @NamedQuery(name = "UploadDocument.fetchDocumentsCount",
                query = """SELECT COUNT(a.id) FROM UploadDocument a
                        WHERE a.userActionItemId = :userActionItemId
                        AND a.responseId = :responseId"""),
        @NamedQuery(name = "UploadDocument.checkFileLocationById",
                query = """ select a.fileLocation FROM UploadDocument a
                    WHERE a.id = :documentId""")

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
    @Column(name = "GCRAFLU_GCRAACT_ID")
    Long userActionItemId

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
     *  User action item pertains to
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
     * Version of the file upload
     */
    @Version
    @Column(name = "GCRAFLU_VERSION")
    Long version

    static constraints = {
        userActionItemId(nullable: false, maxSize: 19)
        responseId(nullable: false, maxSize: 19)
        documentName(nullable: false, maxSize: 60)
        documentUploadedDate(nullable: false)
        fileLocation(nullable: false, maxSize: 3)
        userComments(nullable: true, maxSize: 4000)
        deleteAfterDate(nullable: true)
        storageDays(nullable: true, maxSize: 4)
        lastModifiedBy(nullable: true, maxSize: 30)
        lastModified(nullable: true)
        version(nullable: true, maxSize: 30)
    }
    /**
     * Method to fetch metadata of documents
     * @param paramsObj Map containing userActionItemId and responseId
     * @return List of metadata of files
     */
    static fetchDocuments(paramsObj) {
        def queryCriteria = UploadDocument.createCriteria()
        queryCriteria.list {
            eq("userActionItemId", Long.parseLong(paramsObj.userActionItemId))
            eq("responseId", Long.parseLong(paramsObj.responseId))
            order((paramsObj.sortAscending ? Order.asc(paramsObj.sortColumn) : Order.desc(paramsObj.sortColumn)).ignoreCase())
        }
    }
    /**
     * Method to fetch count of documents
     * @param paramsObj Map containing userActionItemId and responseId
     * @return Count of documents
     */
    static def fetchDocumentsCount(paramsObj) {
        UploadDocument.withSession { session ->
            session.getNamedQuery('UploadDocument.fetchDocumentsCount')
                    .setLong("userActionItemId", Long.parseLong(paramsObj.userActionItemId))
                    .setLong("responseId", Long.parseLong(paramsObj.responseId))
                    .uniqueResult()
        }
    }

    /**
     * Method to fetch file storage location by Document ID
     * @param documentId Id of the document
     * @return File Storage Location AIP or BDM
     */
    static def fetchFileLocationById(Long documentId) {
        UploadDocument.withSession { session ->
            session.getNamedQuery('UploadDocument.checkFileLocationById')
                    .setLong('documentId', documentId)
                    .uniqueResult()
        }
    }

}
