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
import com.silvercreek.wmspickingclient.model.loadpickpalletDetails;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class LoadPickPalletDetailAdapter extends ArrayAdapter<loadpickpalletDetails> {

    private final List<loadpickpalletDetails> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<loadpickpalletDetails> tloadpickpalletDetails=null;
    private String strFlag="Y";

    static class ViewHolder {
        protected TextView tvStop;
        protected TextView tvLoaded;
        protected TextView tvReady;
        protected TextView tvTotal;
        protected TextView tvPicker;
        protected TextView tvTask;
    }

    public LoadPickPalletDetailAdapter(Activity context, List<loadpickpalletDetails> list) {
        super(context, R.layout.adapter_loadpickpalletdetail, list);
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
            view = inflator.inflate(R.layout.adapter_loadpickpalletdetail, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            //Completed TASK background color change code
            mDbHelper.openReadableDatabase();
            tloadpickpalletDetails = mDbHelper.getCompletedLPPDetail();
            mDbHelper.closeDatabase();
            if(tloadpickpalletDetails.size()!=0){
                for(int i=0;i<tloadpickpalletDetails.size();i++){
                    int pos = Integer.valueOf(tloadpickpalletDetails.get(i).getrowNo())-1;

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.tvStop = (TextView) view
                    .findViewById(R.id.tvStop);
            viewHolder.tvLoaded = (TextView) view
                    .findViewById(R.id.tvLoaded);
            viewHolder.tvReady  = (TextView) view
                    .findViewById(R.id.tvReady);
            viewHolder.tvTotal = (TextView) view
                    .findViewById(R.id.tvTotal);
            viewHolder.tvPicker = (TextView) view
                    .findViewById(R.id.tvPicker);
            viewHolder.tvTask = (TextView) view
                    .findViewById(R.id.tvTask);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            mDbHelper.openReadableDatabase();
            tloadpickpalletDetails = mDbHelper.getCompletedLPPDetail();
            mDbHelper.closeDatabase();
            if(tloadpickpalletDetails.size()!=0){
                for(int i=0;i<tloadpickpalletDetails.size();i++){
                    int pos = Integer.valueOf(tloadpickpalletDetails.get(i).getrowNo())-1;

                    if(position==pos){
                        view.setBackgroundColor(Color.parseColor("#00FF00"));
                    }
                }
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        String stop = list.get(position).getStop();

        holder.tvStop.setText(stop);
        holder.tvLoaded.setText(list.get(position).getLoaded());
        holder.tvReady.setText(list.get(position).getReady());
        holder.tvTotal.setText(list.get(position).getTotal());
        holder.tvPicker.setText(list.get(position).getPicker());
        holder.tvTask.setText(list.get(position).getTaskno());
        return view;
    }
}
