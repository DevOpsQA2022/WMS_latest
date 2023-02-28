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
import com.silvercreek.wmspickingclient.model.physicalcountDetail;
import com.silvercreek.wmspickingclient.model.picktaskdetail;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class PhysicalCountDetailAdapter extends ArrayAdapter<physicalcountDetail> {

    private final List<physicalcountDetail> list;
    private final Activity context;
    private Supporter mSupporter;
    private ArrayList<physicalcountDetail> tphysicalcountDetail=null;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;
    private String strFlag="Y";

    static class ViewHolder {
        protected TextView Item;
        protected TextView Lotno;
        protected TextView Umeasur;
        protected TextView Counted;
        protected TextView Descrip;

    }

    public PhysicalCountDetailAdapter(Activity context, List<physicalcountDetail> list) {
        super(context, R.layout.adapter_physicalcountdetail, list);
        this.context = context;
        this.list = list;
        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
        this.inflator = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            view = inflator.inflate(R.layout.adapter_physicalcountdetail, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
            //Completed Physical Count background color change code
            /*mDbHelper.openReadableDatabase();
            tphysicalcountDetail = mDbHelper.getCompletedPCTrans(strFlag);
            mDbHelper.closeDatabase();
            if(tphysicalcountDetail.size()!=0){
                for(int i=0;i<tphysicalcountDetail.size();i++){
                    int pos = Integer.parseInt(tphysicalcountDetail.get(i).getRowNo())-1;
                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }*/
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.Item = (TextView) view
                    .findViewById(R.id.tvItem);
            viewHolder.Lotno = (TextView) view
                    .findViewById(R.id.tvLot);
            viewHolder.Umeasur = (TextView) view
                    .findViewById(R.id.tvUmeasur);
            viewHolder.Counted = (TextView) view
                    .findViewById(R.id.tvCounted);
            viewHolder.Descrip = (TextView) view
                    .findViewById(R.id.tvItemDesc);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
            //Completed Physical Count background color change code

        }

        mDbHelper.openReadableDatabase();
        tphysicalcountDetail = mDbHelper.getCompletedPCTrans(strFlag);
        mDbHelper.closeDatabase();
        if(tphysicalcountDetail.size()!=0){
                if(list.get(position).getposted().equals("P")/*|| list.get(position).getwmsstat().equals("C")*/){
                    view.setBackgroundColor(Color.parseColor("#00FF00")); // color green
                }
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        if(list.get(position).getposted().equals("P") || list.get(position).getwmsstat().equals("C")){
            holder.Counted.setText(String.valueOf(Math.round(Double.parseDouble(list.get(position).gettcountqty()))));
        }else if (!list.get(position).getcounted().equals("") && list.get(position).getcounted()!=null){
            holder.Counted.setText(String.valueOf(Math.round(Double.parseDouble(list.get(position).getcounted()))));
        }else {
            holder.Counted.setText(list.get(position).getcounted());

        }

        holder.Item.setText(list.get(position).getitem());
        holder.Lotno.setText(list.get(position).getlotrefid());
        holder.Umeasur.setText(list.get(position).getumeasur());
      //  holder.Counted.setText(list.get(position).getcounted());
        holder.Descrip.setText(list.get(position).getitmdesc());
        return view;
    }
}
