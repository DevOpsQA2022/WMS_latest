package com.silvercreek.wmspickingclient.model;

import java.io.Serializable;

public class BreakerUomUtility implements Serializable {
    private String buWlotno;
    private String buItem;
    private String buSlot;
    private String buLoctid;
    private String buUOM;
    private double buQty;
    private double buTrkqty;
    private String buIslocked;
    private String buItemDesc;
    private String buCatchwt;
    private String buStkumid;
    private String buLotRefId;

    public String getBuWlotno() {
        return buWlotno;
    }

    public void setBuWlotno(String buWlotno) {
        this.buWlotno = buWlotno;
    }

    public String getBuItem() {
        return buItem;
    }

    public void setBuItem(String buItem) {
        this.buItem = buItem;
    }

    public String getBuSlot() {
        return buSlot;
    }

    public void setBuSlot(String buSlot) {
        this.buSlot = buSlot;
    }

    public String getBuLoctid() {
        return buLoctid;
    }

    public void setBuLoctid(String buLoctid) {
        this.buLoctid = buLoctid;
    }

    public String getBuUOM() {
        return buUOM;
    }

    public void setBuUOM(String buUOM) {
        this.buUOM = buUOM;
    }

    public double getBuQty() {
        return buQty;
    }

    public void setBuQty(double buQty) {
        this.buQty = buQty;
    }

    public double getBuTrkqty() {
        return buTrkqty;
    }

    public void setBuTrkqty(double buTrkqty) {
        this.buTrkqty = buTrkqty;
    }

    public String getBuIslocked() {
        return buIslocked;
    }

    public void setBuIslocked(String buIslocked) {
        this.buIslocked = buIslocked;
    }

    public String getBuItemDesc() {
        return buItemDesc;
    }

    public void setBuItemDesc(String buItemDesc) {
        this.buItemDesc = buItemDesc;
    }

    public String getBuCatchwt() {
        return buCatchwt;
    }

    public void setBuCatchwt(String buCatchwt) {
        this.buCatchwt = buCatchwt;
    }

    public String getBuStkumid() {
        return buStkumid;
    }

    public void setBuStkumid(String buStkumid) {
        this.buStkumid = buStkumid;
    }

    public String getBuLotRefId() {
        return buLotRefId;
    }

    public void setBuLotRefId(String buLotRefId) {
        this.buLotRefId = buLotRefId;
    }
}
