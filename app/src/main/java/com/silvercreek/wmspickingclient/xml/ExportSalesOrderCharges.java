package com.silvercreek.wmspickingclient.xml;

import static com.silvercreek.wmspickingclient.util.Globals.chargelistHash;

import android.app.Activity;

import com.silvercreek.wmspickingclient.controller.SalesOrderCharges;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.SalesOrderCategoryList;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.Globals;

import java.util.Map;

public class ExportSalesOrderCharges {

    public StringBuffer writeXml(StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper,String SoNo) {
        try {
            buffer.append("<SoChargeSaveData>");

            for(Map.Entry m:chargelistHash.entrySet()){
                //System.out.println(m.getKey()+" "+m.getValue());
                buffer.append("<UpdSONotes>");

                buffer.append("<Sono>");
                buffer.append(SoNo);
                buffer.append("</Sono>");

                buffer.append("<Category>");
                buffer.append(m.getKey());
                buffer.append("</Category>");

                buffer.append("<qty>");
                buffer.append(m.getValue());
                buffer.append("</qty>");

                buffer.append("</UpdSONotes>");
            }

            buffer.append("</SoChargeSaveData>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
