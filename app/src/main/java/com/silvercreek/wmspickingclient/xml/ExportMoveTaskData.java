package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.movetaskdetail;
import com.silvercreek.wmspickingclient.model.receivetaskexportdetail;

public class ExportMoveTaskData {

    public StringBuffer writeRTXml(movetaskdetail tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper, String FromSaveOrHold) {
        try {
            buffer.append("<MoveTask>");

            buffer.append("<Taskno>");
            if (tran.getTaskno() != null) {
                buffer.append(tran.getTaskno());
            }
            buffer.append("</Taskno>");

            buffer.append("<Tasktype>");
            if (tran.getTasktype() != null) {
                buffer.append(tran.getTasktype());
            }
            buffer.append("</Tasktype>");

            buffer.append("<Tranlineno>");
            if (tran.getTranlineno() != null) {
                buffer.append(tran.getTranlineno());
            }
            buffer.append("</Tranlineno>");

            buffer.append("<ChildID>");
            if (tran.getChildID() != null) {
                buffer.append(tran.getChildID());
            }
            buffer.append("</ChildID>");

            buffer.append("<Item>");
            if (tran.getItem() != null) {
                buffer.append(tran.getItem());
            }
            buffer.append("</Item>");

            buffer.append("<Loctid>");
            if (tran.getLoctid() != null) {
                buffer.append(tran.getLoctid());
            }
            buffer.append("</Loctid>");

            buffer.append("<Wlotno>");
            if (tran.getWlotno() != null) {
                buffer.append(tran.getWlotno());
            }
            buffer.append("</Wlotno>");

            buffer.append("<Palno>");
            if (tran.getPalno() != null) {
                buffer.append(tran.getPalno());
            }
            buffer.append("</Palno>");

            buffer.append("<Umeasur>");
            if (tran.getUmeasur() != null) {
                buffer.append(tran.getUmeasur());
            }
            buffer.append("</Umeasur>");

            buffer.append("<Tqtyrq>");
            if (tran.getTqtyrq() != null) {
                buffer.append(tran.getTqtyrq());
            }
            buffer.append("</Tqtyrq>");

            buffer.append("<Tqtyact>");
            if (tran.getTqtyact() != null) {
                buffer.append(tran.getTqtyact());
            }
            buffer.append("</Tqtyact>");

            buffer.append("<FromSlot>");
            if (tran.getFromSlot() != null) {
                buffer.append(tran.getFromSlot());
            }
            buffer.append("</FromSlot>");

            buffer.append("<ToSlot>");
            if (tran.getToSlot() != null) {
                buffer.append(tran.getToSlot());
            }
            buffer.append("</ToSlot>");

            buffer.append("<Itmdesc>");
            if (tran.getItmdesc() != null) {
                buffer.append(tran.getItmdesc());
            }
            buffer.append("</Itmdesc>");

            buffer.append("<Pckdesc>");
            if (tran.getPckdesc() != null) {
                buffer.append(tran.getPckdesc());
            }
            buffer.append("</Pckdesc>");

            buffer.append("<Whqty>");
            if (tran.getWhqty() != null) {
                buffer.append(tran.getWhqty());
            }
            buffer.append("</Whqty>");

            buffer.append("<Allocqty>");
            if (tran.getAllocqty() != null) {
                buffer.append(tran.getAllocqty());
            }
            buffer.append("</Allocqty>");

            buffer.append("<Status>");
            if (tran.getStatus() != null) {
                if (FromSaveOrHold.equals("BTNSAVE")) {
                    buffer.append("SAVE");
                } else {
                    buffer.append("HOLD");
                }
            }
            buffer.append("</Status>");

            buffer.append("<Edited>");
            if (tran.getEdited() != null) {
                buffer.append(tran.getEdited());
            }
            buffer.append("</Edited>");

            buffer.append("</MoveTask>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
