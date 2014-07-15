package com.thed.model;

import org.apache.commons.lang.StringUtils;


public class FieldMapDetail {
	
	private String zephyrField;
	
	private String mappedField;

	public FieldMapDetail(String zephyrField, String mappedField) {
		super();
		this.zephyrField = zephyrField;
		this.mappedField = mappedField;
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof FieldMapDetail) {
		FieldMapDetail that = (FieldMapDetail)o;
//			return this.zephyrField.equals(that.zephyrField) && this.mappedField.equals(that.mappedField);
			return (this.zephyrField==that.zephyrField) && (this.mappedField==that.mappedField);
		} else {
			return false;
		}
		}
		public int hashCode() {
			return zephyrField.hashCode() + mappedField.hashCode();
		}
	
    
	/**
	 * @return the mappedField
	 */
	public String getMappedField() {
		return mappedField;
	}

	/**
	 * @param mappedField the mappedField to set
	 */
	public void setMappedField(String mappedField) {
		if (StringUtils.isNotBlank(mappedField)) {
			this.mappedField = mappedField;
		} else {
			this.mappedField = null;
		}
	}

	/**
	 * @return the zephyrField
	 */
	public String getZephyrField() {
		return zephyrField;
	}

	/**
	 * @param zephyrField the zephyrField to set
	 */
	public void setZephyrField(String zephyrField) {
		this.zephyrField = zephyrField;
	}

	
}