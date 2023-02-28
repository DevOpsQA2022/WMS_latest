package com.silvercreek.wmspickingclient.model;

public class configsettings {
    private String configSettingsID;
    private String appName;
    private String appDesc;
    private String installationDate;
    private String expDate;
    private String noOfDays;
    private String deviceId;
    private String username;
    private String password;
    private String adminPassword;
    private String sessionId;
    private String currentCompany;

    public String getID() { return configSettingsID;}
    public void setID(String configSettingsID) { this.configSettingsID = configSettingsID; }
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public String getAppDesc() {
        return appDesc;
    }
    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }
    public String getInstallationDate() {
        return installationDate;
    }
    public void setInstallationDate(String installationDate) {
        this.installationDate = installationDate;
    }
    public String getExpDate() {
        return expDate;
    }
    public void setExpDate(String expDate) {
        this.expDate = expDate;
    }
    public String getNoOfDays() {
        return noOfDays;
    }
    public void setNoOfDays(String noOfDays) {
        this.noOfDays = noOfDays;
    }
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getAdminPassword() {
        return adminPassword;
    }
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }
    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getCurrentCompany()
    {
        return currentCompany;
    }

    public void setCurrentCompany(String pCurrentCompany)
    {
        currentCompany = pCurrentCompany;
    }
}
