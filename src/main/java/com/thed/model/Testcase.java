package com.thed.model;

import java.util.*;

public class Testcase implements java.io.Serializable {

    private Long id;

    private String name;

    private String externalId;

    private String description;

    private String status;

    private String priority;

    private String tag;

    private Date lastModifiedOn;

    private Date creationDate;

    private String comments;

    private Date completedOn;

    private Boolean isComplex;

    private Integer estimatedTime;

    private String assignee;

    private String creator;

    private String tcStepsVersion;

    private Long lastUpdaterId;

    private Boolean temp;

    private Long oldId;

    private Boolean automated;

    private String scriptId;

    private String scriptName;

    private String scriptPath;

    private Set<Long> requirementIds = new HashSet<Long>();

    private Integer attachmentCount = null;

    private String fixVersions;

    private String issueKey;

    public String components;
    public String environment;


    private Map<String, Object> customProperties = new HashMap<String, Object>();

    private String dueDate;


    public Testcase(String name, String description, String status, String priority, String tag, Date lastModifiedOn, Date creationDate, String comments, Date completedOn, Boolean isComplex,
                    Integer estimatedTime, String assignee, String creator, Long lastUpdaterId, String externalId, Boolean temp, Boolean automated, String scriptId, String scriptName, String scriptPath,
                    String fixVersions, String components, String issueKey, String dueDate) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.tag = tag;
        this.lastModifiedOn = lastModifiedOn;
        this.creationDate = creationDate;
        this.comments = comments;
        this.completedOn = completedOn;
        this.isComplex = isComplex;
        this.estimatedTime = estimatedTime;
        this.assignee = assignee;
        this.creator = creator;
        this.lastUpdaterId = lastUpdaterId;
        this.externalId = externalId;
        this.temp = temp;
        //this.defects = defects;
        this.automated = automated;
        this.scriptId = scriptId;
        this.scriptName = scriptName;
        this.scriptPath = scriptPath;
        this.fixVersions = fixVersions;
        this.components = components;
        this.issueKey = issueKey;
        this.dueDate = dueDate;
    }

    public Testcase() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /*
         * public Release getRelease() { return this.release; }
         * 
         * public void setRelease(Release release) { this.release = release; }
         */

    public Date getLastModifiedOn() {
        return this.lastModifiedOn;
    }

    public void setLastModifiedOn(Date lastModifiedOn) {
        this.lastModifiedOn = lastModifiedOn;
    }

    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if ((other == null))
            return false;
        if (!(other instanceof Testcase))
            return false;
        Testcase castOther = (Testcase) other;
        return (this.getId() == castOther.getId());
    }

    public int hashCode() {
        int result = 17;
        if (this.getId() != null) {
            result = 37 * result + this.getId().intValue();
        }
        return result;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getComments() {
        return this.comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getCompletedOn() {
        return this.completedOn;
    }

    public void setCompletedOn(Date completedOn) {
        this.completedOn = completedOn;
    }

    public Boolean getIsComplex() {
        return this.isComplex;
    }

    public void setIsComplex(Boolean isComplex) {
        this.isComplex = isComplex;
    }

    public Integer getEstimatedTime() {
        return this.estimatedTime;
    }

    public void setEstimatedTime(Integer estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public Long getOldId() {
        return oldId;
    }

    public void setOldId(Long oldId) {
        this.oldId = oldId;
    }

    /**
     * @return the automated
     */
    public Boolean getAutomated() {
        return automated;
    }

    /**
     * If automated value is null returns false.
     *
     * @return
     */
    public Boolean getAutomatedDefault() {
        if (automated == null) {
            return Boolean.FALSE;
        }
        return automated;
    }


    /**
     * @param automated the automated to set
     */
    public void setAutomated(Boolean automated) {
        this.automated = automated;
    }

    /**
     * @return the scriptId
     */
    public String getScriptId() {
        return scriptId;
    }

    /**
     * @param scriptId the scriptId to set
     */
    public void setScriptId(String scriptId) {
        this.scriptId = scriptId;
    }

    /**
     * @return the scriptName
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * @param scriptName the scriptName to set
     */
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
    }

    /**
     * @return the scriptPath
     */
    public String getScriptPath() {
        return scriptPath;
    }

    /**
     * @param scriptPath the scriptPath to set
     */
    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public Boolean temp() {
        return temp;
    }

    /* required by hibernate */
    public Boolean getTemp() {
        return temp;
    }


    public void setTemp(Boolean temp) {
        this.temp = temp;
    }

//    public Set<Defect> getDefects() {
//	return defects;
//    }
//
//    public void setDefects(Set<Defect> defects) {
//	this.defects = defects;
//    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creatorId) {
        this.creator = creatorId;
    }

    /**
     * @return the tcStepsVersion
     */
    public String getTcStepsVersion() {
        return tcStepsVersion;
    }

    /**
     * @param tcStepsVersion the tcStepsVersion to set
     */
    public void setTcStepsVersion(String tcStepsVersion) {
        this.tcStepsVersion = tcStepsVersion;
    }

    public Long getLastUpdaterId() {
        return lastUpdaterId;
    }

    public void setLastUpdaterId(Long lastUpdaterId) {
        this.lastUpdaterId = lastUpdaterId;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    /**
     * <b>Description</b>: getter method for attribute <i>requirementIds</i>
     *
     * @return requirementIds
     */
    public Set<Long> getRequirementIds() {
        return this.requirementIds;
    }

    /**
     * <b>Description</b>: setter method for attribute <i>requirementIds</i>
     *
     * @param requirementIds
     */
    public void setRequirementIds(Set<Long> requirementIds) {
        this.requirementIds = requirementIds;
    }

    /**
     * @return the attachmentCount
     */
    public Integer getAttachmentCount() {
        return attachmentCount;
    }

    /**
     * @param attachmentCount the attachmentCount to set
     */
    public void setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    /**
     * @return the releaseId
     */
    public String getFixVersions() {
        return fixVersions;
    }

    /**
     * @param releaseId the releaseId to set
     */
    public void setFixVersions(String releaseId) {
        this.fixVersions = releaseId;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(Map<String, Object> customProperties) {
        this.customProperties = customProperties;
    }

    public Object getValueOfCustomField(String name) {
        return getCustomProperties().get(name);
    }

    public void setValueOfCustomField(String name, Object value) {
        getCustomProperties().put(name, value);
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDueDate() {
        return dueDate;
    }
}