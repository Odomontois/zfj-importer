package com.thed.model;

import java.io.Serializable;
import java.util.Date;

public class JobHistory implements Serializable {
	
	private Long id ;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private Date actionDate;

	private String comments;

	/**
	 * 
	 */
	public JobHistory() {
		super();
	}
	
	/**
	 * @param actionDate
	 * @param comments
	 */
	public JobHistory(Date actionDate, String comments) {
		super();
		this.actionDate = actionDate;
		this.comments = comments;
	}

	/**
	 * @return the actionDate
	 */
	public Date getActionDate() {
		return actionDate;
	}

	/**
	 * @param actionDate the actionDate to set
	 */
	public void setActionDate(Date actionDate) {
		this.actionDate = actionDate;
	}

	/**
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}

	/**
	 * @param comments the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	@Override
	public String toString(){
		return actionDate + " : " + comments + "\n";
	}
	
	/**
	 * <b>Description</b>: 
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((actionDate == null) ? 0 : actionDate.hashCode());
		return result;
	}

	/**
	 * <b>Description</b>: 
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final JobHistory other = (JobHistory) obj;
		if (actionDate == null) {
			if (other.actionDate != null)
				return false;
		} else if (!actionDate.equals(other.actionDate))
			return false;
		return true;
	}

	
	
}