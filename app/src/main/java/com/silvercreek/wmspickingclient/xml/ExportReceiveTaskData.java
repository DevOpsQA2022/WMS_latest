package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.receivetaskexportdetail;
import com.silvercreek.wmspickingclient.util.Globals;

public class ExportReceiveTaskData {

    public StringBuffer writeRTXml(receivetaskexportdetail tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper) {
        try {
            buffer.append("<ReceiveTask>");

            buffer.append("<Taskno>");
            if(tran.gettaskno()!=null){
                buffer.append(tran.gettaskno());
            }
            buffer.append("</Taskno>");

            buffer.append("<Tranlineno>");
            if(tran.gettranlineno()!=null){
                buffer.append(tran.gettranlineno());
            }
            buffer.append("</Tranlineno>");

            buffer.append("<Doctype>");
            if(tran.getDoctype()!=null){
                buffer.append(tran.getDoctype());
            }
            buffer.append("</Doctype>");

            buffer.append("<Docno>");
            if(tran.getDocno()!=null){
                buffer.append(tran.getDocno());
            }
            buffer.append("</Docno>");

            buffer.append("<Doclineno>");
            if(tran.getDoclineno()!=null){
                buffer.append(tran.getDoclineno());
            }
            buffer.append("</Doclineno>");

            buffer.append("<Item>");
            if(tran.getItem()!=null){
                buffer.append(tran.getItem());
            }
            buffer.append("</Item>");

            buffer.append("<umeasur>");
            if(tran.getumeasur()!=null){
                buffer.append(tran.getumeasur());
            }
            buffer.append("</umeasur>");

            buffer.append("<Loctid>");
            if(tran.getLoctid()!=null){
                buffer.append(tran.getLoctid());
            }
            buffer.append("</Loctid>");

            buffer.append("<Wlotno>");
            if(tran.getWlotno()!=null){
                buffer.append(tran.getWlotno());
            }
            buffer.append("</Wlotno>");

            buffer.append("<Lotrefid>");
            if(tran.getLotrefid()!=null){
                buffer.append(tran.getLotrefid());
            }
            buffer.append("</Lotrefid>");

            buffer.append("<Tqtyrec>");
            if(tran.getTqtyrec()!=null){
                buffer.append(tran.getTqtyrec());
            }
            buffer.append("</Tqtyrec>");

            buffer.append("<Trkqtyrec>");
            if(tran.getTrkqtyrec()!=null){
                buffer.append(tran.getTrkqtyrec());
            }
            buffer.append("</Trkqtyrec>");

            buffer.append("<pltlineno>");
            if(tran.getpltlineno()!=null){
                buffer.append(tran.getpltlineno());
            }
            buffer.append("</pltlineno>");

            buffer.append("<ptqty>");
            if(tran.getptqty()!=null){
                buffer.append(tran.getptqty());
            }
            buffer.append("</ptqty>");

            buffer.append("<ptrkqty>");
            if(tran.getptrkqty()!=null){
                buffer.append(tran.getptrkqty());
            }
            buffer.append("</ptrkqty>");

            buffer.append("<prtplttag>");
            if(tran.getprtplttag()!=null){
                buffer.append(tran.getprtplttag());
            }
            buffer.append("</prtplttag>");

            buffer.append("<Slot>");
            if(tran.getSlot()!=null){
                buffer.append(tran.getSlot());
            }
            buffer.append("</Slot>");

            buffer.append("<GTINValue>");
            if(tran.getSlot()!=null){
                buffer.append(tran.getgTin());
            }
            buffer.append("</GTINValue>");

            buffer.append("</ReceiveTask>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
