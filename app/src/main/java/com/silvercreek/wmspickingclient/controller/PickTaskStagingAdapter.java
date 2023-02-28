package com.silvercreek.wmspickingclient.controller;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class PickTaskStagingAdapter extends ArrayAdapter<picktaskdetail> {

    private final List<picktaskdetail> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<picktaskdetail> tpicktaskdetail=null;
    private String strFlag="Y";
    private String subFlag="S";

    static class ViewHolder {
        protected TextView tvSlot;
        protected TextView tvQty;
        protected TextView tvUom;
        protected TextView tvItem;
        protected TextView tvLot;
        protected TextView tvDesc;
    }

    public PickTaskStagingAdapter(Activity context, List<picktaskdetail> list) {
        super(context, R.layout.adapter_picktaskdetail, list);
        this.context = context;
        this.list = list;
        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        String exportLotid="";
        int pos=0;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_picktaskdetail, null);


            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            //Completed TASK background color change code
            /*mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedStagingTrans(strFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
            for(int i=0;i<tpicktaskdetail.size();i++){
                int pos = Integer.valueOf(tpicktaskdetail.size())-1;
                if(tpicktaskdetail.get(i).getSubTranNo()!=null){
                    pos2 = Integer.valueOf(tpicktaskdetail.get(i).getSubTranNo())-1;
                }


                if(position==pos){
                    view.setBackgroundColor(Color.parseColor("#00FF00"));
                }else if(position==pos2){
                    view.setBackgroundColor(Color.parseColor("#00FF00"));
                }
            }
            }*/

            mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedStagingTrans(strFlag);
            mDbHelper.closeDatabase();

            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++){

                    mDbHelper.openReadableDatabase();
                    exportLotid = mDbHelper.getExportLotId(tpicktaskdetail.get(i).getWLotNo());
                    mDbHelper.closeDatabase();

                    /*int pos = orgTranCount-1;*/
                    if(Integer.valueOf(exportLotid)>0){
                        pos = Integer.valueOf(exportLotid)-1;
                    }


                    /* int pos = Integer.valueOf(tpicktaskdetail.get(i).getorgDoclineno())-1;
                   if(tpicktaskdetail.get(i).getSubTranNo()!=null){
                        pos2 = Integer.valueOf(tpicktaskdetail.get(i).getSubTranNo())-1;
                    }*/

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }/*else if(position==pos2){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }*/
                    //int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                    /*if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }*/

                }
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvSlot = (TextView) view
                    .findViewById(R.id.tvSlot);
            viewHolder.tvQty = (TextView) view
                    .findViewById(R.id.tvQty);
            viewHolder.tvUom  = (TextView) view
                    .findViewById(R.id.tvUom);
            viewHolder.tvItem = (TextView) view
                    .findViewById(R.id.tvItem);
            viewHolder.tvLot = (TextView) view
                    .findViewById(R.id.tvLot);
            viewHolder.tvDesc = (TextView) view
                    .findViewById(R.id.tvDesc);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            int orgTranCount=0;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            /*mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedStagingTrans(strFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++) {
                    int pos = Integer.valueOf(tpicktaskdetail.size()-1);

                    if (position == pos) {
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }*/
            mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedStagingTrans(strFlag);
            mDbHelper.closeDatabase();

            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++){
                    mDbHelper.openReadableDatabase();
                    exportLotid = mDbHelper.getExportLotId(tpicktaskdetail.get(i).getWLotNo());
                    mDbHelper.closeDatabase();

                    if(Integer.valueOf(exportLotid)>0){
                        pos = Integer.valueOf(exportLotid)-1;
                    }
                    /*int pos = Integer.valueOf(tpicktaskdetail.get(i).getorgDoclineno())-1;
                    if(tpicktaskdetail.get(i).getSubTranNo()!=null){
                        pos2 = Integer.valueOf(tpicktaskdetail.get(i).getSubTranNo())-1;
                    }*/

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }/*else if(position==pos2){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }*/
                    //int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                    /*if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }*/
                }
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        String slot = list.get(position).getSlot();
        String qty = list.get(position).getTQty();
        String decnum = list.get(position).getdecnum();
        if(decnum==null){
            decnum="0";
        }
        String cQty = mDbHelper.DecimalFractionConversion(qty, decnum);

        holder.tvSlot.setText(slot);
        holder.tvQty.setText(cQty);
        holder.tvUom.setText(list.get(position).getUom());
        holder.tvItem.setText(list.get(position).getItem());
        holder.tvLot.setText(list.get(position).getLotNo());
        holder.tvDesc.setText(list.get(position).getDescrip());
        return view;
    }
}
