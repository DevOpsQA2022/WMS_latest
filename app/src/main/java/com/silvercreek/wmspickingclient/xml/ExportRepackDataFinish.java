package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.RepackFG;

public class ExportRepackDataFinish {

    public StringBuffer writeXml(RepackFG tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper) {
        try {
            buffer.append("<RepackFinishedMaterial>");

            buffer.append("<pano>");
            if(tran.getREPACKFG_PANO()!=null) {
                buffer.append(tran.getREPACKFG_PANO());
            }else {
                buffer.append("");
            }
            buffer.append("</pano>");

           /* buffer.append("<tranlineno>");
            buffer.append(tran.getRIT_TRANLINENO());
            buffer.append("</tranlineno>");*/

            buffer.append("<item>");
            if(tran.getREPACKFG_ITEM()!=null) {
                buffer.append(tran.getREPACKFG_ITEM());
            }else {
                buffer.append("");
            }
            buffer.append("</item>");

            buffer.append("<descrip>");
            if(tran.getREPACKFG_DESCRIP()!=null) {
                buffer.append(tran.getREPACKFG_DESCRIP());
            }else {
                buffer.append("");
            }
            buffer.append("</descrip>");

            buffer.append("<umeasur>");
            if(tran.getREPACKFG_UMEASUR()!=null) {
                buffer.append(tran.getREPACKFG_UMEASUR());
            }else {
                buffer.append("");
            }
            buffer.append("</umeasur>");

            buffer.append("<loctid>");
            if(tran.getREPACKFG_LOCTID()!=null) {
                buffer.append(tran.getREPACKFG_LOCTID());
            }else {
                buffer.append("");
            }
            buffer.append("</loctid>");

            buffer.append("<lotno>");
            if(tran.getREPACKFG_LOTNO()!=null) {
                buffer.append(tran.getREPACKFG_LOTNO());
            }else {
                buffer.append("");
            }
            buffer.append("</lotno>");

            buffer.append("<umfact>");
            if(tran.getREPACKFG_UMFACT()!=null) {
                buffer.append(tran.getREPACKFG_UMFACT());
            }else {
                buffer.append("");
            }
            buffer.append("</umfact>");

            buffer.append("<decnum>");
            if(tran.getREPACKFG_DECNUM()!=null) {
                buffer.append(tran.getREPACKFG_DECNUM());
            }else {
                buffer.append("");
            }
            buffer.append("</decnum>");

            buffer.append("<Qty>");
            if(tran.getREPACKFG_QTYMADE()!=null) {
                buffer.append(tran.getREPACKFG_QTYMADE());
            }else {
                buffer.append("");
            }
            buffer.append("</Qty>");

            buffer.append("<tranlineno>");
            if(tran.getREPACKFG_TRANLINENO()!=null) {
                buffer.append(tran.getREPACKFG_TRANLINENO());
            }else {
                buffer.append("");
            }

            buffer.append("</tranlineno>");

            buffer.append("<countryid>");
            if(tran.getREPACKFG_COUNTRYID()!=null) {
                buffer.append(tran.getREPACKFG_COUNTRYID());
            }else {
                buffer.append("");
            }

            buffer.append("</countryid>");


            buffer.append("<addflag>");
            if(tran.getREPACKFG_ADDFLAG()!=null) {
                buffer.append(tran.getREPACKFG_ADDFLAG());
            }else {
                buffer.append("0");
            }

            buffer.append("</addflag>");

            buffer.append("<updflag>");
            if(tran.getREPACKFG_UPDFLAG()!=null) {
                buffer.append(tran.getREPACKFG_UPDFLAG());
            }else {
                buffer.append("0");
            }
            buffer.append("</updflag>");

            buffer.append("<case_pl>");
            if(tran.getREPACKFG_CASE_PL()!=null) {
                buffer.append(tran.getREPACKFG_CASE_PL());
            }else {
                buffer.append("0");
            }
            buffer.append("</case_pl>");

            buffer.append("<setid>");
            if(tran.getREPACKFG_SETID()!=null) {
                buffer.append(tran.getREPACKFG_SETID());
            }else {
                buffer.append("");
            }
            buffer.append("</setid>");


            buffer.append("</RepackFinishedMaterial>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
