package com.thed.model;

public class Preference  implements java.io.Serializable {

	private String name;
    
    private String value;
    
    private String defaultValue;
	
    private boolean isCustomizable;

    public Preference() {
    }

	
    public Preference(String name) {
        this.name = name;
    }
    public Preference(String name, String value, String defaultValue) {
       this.name = name;
       this.value = value;
       this.defaultValue = defaultValue;
    }
   
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return this.value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }
    public String getDefaultValue() {
        return this.defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public boolean getIsCustomizable() {
        return this.isCustomizable;
    }
    
    public void setIsCustomizable(boolean isCustomizable) {
        this.isCustomizable = isCustomizable;
    }




}


