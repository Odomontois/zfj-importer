package com.thed.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.thed.util.Discriminator;

/**
 * Entry of all the available maps
 * Will also have predefined maps for users to start, Users can add more maps
 * Maps the User's testcase fields with Zephyr fields
 * Current version only supports Excel/csv
 * @author smangal
 *
 */
@SuppressWarnings("serial")
public class FieldMap implements java.io.Serializable{
    private long id;
	
    private String name;
    
    private String description;
    
    private Date creationDate;
    
    private String fileEntensions;
    
    private Integer startingRowNumber;
    
    private Discriminator discriminator;
    
    private Set<FieldMapDetail> fieldMapDetails = new HashSet<FieldMapDetail>();
    
    private  String fieldMapEntityType;

		public FieldMap(long id, String name, String description,
			Date creationDate, String fileEntensions,
			Integer startingRowNumber, Discriminator discriminator,
			Set<FieldMapDetail> fieldMapDetails, String fieldMapEntityType) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.creationDate = creationDate;
		this.fileEntensions = fileEntensions;
		this.startingRowNumber = startingRowNumber;
		this.discriminator = discriminator;
		this.fieldMapDetails = fieldMapDetails;
		this.fieldMapEntityType = fieldMapEntityType;
	}

	/**
	 * @return the creationDate
	 */
	public Date getCreationDate() {
		return creationDate;
	}

	/**
	 * @param creationDate the creationDate to set
	 */
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the fileEntensions
	 */
	public String getFileEntensions() {
		return fileEntensions;
	}

	/**
	 * @param fileEntensions the fileEntensions to set
	 */
	public void setFileEntensions(String fileEntensions) {
		this.fileEntensions = fileEntensions;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the importFieldMapDetail
	 */
	public Set<FieldMapDetail> getFieldMapDetails() {
		return fieldMapDetails;
	}

	/**
	 * @param importFieldMapDetail the importFieldMapDetail to set
	 */
	public void setFieldMapDetails(Set<FieldMapDetail> importFieldMapDetail) {
		this.fieldMapDetails = importFieldMapDetail;
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
	
	public Discriminator getDiscriminator() {
		return discriminator;
	}

	public void setDiscriminator(Discriminator discriminator) {
		this.discriminator = discriminator;
	}

	public Integer getStartingRowNumber() {
		return startingRowNumber;
	}

	public void setStartingRowNumber(Integer startingRowNumber) {
		this.startingRowNumber = startingRowNumber;
	}

	/**
	 * @return the fieldMapEntityType
	 */
	public String getFieldMapEntityType() {
		return fieldMapEntityType;
	}

	/**
	 * @param fieldMapEntityType the fieldMapEntityType to set
	 */
	public void setFieldMapEntityType(String fieldMapEntityType) {
		this.fieldMapEntityType = fieldMapEntityType;
	}


    
}