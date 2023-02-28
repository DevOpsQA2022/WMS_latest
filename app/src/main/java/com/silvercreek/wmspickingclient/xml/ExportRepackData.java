package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.RepackIngredients;

public class ExportRepackData {

    public StringBuffer writeXml(RepackIngredients tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper) {
        try {
            buffer.append("<RepackIngredient>");

            buffer.append("<pano>");
            buffer.append(tran.getRIT_PANO());
            buffer.append("</pano>");

            buffer.append("<tranlineno>");
            buffer.append(tran.getRIT_TRANLINENO());
            buffer.append("</tranlineno>");

            buffer.append("<item>");
            buffer.append(tran.getRIT_ITEM());
            buffer.append("</item>");

            buffer.append("<descrip>");
            buffer.append(tran.getRIT_DESCRIP());
            buffer.append("</descrip>");

            buffer.append("<umeasur>");
            buffer.append(tran.getRIT_UMEASUR());
            buffer.append("</umeasur>");

            buffer.append("<loctid>");
            buffer.append(tran.getRIT_LOCTID());
            buffer.append("</loctid>");

            buffer.append("<serial>");
            buffer.append(tran.getRIT_SERIAL());
            buffer.append("</serial>");

            buffer.append("<qtyused>");
            buffer.append(tran.getRIT_QTYUSED());
            buffer.append("</qtyused>");

            buffer.append("<tqtypicked>");
            buffer.append(tran.getRIT_TRKQTYPK());
            buffer.append("</tqtypicked>");

            buffer.append("<wlotno>");
            buffer.append(tran.getRIT_WLOTNO());
            buffer.append("</wlotno>");

            buffer.append("<lotrefid>");
            buffer.append(tran.getRIT_LOTREFID());
            buffer.append("</lotrefid>");

            buffer.append("<lotno>");
            buffer.append(tran.getRIT_LOTNO());
            buffer.append("</lotno>");

            buffer.append("<umfact>");
            buffer.append(tran.getRIT_UMFACT());
            buffer.append("</umfact>");

            buffer.append("<weight>");
            buffer.append(tran.getRIT_WEIGHT());
            buffer.append("</weight>");

            buffer.append("<countryid>");
            buffer.append(tran.getRIT_COUNTRYID());
            buffer.append("</countryid>");

            buffer.append("<vendno>");
            buffer.append(tran.getRIT_VENDNO());
            buffer.append("</vendno>");

            buffer.append("<setid>");
            buffer.append(tran.getRIT_SETID());
            buffer.append("</setid>");

            buffer.append("<cost>");
            if(tran.getRIT_COST()!=null){
                buffer.append(tran.getRIT_COST());
            }else {
                buffer.append("0");
            }
            buffer.append("</cost>");

            buffer.append("<binno>");
            buffer.append(tran.getRIT_BINNO());
            buffer.append("</binno>");

            buffer.append("<slot>");
            buffer.append(tran.getRIT_SLOT());
            buffer.append("</slot>");

            buffer.append("<origtranln>");
            buffer.append(tran.getRIT_ORIGTRANLN());
            buffer.append("</origtranln>");

            buffer.append("<Lotexpl>");
            buffer.append(tran.getRIT_LOTEXPL());
            buffer.append("</Lotexpl>");

            buffer.append("<Linesplit>");
            buffer.append(tran.getRIT_LINESPLIT());
            buffer.append("</Linesplit>");

            buffer.append("<updflag>");
            buffer.append(tran.getRIT_UPDFLAG());
            buffer.append("</updflag>");

            buffer.append("<addflag>");
            buffer.append(tran.getRIT_ADDFLAG());
            buffer.append("</addflag>");

            Double diffTrkQty = 0.0;
            if(Double.parseDouble(tran.getRIT_TRKQTYPK()) > Double.parseDouble(tran.getRIT_QTYUSED())){
                diffTrkQty = Double.parseDouble(tran.getRIT_TRKQTYPK()) - Double.parseDouble(tran.getRIT_QTYUSED());
            }else{
                diffTrkQty = Double.parseDouble(tran.getRIT_QTYUSED()) - Double.parseDouble(tran.getRIT_TRKQTYPK());
            }

            String difQty = String.valueOf(diffTrkQty).split("\\.")[0];


            if(tran.getRIT_TRANLINENO().contains("-")){
                buffer.append("<allocqty>");
               // buffer.append(difQty);
                buffer.append(tran.getRIT_QTYUSED());
                buffer.append("</allocqty>");
            }else {
                if((Double.parseDouble(tran.getRIT_TRKQTYPK()))>Double.parseDouble(tran.getRIT_QTYUSED())&&Double.parseDouble(tran.getRIT_TRKQTYPK())>0){
                    buffer.append("<allocqty>");
                    buffer.append("-"+ difQty);
                    buffer.append("</allocqty>");
                    /*buffer.append("<allocqty>");
                    buffer.append("-"+tran.getRIT_ALLOCQTY());
                    buffer.append("</allocqty>");*/
                }else {
                    buffer.append("<allocqty>");
                    buffer.append(difQty);
                    buffer.append("</allocqty>");
                   /* buffer.append("<allocqty>");
                    buffer.append(tran.getRIT_ALLOCQTY());
                    buffer.append("</allocqty>");*/
                }

                /*if((Double.parseDouble(tran.getRIT_TRKQTYPK())+Double.parseDouble(tran.getRIT_REMARKS()))<Double.parseDouble(tran.getRIT_QTYUSED())&&Double.parseDouble(tran.getRIT_TRKQTYPK())>0){
                    buffer.append("<allocqty>");
                    buffer.append("-"+ difQty);
                    buffer.append("</allocqty>");
                    *//*buffer.append("<allocqty>");
                    buffer.append("-"+tran.getRIT_ALLOCQTY());
                    buffer.append("</allocqty>");*//*
                }else {
                     buffer.append("<allocqty>");
                    buffer.append(difQty);
                    buffer.append("</allocqty>");
                   *//* buffer.append("<allocqty>");
                    buffer.append(tran.getRIT_ALLOCQTY());
                    buffer.append("</allocqty>");*//*
                }*/
            }

            buffer.append("<tempalloc>");
            buffer.append(tran.getRIT_TEMPALLOC());
            buffer.append("</tempalloc>");

            buffer.append("</RepackIngredient>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
