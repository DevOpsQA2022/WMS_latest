package com.silvercreek.wmspickingclient.model;

import java.io.Serializable;

public class MoveManuallyTransaction implements Serializable {
    private String mmTranWlotno;
    private String mmTranItem;
    private String mmTranSlot;
    private String mmTranLoctid;
    private String mmTranUOM;
    private double mmTranQty;
    private double mmTranTrkqty;
    private String mmTranEqty;
    private String mmTranItmDesc;
    private String mmTranCatchwt;
    private String mmTranLotrefid;


    public String getMmTranWlotno() {
        return mmTranWlotno;
    }

    public void setMmTranWlotno(String mmTranWlotno) {
        this.mmTranWlotno = mmTranWlotno;
    }

    public String getMmTranItem() {
        return mmTranItem;
    }

    public void setMmTranItem(String mmTranItem) {
        this.mmTranItem = mmTranItem;
    }

    public String getMmTranSlot() {
        return mmTranSlot;
    }

    public void setMmTranSlot(String mmTranSlot) {
        this.mmTranSlot = mmTranSlot;
    }

    public String getMmTranLoctid() {
        return mmTranLoctid;
    }

    public void setMmTranLoctid(String mmTranLoctid) {
        this.mmTranLoctid = mmTranLoctid;
    }

    public String getMmTranUOM() {
        return mmTranUOM;
    }

    public void setMmTranUOM(String mmTranUOM) {
        this.mmTranUOM = mmTranUOM;
    }

    public double getMmTranQty() {
        return mmTranQty;
    }

    public void setMmTranQty(double mmTranQty) {
        this.mmTranQty = mmTranQty;
    }

    public double getMmTranTrkqty() {
        return mmTranTrkqty;
    }

    public void setMmTranTrkqty(double mmTranTrkqty) {
        this.mmTranTrkqty = mmTranTrkqty;
    }

    public String getMmTranEqty() {
        return mmTranEqty;
    }

    public void setMmTranEqty(String mmTranEqty) {
        this.mmTranEqty = mmTranEqty;
    }

    public String getMmTranItmDesc() {
        return mmTranItmDesc;
    }

    public void setMmTranItmDesc(String mmTranItmDesc) {
        this.mmTranItmDesc = mmTranItmDesc;
    }

    public String getMmTranCatchwt() {
        return mmTranCatchwt;
    }

    public void setMmTranCatchwt(String mmTranCatchwt) {
        this.mmTranCatchwt = mmTranCatchwt;
    }

    public String getMmTranLotrefid() {
        return mmTranLotrefid;
    }

    public void setMmTranLotrefid(String mmTranLotrefid) {
        this.mmTranLotrefid = mmTranLotrefid;
    }
}
