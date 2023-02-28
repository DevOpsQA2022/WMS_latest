package com.silvercreek.wmspickingclient.model;

public class Movetasklist {
    private String TaskNo;
    private String TaskType;
    private String Status;
    private String RowPrty;

    public String getTaskType() {
        return TaskType;
    }

    public void setTaskType(String taskType) {
        TaskType = taskType;
    }

    public String getRowPrty() {
        return RowPrty;
    }

    public void setRowPrty(String rowPrty) {
        RowPrty = rowPrty;
    }

    private String Userid;
    private String Doctype;
    private String Docno;


    public String getTaskNo() {
        return TaskNo;
    }
    public void setTaskNo(String TaskNo) {
        this.TaskNo = TaskNo;
    }
    public String getStatus() {
        return Status;
    }
    public void setStatus(String Status) {
        this.Status = Status;
    }
    public String getUserid() {
        return Userid;
    }
    public void setUserid(String Userid) {
        this.Userid = Userid;
    }
    public String getDoctype() {
        return Doctype;
    }
    public void setDoctype(String Doctype) {
        this.Doctype = Doctype;
    }
    public String getDocno() {
        return Docno;
    }
    public void setDocno(String Docno) {
        this.Docno = Docno;
    }
}
