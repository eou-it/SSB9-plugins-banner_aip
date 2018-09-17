
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
                    WHERE upper(a.documentName) = upper(:documentName)""")

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
    @Column(name ="GCRAFLU_DOCUMENT_UPLOADED_DATE")
    Date documentUploadedDate

    /**
     * Document Uploaded  Location
     */
    @Column(name ="GCRAFLU_FILE_LOCATION")
    String fileLocation

    /**
     *  User action item pertains to
     */
    @Column(name = "GCRAFLU_USER_ID")
    String lastModifiedBy

    /**
     * User PIDM
     */
    @Column(name = "GCRAFLU_PIDM")
    Long pidm

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
        actionItemId( nullable: false, maxSize: 19 )
        responseId( nullable: false, maxSize: 19 )
        documentName(nullable:false,maxSize: 60)
        documentUploadedDate(nullable:false)
        fileLocation(nullable: false, maxSize: 3 )
        pidm(nullable:false)
        userComments(nullable:true, maxSize: 4000)
        deleteAfterDate(nullable:true)
        storageDays(nullable:true,maxSize: 4)
        lastModifiedBy( nullable: true, maxSize: 30 )
        lastModified( nullable: true )
        version( nullable: true, maxSize: 30 )
    }
     /**
     *
     * @param params
     * @return
     */
    static fetchDocuments( paramsObj ) {
        def queryCriteria = UploadDocument.createCriteria()
        queryCriteria.list( max: paramsObj.max, offset: paramsObj.offset ) {
            eq("actionItemId", Long.parseLong(paramsObj.actionItemId))
            eq("responseId", Long.parseLong(paramsObj.responseId))
            eq("pidm", paramsObj.pidm.longValue())
            ilike( "documentName", CommunicationCommonUtility.getScrubbedInput( paramsObj.filterName ) )
            order( (paramsObj.sortAscending ? Order.asc( paramsObj.sortColumn ) : Order.desc( paramsObj.sortColumn )).ignoreCase() )
            if (!paramsObj.sortColumn.equals( "documentName" )) {
                order( Order.asc( 'documentName' ).ignoreCase() )
            }
        }
    }
}
