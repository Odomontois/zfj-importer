package com.thed.model;

// Generated Jun 29, 2007 8:10:35 PM by Hibernate Tools 3.2.0.b9

import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import scala.Option;
import scala.runtime.AbstractFunction1;

public class ImportJob implements HasJobHistory {

	private String folder;

	private FieldMap fieldMap;

	private Set<JobHistory> history;

	private Option<Pattern> sheetFilter;
	private boolean attachFile = false;

	private Map<String, FieldConfig> fieldConfigs;
	
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

	public ImportJob(String folder, FieldMap fieldMap,
			Map<String, FieldConfig> fieldConfigs,
			Set<JobHistory> history, Option<String> sheetFilter) {
		super();
		this.folder = folder;
		this.fieldMap = fieldMap;
		this.fieldConfigs = fieldConfigs;
		this.history = history;
		this.sheetFilter = sheetFilter
				.map(new AbstractFunction1<String, Pattern>() {

					@Override
					public Pattern apply(String arg0) {
						return Pattern.compile(arg0);
					}

				});

	}

	public ImportJob() {
	}

	/**
	 * @return the folder
	 */
	public String getFolder() {
		return folder;
	}

	/**
	 * @return the history
	 */
	public Set<JobHistory> getHistory() {
		return history;
	}

	/**
	 * @return the fieldMap
	 */
	public FieldMap getFieldMap() {
		return fieldMap;
	}

	public Option<Pattern> getSheetFilter() {
		return sheetFilter;
	}

	public boolean isAttachFile() {
		return attachFile;
	}

	public void setAttachFile(boolean attachFile) {
		this.attachFile = attachFile;
	}

	public Map<String, FieldConfig> getFieldConfigs() {
		return fieldConfigs;
	}
	
	
}
