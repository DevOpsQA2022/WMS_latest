package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.loadpickpalletDetails;
import com.silvercreek.wmspickingclient.model.loadpickpalletWHIPLT;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.Globals;

public class ExportLoadPickPallet {

    public StringBuffer writeXml(loadpickpalletWHIPLT tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper) {
        try {
            buffer.append("<taskno>");

            if(tran.getTaskno()!=null){
                buffer.append(tran.getTaskno());
            }
            buffer.append("</taskno>");

            buffer.append("<palno>");
            if(tran.getPalno()!=null){
                buffer.append(tran.getPalno());
            }
            buffer.append("</palno>");

            buffer.append("<stgslot>");
            if(tran.getstgslot()!=null){
                buffer.append(tran.getstgslot());
            }
            buffer.append("</stgslot>");

            buffer.append("<trailer>");
            if(Globals.gLPPTrailer !=null){
                buffer.append(Globals.gLPPTrailer);
            }
            buffer.append("</trailer>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
