package com.silvercreek.wmspickingclient.model;

public class sessiondetail {
    private String Result;
    private String SessionId;
    private String PalletResetPriv;

    public String getPalletResetPriv() {
        return PalletResetPriv;
    }
    public void setPalletResetPriv(String palletResetPriv) {
        PalletResetPriv = palletResetPriv;
    }
    public String getResult() {
        return Result;
    }
    public void setResult(String Result) {
        this.Result = Result;
    }
    public String getSessionId() {
        return SessionId;
    }
    public void setSessionId(String SessionId) {
        this.SessionId = SessionId;
    }
}
