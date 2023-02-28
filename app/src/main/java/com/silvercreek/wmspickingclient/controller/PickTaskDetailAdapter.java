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
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.Globals;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class PickTaskDetailAdapter extends ArrayAdapter<picktaskdetail> {

    private final List<picktaskdetail> list;
    private String taskNum="";
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<picktaskdetail> tpicktaskdetail=null;
    private String strFlag="Y";
    private String subFlag="S";
    public Double cQTy = 0.0;


    static class ViewHolder {
        protected TextView tvSlot;
        protected TextView tvQty;
        protected TextView tvOrdQty;
        protected TextView tvUom;
        protected TextView tvItem;
        protected TextView tvLot;
        protected TextView tvDesc;
    }

    public PickTaskDetailAdapter(Activity context, List<picktaskdetail> list) {
        super(context, R.layout.adapter_picktaskdetail, list);
        this.context = context;
        this.list = list;

        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_picktaskdetail, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            //Completed TASK background color change code


            mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(strFlag,taskNum);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++){
                    int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }

       /* if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_picktaskdetail, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }*/

            //Completed TASK background color change code
          /*  mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(strFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
            for(int i=0;i<tpicktaskdetail.size();i++){
                int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                if(position==pos){
                    view.setBackgroundColor(Color.parseColor("#00FF00"));
                }
            }
            }

            mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(subFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++){
                    int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#FF7F50"));
                    }
                }


            }*/

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvSlot = (TextView) view
                    .findViewById(R.id.tvSlot);
            viewHolder.tvOrdQty = (TextView) view
                    .findViewById(R.id.tvOrdQty);
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
        /*} else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
*/
           /* mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(strFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++) {
                    int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo()) - 1;

                    if (position == pos) {
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }
            mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(subFlag);
            mDbHelper.closeDatabase();
            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++){
                    int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#FF7F50"));
                    }
                }
            }*/
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            mDbHelper.openReadableDatabase();
            tpicktaskdetail = mDbHelper.getCompletedTrans(strFlag,taskNum);
            mDbHelper.closeDatabase();

            if(tpicktaskdetail.size()!=0){
                for(int i=0;i<tpicktaskdetail.size();i++){
                    int pos = Integer.valueOf(tpicktaskdetail.get(i).getrowNo())-1;
                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));   //greenColor
                    }
                }
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        if (list.get(position).getChgQty().equals("Y") && !list.get(position).getDocstat().equals("X") && !list.get(position).getDocstat().equals("V") /*&& list.get(position).getFlag().equals("Y")*/){
            view.setBackgroundColor(Color.parseColor("#ffff4b"));  // ColorYellow
        }
        if(list.get(position).getDocstat().equals("X") || list.get(position).getDocstat().equals("V")){
            view.setBackgroundColor(Color.parseColor("#F44336"));  //ColorRed
        }

        String slot = list.get(position).getLotNo();
        String qty = list.get(position).getTQty();
        String decnum = list.get(position).getdecnum();
        if(decnum==null){
            decnum="0";
        }
        String cQty = mDbHelper.DecimalFractionConversion(qty, decnum);
         cQTy = Double.valueOf(mDbHelper.DecimalFractionConversion(qty, decnum));

       // holder.tvSlot.setText(slot);
        if (list.get(position).getFlag().equals("N")){
            holder.tvQty.setText("0");
        }else {
            holder.tvQty.setText(String.valueOf(Math.round(cQTy)));
        }
        holder.tvOrdQty.setText(String.valueOf(Math.round(Double.parseDouble(list.get(position).getorgTQty()))));

       // holder.tvQty.setText(cQty);
        holder.tvUom.setText(list.get(position).getUom());

        holder.tvItem.setText(list.get(position).getItem());

        if(list.get(position).getDocstat().equals("X")) {
            holder.tvLot.setText("DELETED");
        }else if(list.get(position).getDocstat().equals("V")){
            holder.tvLot.setText("VOID");
        }else{
            holder.tvLot.setText(slot);
        }
       // holder.tvLot.setText(slot);
        holder.tvDesc.setText(list.get(position).getDescrip());
        return view;
    }
}
