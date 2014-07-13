package com.thed.model;

import java.util.Set;

public interface HasJobHistory {
	public Set<JobHistory> getHistory();

	public void setHistory(Set<JobHistory> hashSet);
}
