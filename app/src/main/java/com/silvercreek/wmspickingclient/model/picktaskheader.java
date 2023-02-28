package com.silvercreek.wmspickingclient.model;

public class picktaskheader {
    private String casecount;
    private String weight;
    private String Route;
    private String Stop;
    private String Trailer;

    public String getCasecount() {
        return casecount;
    }
    public void setCasecount(String casecount) {
        this.casecount = casecount;
    }
    public String getWeight() {
        return weight;
    }
    public void setWeight(String weight) {
        this.weight = weight;
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
    public String getTrailer() {
        return Trailer;
    }
    public void setTrailer(String Trailer) {
        this.Trailer = Trailer;
    }
}
