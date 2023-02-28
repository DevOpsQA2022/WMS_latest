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
import com.silvercreek.wmspickingclient.model.receivetaskdetail;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ReceiveTaskAdapter extends ArrayAdapter<receivetaskdetail> {

    private final List<receivetaskdetail> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<receivetaskdetail> treceivetaskdetail=null;
    private String strFlag="Y";

    static class ViewHolder {
        protected TextView actQty;
        protected TextView tvQty;
        protected TextView tvUom;
        protected TextView tvItem;
        protected TextView tvDesc;
        protected TextView tvPalno;
        protected TextView tvSlot;
    }

    public ReceiveTaskAdapter(Activity context, List<receivetaskdetail> list) {
        super(context, R.layout.adapter_receivetask, list);
        this.context = context;
        this.list = list;
        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String catchwt="", isCompleted="N", isPartial="N";
        double tqtyrec=0.0, trkqtyrec=0.0, tqtyinc=0.0;
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.adapter_receivetask, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            //Completed TASK background color change code
            mDbHelper.openReadableDatabase();
            treceivetaskdetail = mDbHelper.getCompletedRTTrans(strFlag);
            mDbHelper.closeDatabase();
            if(treceivetaskdetail.size()!=0){
                for(int i=0;i<treceivetaskdetail.size();i++){
                    isCompleted="N"; isPartial="N";
                   /* int pos = Integer.valueOf(treceivetaskdetail.get(i).getrowNo())-1;*/
                    int pos = Integer.valueOf(treceivetaskdetail.get(i).gettranlineno())-1;

                    tqtyinc = Double.valueOf(treceivetaskdetail.get(i).gettqtyinc());
                    tqtyrec = Double.valueOf(treceivetaskdetail.get(i).gettqtyrec());
                    trkqtyrec = Double.valueOf(treceivetaskdetail.get(i).gettrkqtyrec());
                    catchwt = treceivetaskdetail.get(i).getcatchwt();
                    if( trkqtyrec  <= tqtyrec){
                        isCompleted="Y";
                    } else if(tqtyrec > 0){
                        isPartial="Y";
                    }
                    /*if (catchwt.equals("0")){
                        if(tqtyinc <= tqtyrec){
                            isCompleted="Y";
                        } else if(tqtyrec > 0){
                            isPartial="Y";
                        }
                    } else{
                        if(tqtyinc <= trkqtyrec){
                            isCompleted="Y";
                        } else if(trkqtyrec > 0){
                            isPartial="Y";
                        }
                    }*/
                    if (isCompleted.equals("Y")){
                        if(position==pos){
                            view.setBackgroundColor(Color.parseColor("#00FF00")); //Green color
                        }
                    } else if(isPartial.equals("Y")){
                        if(position==pos){
                            view.setBackgroundColor(Color.parseColor("#ffff4b")); //Yellow color
                        }
                    }
                }
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.actQty = (TextView) view
                    .findViewById(R.id.actQty);
            viewHolder.tvQty = (TextView) view
                    .findViewById(R.id.tvQty);
            viewHolder.tvUom  = (TextView) view
                    .findViewById(R.id.tvUom);
            viewHolder.tvItem = (TextView) view
                    .findViewById(R.id.tvItem);
            viewHolder.tvDesc = (TextView) view
                    .findViewById(R.id.tvDesc);
            viewHolder.tvPalno = (TextView) view.findViewById(R.id.tvPalno);
            viewHolder.tvSlot = (TextView) view.findViewById(R.id.tvSlot);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            mDbHelper.openReadableDatabase();
            treceivetaskdetail = mDbHelper.getCompletedRTTrans(strFlag);
            mDbHelper.closeDatabase();
            if(treceivetaskdetail.size()!=0){
                for(int i=0;i<treceivetaskdetail.size();i++){
                    isCompleted="N"; isPartial="N";

                    //int pos = Integer.valueOf(treceivetaskdetail.get(i).getrowNo())-1;
                    int pos = Integer.valueOf(treceivetaskdetail.get(i).gettranlineno())-1;
                    tqtyinc = Double.valueOf(treceivetaskdetail.get(i).gettqtyinc());
                    tqtyrec = Double.valueOf(treceivetaskdetail.get(i).gettqtyrec());
                    trkqtyrec = Double.valueOf(treceivetaskdetail.get(i).gettrkqtyrec());
                    catchwt = treceivetaskdetail.get(i).getcatchwt();
                    if( trkqtyrec  <= tqtyrec){
                        isCompleted="Y";
                    } else if(tqtyrec > 0){
                        isPartial="Y";
                    }
                    /*if (catchwt.equals("0")){
                        if(tqtyinc <= tqtyrec){
                            isCompleted="Y";
                        } else if(tqtyrec > 0){
                            isPartial="Y";
                        }
                    } else{
                        if(tqtyinc <= trkqtyrec){
                            isCompleted="Y";
                        } else if(trkqtyrec > 0){
                            isPartial="Y";
                        }
                    }*/
                    if (isCompleted.equals("Y")){
                        if(position==pos){
                            view.setBackgroundColor(Color.parseColor("#00FF00")); //Green color
                        }
                    } else if(isPartial.equals("Y")){
                        if(position==pos){
                            view.setBackgroundColor(Color.parseColor("#ffff4b")); //Yellow color
                        }
                    }


                }
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        String displayQty="";
        double qty = Double.parseDouble(list.get(position).gettqtyinc());
        double updatedQty = Double.parseDouble(list.get(position).gettqtyrec());
        if(updatedQty>0){
            displayQty=String.valueOf(updatedQty);
        } else {
            displayQty=String.valueOf(qty);
        }

        String decnum = list.get(position).getdecnum();

        String cQty = mDbHelper.DecimalFractionConversion(displayQty, decnum);


        holder.actQty.setText(String.valueOf(Math.round(Double.valueOf(qty))));
       // holder.tvQty.setText(String.valueOf(Math.round(Double.valueOf(cQty))));
        holder.tvUom.setText(list.get(position).getumeasur());
        holder.tvItem.setText(list.get(position).getitem());
        holder.tvDesc.setText(list.get(position).getitmdesc());
        holder.tvPalno.setText(list.get(position).getPalno());
       // if(isCompleted.equals("Y") ||isPartial.equals("Y")) {
        if(!(Double.parseDouble(list.get(position).gettqtyrec()) <= 0)) {
            holder.tvSlot.setText(list.get(position).getcollection());
        }else {
            holder.tvSlot.setText("");
        }
        if(!(Double.parseDouble(list.get(position).gettqtyrec()) <= 0)) {
            holder.tvQty.setText(String.valueOf(Math.round(Double.valueOf(cQty))));
        }else {
            holder.tvQty.setText("0");
        }
        return view;
    }
}
