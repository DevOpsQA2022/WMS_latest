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
import com.silvercreek.wmspickingclient.model.physicalcountSlot;
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class PhysicalCountMenuAdapter extends ArrayAdapter<physicalcountSlot> {

    private final List<physicalcountSlot> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;
    private ArrayList<physicalcountSlot> tphysicalcountSlot=null;

    static class ViewHolder {
        protected TextView Slot;
        protected TextView Status;
    }

    public PhysicalCountMenuAdapter(Activity context, List<physicalcountSlot> list) {
        super(context, R.layout.adapter_physicalcountmenu, list);
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
        String Status="";
        View view = null;
        if (convertView == null) {
            view = inflator.inflate(R.layout.adapter_physicalcountmenu, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
            //Completed Physical Count background color change code
            mDbHelper.openReadableDatabase();
            tphysicalcountSlot = mDbHelper.getCompletedPCSLOTTrans();
            mDbHelper.closeDatabase();
            if(tphysicalcountSlot.size()!=0){
                for(int i=0;i<tphysicalcountSlot.size();i++){
                    int pos = Integer.valueOf(tphysicalcountSlot.get(i).getrowno())-1;
                    Status = tphysicalcountSlot.get(i).getstatus();

                    if (Status.equals("COUNTED") || Status.equals("POSTED")){
                        if(position==pos){
                            view.setBackgroundColor(Color.parseColor("#00FF00")); //Green color
                        }
                    } else if(Status.equals("PARTIAL")){
                        if(position==pos){
                            view.setBackgroundColor(Color.parseColor("#ffff4b")); //Yellow color
                        }
                    }
                }
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.Slot = (TextView) view
                    .findViewById(R.id.tvSlot);
            viewHolder.Status = (TextView) view
                    .findViewById(R.id.tvStatus);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
        }
        //Completed Physical Count background color change code
        mDbHelper.openReadableDatabase();
        tphysicalcountSlot = mDbHelper.getCompletedPCSLOTTrans();
        mDbHelper.closeDatabase();
        if(tphysicalcountSlot.size()!=0){
            for(int i=0;i<tphysicalcountSlot.size();i++){
                int pos = Integer.parseInt(tphysicalcountSlot.get(i).getrowno())-1;
                Status = tphysicalcountSlot.get(i).getstatus();

                if (Status.equals("COUNTED") || Status.equals("POSTED")){
                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));//Green color
                    }
                } else if(Status.equals("PARTIAL")){
                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#ffff4b"));//Yellow color
                    }
                }
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.Slot.setText(list.get(position).getslot());
        holder.Status.setText(list.get(position).getstatus());
        return view;
    }
}
