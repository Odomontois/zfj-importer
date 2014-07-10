package com.thed.model;
// Generated Jun 29, 2007 8:10:35 PM by Hibernate Tools 3.2.0.b9


import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

@SuppressWarnings("serial")
public class ImportJob  implements java.io.Serializable {

    private Long id;

    private String name;
    
    private String folder;
    
    private String fileExtension;
    
    private Date scheduledDate;
        
    private String status;

    /*@Column(name="discriminator", nullable = true)
    private String discriminator;*/

    private User creator;
    
/*  @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "tcrCatalogtree_id", nullable = true)
    private TCRCatalogTree tcrCatalogTree;*/
    
    private Long treeId;
    
    private FieldMap fieldMap;
    
    private  String importEntityType;
        
    private Set<JobHistory> history  ;

		private Pattern sheetFilter;
  
    /**
	 * @param id
	 * @param folder
	 * @param fileExtension
	 * @param scheduledDate
	 * @param status
	 * @param release
	 * @param fieldMap
	 * @param tcrCatalogTree
	 * @param history
	 */

    public ImportJob(Long id, String name, String folder, String fileExtension,	Date scheduledDate, String status, Long treeId,FieldMap fieldMap, Set<JobHistory> history,String importEntityType, String sheetFilter) {
		super();
		this.id = id;
		this.name = name;
		this.folder = folder;
		this.fileExtension = fileExtension;
		this.scheduledDate = scheduledDate;
		this.status = status;
		this.treeId = treeId;
		this.fieldMap = fieldMap;
		this.history = new LinkedHashSet<JobHistory>(history);
		this.importEntityType=importEntityType;
		this.sheetFilter = Pattern.compile(sheetFilter); 
	}

	public ImportJob() {
    }

	
	
	/**
	 * @return the fileExtension
	 */
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * @param fileExtension the fileExtension to set
	 */
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @param folder the folder to set
	 */
	public void setFolder(String folder) {
		this.folder = folder;
	}

	/**
	 * @return the history
	 */
	public Set<JobHistory> getHistory() {
		return history;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(Set<JobHistory> history) {
		this.history = history;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the scheduledDate
	 */
	public Date getScheduledDate() {
		return scheduledDate;
	}

	/**
	 * @param scheduledDate the scheduledDate to set
	 */
	public void setScheduledDate(Date scheduledDate) {
		this.scheduledDate = scheduledDate;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}


	/**
	 * @return the treeId
	 */
	public Long getTreeId() {
		return treeId;
	}

	/**
	 * @param treeId the treeId to set
	 */
	public void setTreeId(Long treeId) {
		this.treeId = treeId;
	}

	/**
	 * @return the fieldMap
	 */
	public FieldMap getFieldMap() {
		return fieldMap;
	}

	/**
	 * @param fieldMap the fieldMap to set
	 */
	public void setFieldMap(FieldMap fieldMap) {
		this.fieldMap = fieldMap;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/*public String getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(String discriminator) {
		this.discriminator = discriminator;
	}*/

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	/**
	 * @return the importEntityType
	 */
	public String getImportEntityType() {
		return importEntityType;
	}

	/**
	 * @param importEntityType the importEntityType to set
	 */
	public void setImportEntityType(String importEntityType) {
		this.importEntityType = importEntityType;
	}
	
	public Pattern getSheetFilter() {
		return sheetFilter;
	}
}


