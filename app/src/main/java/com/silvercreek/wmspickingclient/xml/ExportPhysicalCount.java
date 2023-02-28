package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.loadpickpalletWHIPLT;
import com.silvercreek.wmspickingclient.model.physicalcountDetail;
import com.silvercreek.wmspickingclient.util.Globals;

public class ExportPhysicalCount {

    public StringBuffer writeXml(physicalcountDetail tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper) {
        try {

            buffer.append("<PhysicalCount>");

            buffer.append("<countid>");

            if(tran.getcountid()!=null){
                buffer.append(tran.getcountid());
            }
            buffer.append("</countid>");

            buffer.append("<page>");
            if(tran.getpage()!=null){
                buffer.append(tran.getpage());
            }
            buffer.append("</page>");

            buffer.append("<doclineno>");
            if(tran.getdoclineno()!=null){
                buffer.append(tran.getdoclineno());
            }
            buffer.append("</doclineno>");

            buffer.append("<loctid>");
            if(tran.getloctid() !=null){
                buffer.append(tran.getloctid());
            }
            buffer.append("</loctid>");

            buffer.append("<slot>");

            if(tran.getslot()!=null){
                buffer.append(tran.getslot());
            }
            buffer.append("</slot>");

            buffer.append("<item>");
            if(tran.getitem()!=null){
                buffer.append(tran.getitem());
            }
            buffer.append("</item>");

            buffer.append("<wlotno>");
            if(tran.getwlotno()!=null){
                buffer.append(tran.getwlotno());
            }
            buffer.append("</wlotno>");

            buffer.append("<lotrefid>");
            if(tran.getlotrefid() !=null){
                buffer.append(tran.getlotrefid());
            }
            buffer.append("</lotrefid>");

            buffer.append("<umeasur>");

            if(tran.getumeasur()!=null){
                buffer.append(tran.getumeasur());
            }
            buffer.append("</umeasur>");

            buffer.append("<tcountqty>");
            if(tran.gettcountqty()!=null){
                buffer.append(tran.gettcountqty());
            }
            buffer.append("</tcountqty>");

            buffer.append("<tqtyorig>");
            if(tran.gettqty() !=null){
                buffer.append(tran.gettqty());
            }
            buffer.append("</tqtyorig>");

            buffer.append("<surpriseadd>");
            if(tran.getsurprisadd() !=null){
                buffer.append(tran.getsurprisadd());
            }
            buffer.append("</surpriseadd>");

            buffer.append("<invtype>");
            if(tran.getinvtype() !=null){
                buffer.append(tran.getinvtype());
            }
            buffer.append("</invtype>");

            buffer.append("</PhysicalCount>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
