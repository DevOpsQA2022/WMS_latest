package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.receivetaskloadtype;
import com.silvercreek.wmspickingclient.util.Globals;

public class ExportReceiveTaskLoadData {

    public StringBuffer writeRTLTXml(receivetaskloadtype tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper) {
        try {
            buffer.append("<Loadtypelist>");

            buffer.append("<loadtype>");
            if(tran.getloadtype()!=null){
                buffer.append(tran.getloadtype());
            }
            buffer.append("</loadtype>");

            buffer.append("<descrip>");
            if(tran.getdescrip()!=null){
                buffer.append(tran.getdescrip());
            }
            buffer.append("</descrip>");

            buffer.append("<welement>");
            if(tran.getwelement()!=null){
                buffer.append(tran.getwelement());
            }
            buffer.append("</welement>");

            buffer.append("<collection>");
            if(tran.getcollection()!=null){
                buffer.append(tran.getcollection());
            }
            buffer.append("</collection>");

            buffer.append("<widgetID>");
            if(tran.getwidgetID()!=null){
                buffer.append(tran.getwidgetID());
            }
            buffer.append("</widgetID>");

            buffer.append("<LoadId>");
            if(tran.getLoadId()!=null){
                buffer.append(tran.getLoadId());
            }
            buffer.append("</LoadId>");

            buffer.append("<LoadTypeStatus>");
            if(tran.getLoadTypeStatus()!=null){
                buffer.append(tran.getLoadTypeStatus());
            }
            buffer.append("</LoadTypeStatus>");

            buffer.append("<WmsDate>");
            if(Globals.gRTWMSDate !=null){
                buffer.append(Globals.gRTWMSDate);
            }
            buffer.append("</WmsDate>");

            buffer.append("<Metricval>");
            if(tran.getMetricval()!=null){
                buffer.append(tran.getMetricval());
            }
            buffer.append("</Metricval>");

            buffer.append("</Loadtypelist>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
