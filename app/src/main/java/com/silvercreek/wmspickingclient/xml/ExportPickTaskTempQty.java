package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.Globals;

public class ExportPickTaskTempQty {

    public StringBuffer writeXml(picktaskdetail tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper,String fromHoldOrSave) {
        try {
            buffer.append("<PickTaskTempAlloc>");


            buffer.append("<Item>");
            if(tran.getItem()!=null){
                buffer.append(tran.getItem());
            }
            buffer.append("</Item>");

            buffer.append("<Palno>");
            if(tran.getLotNo()!=null){
                buffer.append(tran.getLotNo());
            }
            buffer.append("</Palno>");

            buffer.append("<Wlotno>");
            if(tran.getLotNo()!=null){
                buffer.append(tran.getWLotNo());
            }
            buffer.append("</Wlotno>");

            buffer.append("<TempAlloc>");
            if(tran.getLotNo()!=null){
                buffer.append(String.valueOf(tran.getTempQty()));
            }
            buffer.append("</TempAlloc>");

            buffer.append("<Qsoalloc>");
            if(tran.getLotNo()!=null){
                if (fromHoldOrSave.equals("NOTFROMCANCEL")){
                    buffer.append(String.valueOf(tran.getTempQty()));
                }else {
                    buffer.append("0");
                }
            }
            buffer.append("</Qsoalloc>");

            buffer.append("<Loctid>");
            if(Globals.gLoctid!=null){
                buffer.append(Globals.gLoctid);
            }
            buffer.append("</Loctid>");

            buffer.append("<Slot>");
            if(tran.getSlot()!=null){
                buffer.append(tran.getSlot());
            }
            buffer.append("</Slot>");

            buffer.append("</PickTaskTempAlloc>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
