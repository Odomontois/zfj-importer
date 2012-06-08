package com.thed.model;

import java.util.List;

/**
 * Holds information about custom field.
 * 
 * @author rajeevg
 *
 */
public class FieldConfig {

	
	/*-----------------------------------------------------------------------
	 * ATTRIBUTES
	 *---------------------------------------------------------------------*/

	/* primary key */
	private String id ;
	
	/* Entity to which this custom field belongs.
	 * Entity name should be same as Java class name. It should be in proper case.
	 * E.g. "Requirement" and not "requirement" 
	 * */
	private String entityName;

	/*system field or custom field */
	private Boolean systemField;
	
	/* data-type: int, long, string, etc.
	 * Explicit foreign key is not added to FieldTypeMetaData.id table */
	private String fieldTypeMetadata ;
	
	/* field name, all lower-case, starts with alphabetic character. Holds value of "name" attribute
	 * <property column="zcf_myCustomField" name="myCustomField" not-null="false" type="java.lang.String"/> */
	private String fieldName ;

	/* column name, all lower-case, starts with alphabetic character. Holds value of "column" attribute
	 * <property column="zcf_myCustomField" name="myCustomField" not-null="false" type="java.lang.String"/> */
	private String columnName ;
	
	/* Descriptive field name */
	private String displayName ;

	/* Long description */
	private String description ;
	
	/* value is mandatory at client */
	private Boolean mandatory ;
	
	/* value is searchable */
	private Boolean searchable ;
	
	/* value is importable */
	private Boolean importable ;
	
	/* value is exportable */
	private Boolean exportable ;
	
	/* key-value mappings if datatype is LOV. 
	 * This value is saved in Preference table. 
	 * Preference.name is entity.fieldname.LOV, e.g.: requirement.zcf_1001.LOV*/
//	@Column(name="lovValue", length = 255)
//	@Transient
//	private String lovValue ;

	/* length of column if of type String */
	private Integer length;

    private List allowedValues;

	public FieldConfig(String id, String entityName, Boolean systemField,
			String fieldTypeMetadata, String fieldName, String columnName,
			String displayName, String description, Boolean mandatory,
			Boolean searchable, Boolean importable, Boolean exportable,
			Integer length, List allowedValues) {
		super();
		this.id = id;
		this.entityName = entityName;
		this.systemField = systemField;
		this.fieldTypeMetadata = fieldTypeMetadata;
		this.fieldName = fieldName;
		this.columnName = columnName;
		this.displayName = displayName;
		this.description = description;
		this.mandatory = mandatory;
		this.searchable = searchable;
		this.importable = importable;
		this.exportable = exportable;
		this.length = length;
        this.allowedValues = allowedValues;
	}
	
	/*-----------------------------------------------------------------------
	 * OVERRIDE
	 *---------------------------------------------------------------------*/

	@Override
	public String toString() {
		return "FieldConfig: id:" + id + " entityName:" + entityName + " systemField:" + systemField 
				+ " fieldTypeMetadata:" + fieldTypeMetadata + " fieldName:" + fieldName 
				+ " columnName:" + columnName + " displayName:" + displayName 
				+ " mandatory:" + mandatory + " searchable:" + searchable
				+ " importable: " + importable + " exportable:" + exportable 
				+ " length:" + length ;
//				+ " lovValue:" + lovValue ;
	}


	/**
	 * Merges (i..e. copies) attributes from input entity to itself.
	 * 
	 * @param updatedFieldConfig
	 */
	public void merge(FieldConfig updatedFieldConfig) {
		this.setId(updatedFieldConfig.getId());
		this.setEntityName(updatedFieldConfig.getEntityName());
		this.setSystemField(updatedFieldConfig.getSystemField());
		this.setFieldTypeMetadata(updatedFieldConfig.getFieldTypeMetadata());
		this.setFieldName(updatedFieldConfig.getFieldName());
		this.setColumnName(updatedFieldConfig.getColumnName());
		this.setDisplayName(updatedFieldConfig.getDisplayName());
		this.setDescription(updatedFieldConfig.getDescription());
		this.setMandatory(updatedFieldConfig.getMandatory());
		this.setSearchable(updatedFieldConfig.getSearchable());
		this.setImportable(updatedFieldConfig.getImportable());
		this.setExportable(updatedFieldConfig.getExportable());
//		this.setLength(updatedFieldConfig.getLength());
	}
	
	/*-----------------------------------------------------------------------
	 * GETTER/SETTER
	 *---------------------------------------------------------------------*/

	

	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public String getEntityName() {
		return entityName;
	}


	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}


	public Boolean getSystemField() {
		return systemField;
	}


	public void setSystemField(Boolean systemField) {
		this.systemField = systemField;
	}


	public String getFieldTypeMetadata() {
		return fieldTypeMetadata;
	}


	public void setFieldTypeMetadata(String fieldTypeMetadata) {
		this.fieldTypeMetadata = fieldTypeMetadata;
	}


	public String getFieldName() {
		return fieldName;
	}


	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}


	public String getColumnName() {
		return columnName;
	}


	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public Boolean getMandatory() {
		return mandatory;
	}


	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}


	public Boolean getSearchable() {
		return searchable;
	}


	public void setSearchable(Boolean searchable) {
		this.searchable = searchable;
	}


	public Boolean getImportable() {
		return importable;
	}


	public void setImportable(Boolean importable) {
		this.importable = importable;
	}


	public Boolean getExportable() {
		return exportable;
	}


	public void setExportable(Boolean exportable) {
		this.exportable = exportable;
	}

	
	public Integer getLength() {
		return length;
	}


	public void setLength(Integer length) {
		this.length = length;
	}

    public List getAllowedValues() {
        return allowedValues;
    }

	
}