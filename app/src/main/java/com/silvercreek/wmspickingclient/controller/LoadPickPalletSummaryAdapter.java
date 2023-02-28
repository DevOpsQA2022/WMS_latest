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
import com.silvercreek.wmspickingclient.model.loadpickpalletSummary;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class LoadPickPalletSummaryAdapter extends ArrayAdapter<loadpickpalletSummary> {

    private final List<loadpickpalletSummary> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<loadpickpalletSummary> tloadpickpalletSummary=null;
    private String strFlag="Y";

    static class ViewHolder {
        protected TextView tvTruck;
        protected TextView tvDock;
        protected TextView tvRoutes;
        protected TextView tvStop;
        protected TextView tvPallet;
    }

    public LoadPickPalletSummaryAdapter(Activity context, List<loadpickpalletSummary> list) {
        super(context, R.layout.adapter_loadpickpalletsummary, list);
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
            view = inflator.inflate(R.layout.adapter_loadpickpalletsummary, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            //Completed TASK background color change code
            mDbHelper.openReadableDatabase();
            tloadpickpalletSummary = mDbHelper.getCompletedLPPSummary(strFlag);
            mDbHelper.closeDatabase();
            if(tloadpickpalletSummary.size()!=0){
                for(int i=0;i<tloadpickpalletSummary.size();i++){
                    int pos = Integer.valueOf(tloadpickpalletSummary.get(i).getrowNo())-1;

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvTruck = (TextView) view
                    .findViewById(R.id.tvTruck);
            viewHolder.tvDock = (TextView) view
                    .findViewById(R.id.tvDock);
            viewHolder.tvRoutes  = (TextView) view
                    .findViewById(R.id.tvRoutes);
            viewHolder.tvStop = (TextView) view
                    .findViewById(R.id.tvStop);
            viewHolder.tvPallet = (TextView) view
                    .findViewById(R.id.tvPallet);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            mDbHelper.openReadableDatabase();
            tloadpickpalletSummary = mDbHelper.getCompletedLPPSummary(strFlag);
            mDbHelper.closeDatabase();
            if(tloadpickpalletSummary.size()!=0){
                for(int i=0;i<tloadpickpalletSummary.size();i++){
                    int pos = Integer.valueOf(tloadpickpalletSummary.get(i).getrowNo())-1;
                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();
        String Truck = list.get(position).getTruck();
        holder.tvTruck.setText(Truck);
        holder.tvDock.setText(list.get(position).getDock());
        holder.tvRoutes.setText(list.get(position).getRoutecnt());
        holder.tvStop.setText(list.get(position).getStopcnt());
        holder.tvPallet.setText(list.get(position).getPalcnt());
        return view;
    }
}
