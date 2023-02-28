package com.silvercreek.wmspickingclient.model;

import java.io.Serializable;

public class BreakerUOMList implements Serializable {
    private String buBrName;
    private String buUnit;

    public String getBuBrName() {
        return buBrName;
    }

    public void setBuBrName(String buBrName) {
        this.buBrName = buBrName;
    }

    public String getBuUnit() {
        return buUnit;
    }

    public void setBuUnit(String buUnit) {
        this.buUnit = buUnit;
    }
}
