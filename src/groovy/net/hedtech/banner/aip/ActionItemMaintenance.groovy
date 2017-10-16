package net.hedtech.banner.aip
/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
 
 
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type
import javax.persistence.* 
 
 
/** 
 * Action Item Maintenance: Defines main attributes of action items.
 */ 
@Entity 
@Table(name = "GCBACTM") 
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true) 
//TODO : Please verify class name. 
class ActionItemMaintenance implements Serializable { 
 
 	 /** 
 	  * SURROGATE ID: Generated unique numeric identifier for this entity. 
 	  */ 
	 @Id 
 	 @Column(name = "GCBACTM_SURROGATE_ID") 
 	 @SequenceGenerator(name = "GCBACTM_SEQ_GEN", allocationSize = 1, sequenceName = "GCBACTM_SURROGATE_ID_SEQUENCE") 
 	 @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBACTM_SEQ_GEN") 
 	 Long id
 
 
	 /** 
 	  * VERSION: Optimistic lock token. 
 	  */ 
	 @Version 
 	 @Column(name = "GCBACTM_VERSION") 
 	 Long version
 
 
	 /** 
 	  * ACTION ITEM FOLDER ID: Foreign key reference to the folder under which this action item is organized. 
 	  */ 
	 @Column(name = "GCBACTM_GCRFLDR_ID") 
 	 Long actionItemFolderId
 
 
	 /** 
 	  * ACTION ITEM NAME: NAME OF THE ACTION ITEM. 
 	  */ 
	 @Column(name = "GCBACTM_NAME") 
 	 String actionItemName
 
 
	 /** 
 	  * ACTION ITEM TITLE: TITLE of the Action Item. 
 	  */ 
	 @Column(name = "GCBACTM_TITLE") 
 	 String actionItemTitle
 
 
	 /** 
 	  * ACTION ITEM STATUS: Status of the record. Valid values are (D)raft, (A)ctive and (I)nactive. 
 	  */ 
	 @Column(name = "GCBACTM_STATUS_CODE") 
 	 String actionItemStatus
 
 
	 /** 
 	  * ACTION ITEM POSTING INDICATOR: Action Item Posting Indicator. Valid values are (Y)es and (N)o. Default is (N)o. 
 	  */ 
	 @Type(type = "yes_no")   
 	 //TODO : Please verify from database   
 	 @Column(name = "GCBACTM_POSTED_IND") 
 	 Boolean actionItemPostingIndicator
 
 
	 /** 
 	  * Action Item DESCRIPTION: Description of the Action Item. 
 	  */ 
	 @Column(name = "GCBACTM_DESCRIPTION") 
 	 String actionItemDescription
 
 
	 /** 
 	  * CREATOR: The Oracle user name that first created this record. 
 	  */ 
	 @Column(name = "GCBACTM_CREATOR_ID") 
 	 String creator
 
 
	 /** 
 	  * CREATE DATE: The date when this Action Item record was created. 
 	  */ 
	 @Temporal(TemporalType.DATE)   
 	 @Column(name = "GCBACTM_CREATE_DATE") 
 	 Date createDate
 
 
	 /** 
 	  * ACTIVITY DATE: Date that record was created or last updated. 
 	  */ 
	 @Temporal(TemporalType.DATE)   
 	 @Column(name = "GCBACTM_ACTIVITY_DATE") 
 	 Date lastModified
 
 
	 /** 
 	  * USER ID: The user ID of the person who inserted or last updated this record. 
 	  */ 
	 @Column(name = "GCBACTM_USER_ID") 
 	 String lastModifiedBy
 
 
	 /** 
 	  * DATA ORIGIN: Source system that created or updated the data. 
 	  */ 
	 @Column(name = "GCBACTM_DATA_ORIGIN") 
 	 String dataOrigin
 
 
	 /** 
 	  * VPDI CODE: Multi-entity processing code. 
 	  */ 
	 @Column(name = "GCBACTM_VPDI_CODE") 
 	 String vpdiCode
 
 
 	 static constraints = { 
	 	id( nullable : false, maxSize:19) 
	 	version( nullable : true, maxSize:19) 
	 	actionItemFolderId( nullable : false, maxSize:19) 
	 	actionItemName( nullable : false, maxSize:60) 
	 	actionItemTitle( nullable : false, maxSize:2048) 
	 	actionItemStatus( nullable : false, maxSize:1) 
	 	actionItemPostingIndicator( nullable : false, maxSize:1) 
	 	actionItemDescription( nullable : true) 
	 	creator( nullable : true, maxSize:30) 
	 	createDate( nullable : true) 
	 	lastModified( nullable : false) 
	 	lastModifiedBy( nullable : false, maxSize:30) 
	 	dataOrigin( nullable : true, maxSize:30) 
	 	vpdiCode( nullable : true, maxSize:6) 
 	 } 
 }