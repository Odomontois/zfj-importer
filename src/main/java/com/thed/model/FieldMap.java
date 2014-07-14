package com.thed.model;

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
    private Integer startingRowNumber;
    
    private Discriminator discriminator;
    
    private Set<FieldMapDetail> fieldMapDetails = new HashSet<FieldMapDetail>();

		public FieldMap(Integer startingRowNumber, Discriminator discriminator,
			Set<FieldMapDetail> fieldMapDetails) {
		super();
		this.startingRowNumber = startingRowNumber;
		this.discriminator = discriminator;
		this.fieldMapDetails = fieldMapDetails;
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

    
}