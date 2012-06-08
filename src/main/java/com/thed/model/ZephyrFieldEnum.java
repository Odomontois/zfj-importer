package com.thed.model;

/**
 * This interface holds all the valid Zephyr fields as constants
 * This is not a true enumeration as there is not an easy way to Marshall/demarshall java enums 
 * into AMF
 * 
 * IDs listed here match to primary key of corresponding fields in field_config table.
 * 
 * With respect to import/export -
 * ======= ID 1 - 80 are for testcase. ======= 
 *  
 * ID 1 - 36 are used by existing Testcase import.
 * ID 51 - 66 are used by non-importable fields of testcase.
 * ID 66 - 80 are reserved for future testcase fields
 * 
 * ======= ID 100 - 199 are used by Requirement ======= 
 * ID 101 - 108 are used by importable Requirement fields
 * ID 120 - 127 are for non-importable Requirement fields
 * ID 127 - 199 are reserved for future requirement fields. 
 * 
 * Start with 200 for any new importable entity
 * 
 * Custom fields start from 1001. A dummy row with id 1000 ensures that.
 * 
 * @author smangal, rajeev goel
 *
 */
public interface ZephyrFieldEnum {
	
	public static final String NAME = "1";
	public static final String STEPS = "2";
	public static final String RESULT = "3";
	public static final String EXTERNAL_ID = "21";
	public static final String TESTDATA = "22";
	public static final String PRIORITY = "23";
	public static final String CATEGORY = "24";
	public static final String CREATED_BY = "25";
	public static final String CREATED_ON = "26";
	public static final String LABELS = "27";
	public static final String COMMENTS = "28";
	
	public static final String AFFECTS_VERSION = "41"; /* Not Used */
	public static final String FIX_VERSION = "42";
	public static final String COMPONENT = "43";
	public static final String ASSIGNEE = "45";
	public static final String DESCRIPTION = "46";
	public static final String STATUS = "47";
	public static final String RESOLUTION = "48";

//	public static final String ATTACHMENT="29" ;		// used in requirement
//	public static final String URL= "30";				// used in requirement
//	public static final String IS_MARKED= "32";			// not used 
	public static final String FLAG_AUTOMATION= "33";
	
	public static final String SCRIPT_ID= "34";
	public static final String SCRIPT_NAME= "35";
	public static final String SCRIPT_PATH= "36";
	
	/* Requirement fields */
	/* old to new id mapping : */
//	select 	case zephyr_field_id 
//	when 1	then 101  /* name */
//	when 31  then 102  /* details */
//	when 21  then 103  /* external id */
//	when 29  then 104  /* attachment */ ***
//	when 23  then 105 /* priority */
//	when 30  then 106 /* url */
//	when 25  then 107 /* created by */
//	when 26  then 108 /* created on */
//	end
//  from field_map_detail where field_map_id = 1 ; (for requirement).


	public static final String REQUIREMENT_NAME = "101";
	public static final String REQUIREMENT_DETAILS ="102" ;			// used in requirement
	public static final String REQUIREMENT_EXTERNAL_ID = "103";
	public static final String REQUIREMENT_ATTACHMENT = "104" ;		// used in requirement
	public static final String REQUIREMENT_PRIORITY = "105";
	public static final String REQUIREMENT_URL= "106";				// used in requirement
	public static final String REQUIREMENT_CREATED_BY = "107";
	public static final String REQUIREMENT_CREATED_ON = "108";
	
	public static final String TCR_CATALOG_TREE_NAME = "283";		
	
}