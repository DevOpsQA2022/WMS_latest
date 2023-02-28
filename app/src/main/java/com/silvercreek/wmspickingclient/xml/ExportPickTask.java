package com.silvercreek.wmspickingclient.xml;

import android.app.Activity;

import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.model.picktaskheader;
import com.silvercreek.wmspickingclient.util.Globals;

public class ExportPickTask {

    public StringBuffer writeXml(picktaskdetail tran, StringBuffer buffer, Activity activity, WMSDbHelper mDBHelper) {
        try {
            buffer.append("<PickTask>");

            buffer.append("<Taskno>");
            if(Globals.gTaskNo !=null){
                buffer.append(Globals.gTaskNo);
            }
            buffer.append("</Taskno>");

            buffer.append("<Tranlineno>");
            if(tran.getTranlineno()!=null){
                buffer.append(tran.getTranlineno());
            }
            buffer.append("</Tranlineno>");

            buffer.append("<Origtranln>");
            if(tran.getorgTranlineno()!=null){
                buffer.append(tran.getorgTranlineno());
            }else {
                buffer.append(tran.getSubTranNo());
            }
            buffer.append("</Origtranln>");

            buffer.append("<Doctype>");
            if(tran.getDoctype()!=null){
                buffer.append(tran.getDoctype());
            }
            buffer.append("</Doctype>");

            buffer.append("<Docno>");
            if(tran.getDoctype()!=null){
                buffer.append(tran.getDocno());
            }
            buffer.append("</Docno>");

            buffer.append("<Doclineno>");
            if(tran.getDoclineno()!=null){
                buffer.append(tran.getDoclineno());
            }
            buffer.append("</Doclineno>");

            buffer.append("<Origdocln>");
            if(tran.getorgDoclineno()!=null){
                buffer.append(tran.getorgDoclineno());
            }else {
                buffer.append(tran.getSubTranNo());
            }
            buffer.append("</Origdocln>");

            buffer.append("<Docstat>");
            if(tran.getDocstat()!=null){
                buffer.append(tran.getDocstat());
            }
            buffer.append("</Docstat>");

            buffer.append("<Route>");
            if(Globals.gRoute !=null){
                buffer.append(Globals.gRoute);
            }
            buffer.append("</Route>");

            buffer.append("<Stop>");
            if(Globals.gStop !=null){
                buffer.append(Globals.gStop);
            }
            buffer.append("</Stop>");

            buffer.append("<Item>");
            if(tran.getItem()!=null){
                buffer.append(tran.getItem());
            }
            buffer.append("</Item>");

            buffer.append("<Loctid>");
            if(Globals.gLoctid!=null){
                buffer.append(Globals.gLoctid);
            }
            buffer.append("</Loctid>");

            buffer.append("<Wlotno>");
            if(tran.getLotNo()!=null){
                buffer.append(tran.getWLotNo());
            }
            buffer.append("</Wlotno>");

            buffer.append("<Lotrefid>");
            if(tran.getLotNo()!=null){
                buffer.append(tran.getLotNo());
            }
            buffer.append("</Lotrefid>");

            buffer.append("<Palno>");
            if(Globals.gPickTaskPallet!=null){
                buffer.append(Globals.gPickTaskPallet);
            }
            buffer.append("</Palno>");

            buffer.append("<Stkumid>");
            if(tran.getStkumid()!=null){
                buffer.append(tran.getStkumid());
            }
            buffer.append("</Stkumid>");

            buffer.append("<Umeasur>");
            if(tran.getUom()!=null){
                buffer.append(tran.getUom());
            }
            buffer.append("</Umeasur>");

            buffer.append("<Umfact>");
            if(tran.getUmfact()!=null){
                buffer.append(tran.getUmfact());
            }else {
                buffer.append("0");
            }
            buffer.append("</Umfact>");

            buffer.append("<Tqtyreq>");
            if(tran.getTQty()!=null){
               // buffer.append(tran.getTQty());
                buffer.append(tran.getorgTQty());
            }else {
                buffer.append("0");
            }
            buffer.append("</Tqtyreq>");

            buffer.append("<Orgqty>");
            if(tran.getorgTQty()!=null){
                buffer.append(tran.getorgTQty());
            }else {
                buffer.append("0");
            }
            buffer.append("</Orgqty>");

            buffer.append("<Tqtypicked>");
            if(tran.getTQty()!=null){
                if (!tran.getFlag().equals("N")){
                    buffer.append(tran.getTQty());
                }else {
                    buffer.append("0");
                }

            }else {
                buffer.append("0");
            }
            buffer.append("</Tqtypicked>");

            buffer.append("<Trkqtyreq>");
            if(tran.getTrkQty()!=null){
                buffer.append(tran.getTrkQty());
            }else {
                buffer.append("0");
            }
            buffer.append("</Trkqtyreq>");

            buffer.append("<Trkqtypk>");
            if(tran.getTrkQty()!=null){
                buffer.append(tran.getTrkQty());
            }else {
                buffer.append("0");
            }
            buffer.append("</Trkqtypk>");

            buffer.append("<Tshipped>");
            if(tran.getTshipped()!=null && Globals.FROMBTNDONE && tran.getFlag().equals("Y")){
                String extensionRemoved = tran.getTQty().split("\\.")[0];
                buffer.append(extensionRemoved);
               // buffer.append(tran.getTQty());
              //  buffer.append(tran.getTshipped());
            }else {
                buffer.append("");
                //buffer.append("0");
            }
            buffer.append("</Tshipped>");

            buffer.append("<Trkshipped>");
            if(tran.getTrkshipped()!=null && Globals.FROMBTNDONE){
                buffer.append(tran.getTrkshipped());
            }else {
                buffer.append("");
               // buffer.append("0");
            }
            buffer.append("</Trkshipped>");

            buffer.append("<Lbshp>");
            if(tran.getLbshp()!=null){
                buffer.append(tran.getLbshp());
            }else {
                buffer.append("0");
            }
            buffer.append("</Lbshp>");

            buffer.append("<Weight>");
            if(tran.getWeight()!=null){
                buffer.append(tran.getWeight());
            }else {
                buffer.append("0");
            }
            buffer.append("</Weight>");

            buffer.append("<Volume>");
            if(tran.getVolume()!=null){
                buffer.append(tran.getVolume());
            }else {
                buffer.append("0");
            }
            buffer.append("</Volume>");

            buffer.append("<Subitem>");

            buffer.append("</Subitem>");

            buffer.append("<Pkdur>");
                buffer.append(tran.getpickDuration());
            buffer.append("</Pkdur>");

            buffer.append("<Pktime>");
            if(Globals.gPkTime!=null){
                buffer.append(Globals.gPkTime);
            }
            buffer.append("</Pktime>");

            buffer.append("<Pickseq>");
            buffer.append("0");
            buffer.append("</Pickseq>");

            buffer.append("<Pickord>");
            buffer.append("0");
            buffer.append("</Pickord>");

            buffer.append("<Trackwt>");

            buffer.append("</Trackwt>");

            buffer.append("<Catchwt>");
            if(tran.getCatchwt()!=null){
                buffer.append(tran.getCatchwt());
            }
            buffer.append("</Catchwt>");

            buffer.append("<Slot>");
            if(tran.getSlot()!=null){
                buffer.append(tran.getSlot());
            }
            buffer.append("</Slot>");

            buffer.append("<Pckdesc>");
            buffer.append("</Pckdesc>");

            buffer.append("<Tmpltid>");
            buffer.append("</Tmpltid>");

            buffer.append("<Lotexpl>");
            buffer.append("0");
            buffer.append("</Lotexpl>");

            buffer.append("<Linesplit>");
            if(tran.getLinesplit()!=null){
                buffer.append(tran.getLinesplit());
            }
            buffer.append("</Linesplit>");

            buffer.append("<Stgslot>");
            if(tran.getStagingSlot()!=null){
                buffer.append(tran.getStagingSlot());
            }
            /*if(Globals.gStgslot!=null){
                buffer.append(Globals.gStgslot);
            }*/
            buffer.append("</Stgslot>");

            buffer.append("<OrgSOItem>");
            if(tran.getOrgSOItem()!=null){
                buffer.append(tran.getOrgSOItem());
            }
            buffer.append("</OrgSOItem>");

            buffer.append("<SubItemNumber>");
            if(tran.getSubItem()!=null){
               /* buffer.append(tran.getSubItem());*/
                buffer.append("");
            }
            buffer.append("</SubItemNumber>");

            buffer.append("<SubTranNum>");
            /*if(tran.getSubTranNo()!=null){
                buffer.append(tran.getSubTranNo());
            }else {
                buffer.append("0");
            }*/
            buffer.append("0");
            buffer.append("</SubTranNum>");

            buffer.append("<IsSubItem>");
            if(tran.getIsSubItem()!=null){
                buffer.append(tran.getIsSubItem());
            }else {
                buffer.append("False");
            }
            buffer.append("</IsSubItem>");

            buffer.append("<diffQTY>");
            if(tran.getSubItem()!=null){
                if(tran.getSubItem().equals("")){
                    buffer.append("0.0");
                }else {
                    buffer.append(tran.getSubItem());
                }
            }else {
                buffer.append("0.0");
            }
            buffer.append("</diffQTY>");

            buffer.append("<IsEdited>");
            if(tran.getIsedited()!=null){
                buffer.append(tran.getIsedited());
            }else {
                buffer.append("N");
            }

            buffer.append("</IsEdited>");

            buffer.append("<Chgqty>");
            if(tran.getChgQty()!=null ){
                buffer.append(tran.getChgQty());
            }else {
                buffer.append("");
            }
            buffer.append("</Chgqty>");

            buffer.append("<PickStatus>");
            if(Globals.FROMBTNDONE){
                buffer.append("PICKED");
            }else {
                buffer.append("ONHOLD");
            }
            buffer.append("</PickStatus>");

            buffer.append("<Picked>");
            if(tran.getPicked()!= null || !tran.getPicked().equals("")){
                buffer.append(tran.getPicked());
            }else {
                buffer.append("N");
            }
            buffer.append("</Picked>");

            buffer.append("</PickTask>");

            return buffer;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
