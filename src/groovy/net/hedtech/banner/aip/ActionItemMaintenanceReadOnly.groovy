package net.hedtech.banner.aip
/*******************************************************************************
 Copyright 2017 Ellucian Company L.P. and its affiliates.
 ****************************************************************************** */
 
 
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.hibernate.annotations.Type
import javax.persistence.* 
 
 
/** 
 * ACTION ITEM LIST VIEW for admin 
 */ 
@Entity 
@Table(name = "GVQ_GCBACTM") 
@ToString(includeNames = true, ignoreNulls = true)
@EqualsAndHashCode(includeFields = true) 
//TODO : Please verify class name. 
class ActionItemMaintenanceReadOnly implements Serializable {
 
 	 /** 
 	  * ACTION ITEM VERSION: The optimistic lock token. 
 	  */ 
	 @Version 
 	 @Column(name = "ACTION_ITEM_VERSION") 
 	 Long actionItemVersion
 
 
	 /** 
 	  * ACTION ITEM ID: unique numeric identifier for this entity. 
 	  */ 
	 @Column(name = "ACTION_ITEM_ID") 
 	 Long actionItemId
 
 
	 /** 
 	  * ACTION ITEM NAME: name of the action item. 
 	  */ 
	 @Column(name = "ACTION_ITEM_NAME") 
 	 String actionItemName
 
 
	 /** 
 	  * ACTION ITEM FOLDER ID: folder id selected for the action item. 
 	  */ 
	 @Column(name = "ACTION_ITEM_FOLDER_ID") 
 	 Long actionItemFolderId
 
 
	 /** 
 	  * ACTION ITEM FOLDER ID: folder name for the action item. 
 	  */ 
	 @Column(name = "ACTION_ITEM_FOLDER_NAME") 
 	 String actionItemFolderId1
 
 
	 /** 
 	  *  
 	  */ 
	 @Column(name = "ACTION_ITEM_FOLDER_DESCRIPTION") 
 	 String actionItemFolderDescription
 
 
	 /** 
 	  * ACTION ITEM DESCRIPTION: description of the action item. 
 	  */ 
	 @Column(name = "ACTION_ITEM_DESCRIPTION") 
 	 String actionItemDescription
 
 
	 /** 
 	  * ACTION ITEM STATUS: Status of the record. Valid values are Pending, Completed. 
 	  */ 
	 @Column(name = "ACTION_ITEM_STATUS") 
 	 String actionItemStatus
 
 
	 /** 
 	  * ACTION ITEM ACTIVITY_DATE: Date that record was last updated. 
 	  */ 
	 @Temporal(TemporalType.DATE)   
 	 @Column(name = "ACTION_ITEM_ACTIVITY_DATE") 
 	 Date lastModified
 
 
	 /** 
 	  * ACTION ITEM USER_ID: The user ID of the person who last updated this record. 
 	  */ 
	 @Column(name = "ACTION_ITEM_USER_ID") 
 	 String lastModifiedBy
 
 
	 /** 
 	  * ACTION ITEM CREATOR_ID: The user ID of the person who created this record. 
 	  */ 
	 @Column(name = "ACTION_ITEM_CREATOR_ID") 
 	 String actionItemCreator_id
 
 
	 /** 
 	  * ACTION ITEM CREATE_DATE: Date that record was created. 
 	  */ 
	 @Temporal(TemporalType.DATE)   
 	 @Column(name = "ACTION_ITEM_CREATE_DATE") 
 	 Date actionItemCreate_date
 
 
	 /** 
 	  * ACTION ITEM CONTENT ID: The content id for the action item from the action item content table. 
 	  */ 
	 @Column(name = "ACTION_ITEM_CONTENT_ID") 
 	 Long actionItemContentId
 
 
	 /** 
 	  * ACTION ITEM CONTENT: The content for the action item from the action item content table. 
 	  */ 
	 @Column(name = "ACTION_ITEM_CONTENT") 
 	 String actionItemContent
 
 
	 /** 
 	  * ACTION ITEM CONTENT ACTIVITY DATE: The date that the action item content was last updated. 
 	  */ 
	 @Temporal(TemporalType.DATE)   
 	 @Column(name = "ACTION_ITEM_CONTENT_DATE") 
 	 Date actionItemContentActivityDate
 
 
	 /** 
 	  * ACTION ITEM CONTENT USER ID: The user that last updated the action item content record 
 	  */ 
	 @Column(name = "ACTION_ITEM_CONTENT_USER_ID") 
 	 String lastModifiedBy2
 
 
	 /** 
 	  * ACTION ITEM TEMPLATE ID: The id for the template which links to the pagebuilder page. 
 	  */ 
	 @Column(name = "ACTION_ITEM_TEMPLATE_ID") 
 	 Long actionItemTemplateId
 
 
	 /** 
 	  * ACTION ITEM TEMPLATE NAME: The name for the template which links to the pagebuilder page. 
 	  */ 
	 @Column(name = "ACTION_ITEM_TEMPLATE_NAME") 
 	 String actionItemTemplateName
 
 
	 /** 
 	  * ACTION ITEM PAGE ID: The id or constant name for the page which links to the pagebuilder page. 
 	  */ 
	 @Column(name = "ACTION_ITEM_PAGE_NAME") 
 	 String actionItemPageId
 
 
	 /** 
 	  *  
 	  */ 
	 @Temporal(TemporalType.DATE)   
 	 @Column(name = "ACTION_ITEM_COMPOSITE_DATE") 
 	 Date actionItemCompositeDate
 
 
	 /** 
 	  *  
 	  */ 
	 @Column(name = "ACTION_ITEM_LAST_USER_ID") 
 	 String lastModifiedBy3
 
 
 	 static constraints = { 
	 	actionItemVersion( nullable : true, maxSize:19) 
	 	actionItemId( nullable : false, maxSize:19) 
	 	actionItemName( nullable : false, maxSize:2048) 
	 	actionItemFolderId( nullable : false, maxSize:19) 
	 	actionItemFolderId1( nullable : false, maxSize:1020) 
	 	actionItemFolderDescription( nullable : true, maxSize:4000) 
	 	actionItemDescription( nullable : true) 
	 	actionItemStatus( nullable : false, maxSize:30) 
	 	lastModified( nullable : false) 
	 	lastModifiedBy( nullable : false, maxSize:30) 
	 	actionItemCreator_id( nullable : true, maxSize:30) 
	 	actionItemCreate_date( nullable : true) 
	 	actionItemContentId( nullable : true, maxSize:19) 
	 	actionItemContent( nullable : true) 
	 	actionItemContentActivityDate( nullable : true) 
	 	lastModifiedBy2( nullable : true, maxSize:30) 
	 	actionItemTemplateId( nullable : true, maxSize:19) 
	 	actionItemTemplateName( nullable : true, maxSize:2048) 
	 	actionItemPageId( nullable : true, maxSize:60) 
	 	actionItemCompositeDate( nullable : true) 
	 	lastModifiedBy3( nullable : true, maxSize:30) 
 	 } 
 }