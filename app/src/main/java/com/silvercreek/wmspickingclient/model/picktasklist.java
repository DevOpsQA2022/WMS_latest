package com.silvercreek.wmspickingclient.model;

public class picktasklist {
    private String TaskNo;
    private String Status;
    private String Route;
    private String Stop;
    private String Date;

    public String getSonos() {
        return Sonos;
    }

    public void setSonos(String sonos) {
        Sonos = sonos;
    }

    private String Sonos;

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

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
    public String getRoute() {
        return Route;
    }
    public void setRoute(String Route) {
        this.Route = Route;
    }
    public String getStop() {
        return Stop;
    }
    public void setStop(String Stop) {
        this.Stop = Stop;
    }
}
