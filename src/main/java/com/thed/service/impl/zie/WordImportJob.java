package com.thed.service.impl.zie;

import java.util.LinkedHashSet;
import java.util.Set;

import com.thed.model.HasJobHistory;
import com.thed.model.JobHistory;

public class WordImportJob implements HasJobHistory {

	private Set<JobHistory> history = new LinkedHashSet<JobHistory>();
	
	private String folder;
	private String components;
	private String labels;
	
	
	public WordImportJob(Set<JobHistory> history, String folder,
			String components, String labels) {
		super();
		this.history = history;
		this.folder = folder;
		this.components = components;
		this.labels = labels;
	}

	@Override
	public Set<JobHistory> getHistory() {
		return history;
	}

	public String getFolder() {
		return folder;
	}

	public String getComponents() {
		return components;
	}

	public String getLabels() {
		return labels;
	}

	
}
