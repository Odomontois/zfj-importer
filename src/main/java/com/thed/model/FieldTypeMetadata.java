package com.thed.model;


/**
 * Holds custom field type meta data.
 * 
 * @author rajeev goel
 *
 */
public class FieldTypeMetadata {
	
	public static final Long TYPE_TEXT_ID = 1l;
	public static final Long TYPE_LONGTEXT_ID = 2l;
	public static final Long TYPE_LIST_ID = 3l;
	public static final Long TYPE_CHECKBOX_ID = 4l;
	public static final Long TYPE_DATE_ID = 5l;
	public static final Long TYPE_DECIMAL_ID = 10l;

	
	// invisible to user
//	public static final String TYPE_LONG = "Long";
//	public static final String TYPE_INTEGER = "Integer";
//	public static final String TYPE_STRING = "string";
//	public static final String TYPE_COLLECTION = "collection";
	
	/*-----------------------------------------------------------------------
	 * ATTRIBUTES
	 *---------------------------------------------------------------------*/

	/* primary key */
	private String id ;

	/* descriptive name of data type: text, long text, etc */
	private String displayLabel ;
	
	/* JIRA Type: string, array, version, user, number, date, dateTime, project, component etc */
	private String jiraDataType;

	/* For array type fields, this will indicate datatypes of contained items */
	private String itemsDataType;

	/* length of string field. applies only to text and long text fields */
	private Integer length ;

	
	/* Row shows up in custom fild UI if true */
	private Boolean visible;

	private Integer labelWidth;

    public FieldTypeMetadata(String id, String displayLabel,
                             String jiraDataType, String itemsDataType, Integer length,
                             Boolean visible, Integer labelWidth) {
		super();
		this.id = id;
		this.displayLabel = displayLabel;
		this.jiraDataType = jiraDataType;
		this.itemsDataType = itemsDataType;
		this.length = length;
		this.visible = visible;
		this.labelWidth = labelWidth;
	}
	/*-----------------------------------------------------------------------
	 * OVERRIDE
	 *---------------------------------------------------------------------*/

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
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


	public String getDataType() {
		return displayLabel;
	}


	public void setDataType(String dataType) {
		this.displayLabel = dataType;
	}


	public String getJiraDataType() {
		return jiraDataType;
	}


	public void setJiraDataType(String jiraDataType) {
		this.jiraDataType = jiraDataType;
	}


	public String getItemsDataType() {
		return itemsDataType;
	}


	public void setItemsDataType(String itemsDataType) {
		this.itemsDataType = itemsDataType;
	}


	public Integer getLength() {
		return length;
	}


	public void setLength(Integer length) {
		this.length = length;
	}


	public Integer getLabelWidth() {
		return labelWidth;
	}


	public void setLabelWidth(Integer labelWidth) {
		this.labelWidth = labelWidth;
	}

}