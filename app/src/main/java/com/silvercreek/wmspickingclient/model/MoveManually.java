package com.silvercreek.wmspickingclient.model;

import java.io.Serializable;

public class MoveManually implements Serializable {
    private String mmWlotno;
    private String mmItem;
    private String mmSlot;
    private String mmLoctid;
    private String mmUOM;
    private double mmQty;
    private double mmTrkqty;
    private String mmIslocked;
    private String mmItemDesc;
    private String mmCatchwt;
    private String mmLotrefid;
    private String mmrpAlloc;

    public String getMmrpAlloc() {
        return mmrpAlloc;
    }

    public void setMmrpAlloc(String mmrpAlloc) {
        this.mmrpAlloc = mmrpAlloc;
    }

    public String getMmWlotno() {
        return mmWlotno;
    }

    public void setMmWlotno(String mmWlotno) {
        this.mmWlotno = mmWlotno;
    }

    public String getMmItem() {
        return mmItem;
    }

    public void setMmItem(String mmItem) {
        this.mmItem = mmItem;
    }

    public String getMmSlot() {
        return mmSlot;
    }

    public void setMmSlot(String mmSlot) {
        this.mmSlot = mmSlot;
    }

    public String getMmLoctid() {
        return mmLoctid;
    }

    public void setMmLoctid(String mmLoctid) {
        this.mmLoctid = mmLoctid;
    }

    public String getMmUOM() {
        return mmUOM;
    }

    public void setMmUOM(String mmUOM) {
        this.mmUOM = mmUOM;
    }

    public double getMmQty() {
        return mmQty;
    }

    public void setMmQty(double mmQty) {
        this.mmQty = mmQty;
    }

    public double getMmTrkqty() {
        return mmTrkqty;
    }

    public void setMmTrkqty(double mmTrkqty) {
        this.mmTrkqty = mmTrkqty;
    }

    public String getMmIslocked() {
        return mmIslocked;
    }

    public void setMmIslocked(String mmIslocked) {
        this.mmIslocked = mmIslocked;
    }

    public String getMmItemDesc() {
        return mmItemDesc;
    }

    public void setMmItemDesc(String mmItemDesc) {
        this.mmItemDesc = mmItemDesc;
    }

    public String getMmCatchwt() {
        return mmCatchwt;
    }

    public void setMmCatchwt(String mmCatchwt) {
        this.mmCatchwt = mmCatchwt;
    }

    public String getMmLotrefid() {
        return mmLotrefid;
    }

    public void setMmLotrefid(String mmLotrefid) {
        this.mmLotrefid = mmLotrefid;
    }
}
