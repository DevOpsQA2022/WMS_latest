package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.ResetItemDetails;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.Globals;

public class ExportResetPalletDeatails {

    public StringBuffer writeXml(ResetItemDetails tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper,String SOorRepack,String edtPalQty) {
        try {
            buffer.append("<Resetiteminfo>");

            buffer.append("<Item>");
            if(tran.getItem() !=null){
                buffer.append(tran.getItem());
            }
            buffer.append("</Item>");

            buffer.append("<Loctid>");
            if(tran.getLoctid() !=null){
                buffer.append(tran.getLoctid());
            }
            buffer.append("</Loctid>");

            buffer.append("<wLotno>");
            if(tran.getWlotno() !=null){
                buffer.append(tran.getWlotno());
            }
            buffer.append("</wLotno>");

            buffer.append("<lotno>");
            if(tran.getLotno() !=null){
                buffer.append(tran.getLotno());
            }
            buffer.append("</lotno>");

            buffer.append("<Palno>");
            if(tran.getPalno() !=null){
                buffer.append(tran.getPalno());
            }
            buffer.append("</Palno>");

            buffer.append("<slot>");
            if(tran.getSlot() !=null){
                buffer.append(tran.getSlot());
            }
            buffer.append("</slot>");

            buffer.append("<umeasur>");
            if(tran.getUmeasur() !=null){
                buffer.append(tran.getUmeasur());
            }
            buffer.append("</umeasur>");

            buffer.append("<tqty>");
            if(tran.getQty() !=null){
                buffer.append(tran.getQty());
            }
            buffer.append("</tqty>");

            buffer.append("<rpallocqty>");
            if(tran.getRpallocqty() !=null){
                buffer.append(tran.getRpallocqty());
            }
            buffer.append("</rpallocqty>");

            buffer.append("<whqty>");
            if(tran.getWhqty() !=null){
                buffer.append(tran.getWhqty());
            }
            buffer.append("</whqty>");

            buffer.append("<icqty>");
            if(tran.getIcqty() !=null){
                buffer.append(tran.getIcqty());
            }
            buffer.append("</icqty>");

            buffer.append("<itmdesc>");
            if(tran.getItmdesc() !=null){
                buffer.append(tran.getItmdesc());
            }
            buffer.append("</itmdesc>");

            buffer.append("<qty>");
            if(tran.getQty() !=null){
                buffer.append(tran.getQty());
            }
            buffer.append("</qty>");

            buffer.append("<psoaloc>");
            if(tran.getPsoaloc() !=null){
                buffer.append(tran.getPsoaloc());
            }
            buffer.append("</psoaloc>");

            buffer.append("<prpaloc>");
            if(tran.getPrpaloc() !=null){
                buffer.append(tran.getPrpaloc());
            }
            buffer.append("</prpaloc>");

            buffer.append("<ptfrout>");
            if(tran.getPtfrout() !=null){
                buffer.append(tran.getPtfrout());
            }
            buffer.append("</ptfrout>");

            buffer.append("<ptmaloc>");
            if(tran.getPtmaloc() !=null){
                buffer.append(tran.getPtmaloc());
            }
            buffer.append("</ptmaloc>");

            if (SOorRepack.equals("UPDATE_PALLET")){
                buffer.append("<newqty>");
                if(tran.getPtmaloc() != null){
                    buffer.append(edtPalQty);
                }
                buffer.append("</newqty>");

                buffer.append("<oldqty>");
                if(tran.getPtmaloc() != null){
                    buffer.append(tran.getQty());
                }
                buffer.append("</oldqty>");
            }

            /*if (SOorRepack.equals("SOTRAN")){
                buffer.append("<opentran>");
                if(tran.getOpensocount() !=null){
                    buffer.append(tran.getOpensocount());
                }
                buffer.append("</opentran>");
            }
            if (SOorRepack.equals("REPACK")){
                buffer.append("<opentran>");
                if(tran.getOpenrepackcount() !=null){
                    buffer.append(tran.getOpenrepackcount());
                }
                buffer.append("</opentran>");
            }*/

            buffer.append("</Resetiteminfo>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
