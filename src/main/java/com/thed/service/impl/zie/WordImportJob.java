package com.thed.service.impl.zie;

import java.util.LinkedHashSet;
import java.util.Set;

import com.thed.model.HasJobHistory;
import com.thed.model.JobHistory;

public class WordImportJob implements HasJobHistory {

	private Set<JobHistory> history = new LinkedHashSet<JobHistory>();
	private String folder;
	private String status;
	private String components;
	private String labels;
	
	@Override
	public Set<JobHistory> getHistory() {
		return history;
	}

	@Override
	public void setHistory(Set<JobHistory> hashSet) {
		history = hashSet;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getComponents() {
		return components;
	}

	public void setComponents(String components) {
		this.components = components;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	
}
