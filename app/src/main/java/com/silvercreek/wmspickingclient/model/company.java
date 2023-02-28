package com.silvercreek.wmspickingclient.model;

public class company {
    private String companyID;
    private String companyName;
    private String companyDatabase;
    private String logoURL;

    public String getCompanyID() {
        return companyID;
    }
    public void setCompanyID(String companyID) {
        this.companyID = companyID;
    }
    public String getCompanyName() {
        return companyName;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getCompanyDatabase() {
        return companyDatabase;
    }
    public void setCompanyDatabase(String companyDatabase) {
        this.companyDatabase = companyDatabase;
    }
    public String getlogoURL() {
        return logoURL;
    }
    public void setlogoURL(String logoURL) {
        this.logoURL = logoURL;
    }
}
