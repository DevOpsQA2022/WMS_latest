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
import com.silvercreek.wmspickingclient.model.picktasklist;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.List;

public class PickTaskListAdapter extends ArrayAdapter<picktasklist> {

    private final List<picktasklist> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;

    static class ViewHolder {
        protected TextView TaskNo;
        protected TextView Date;
        protected TextView Route;
       // protected TextView Stop;
        protected TextView Status;
        protected TextView DocNo;


    }

    public PickTaskListAdapter(Activity context, List<picktasklist> list) {
        super(context, R.layout.adapter_picktasklist, list);
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
        String soNos1="";
        if (convertView == null) {
            view = inflator.inflate(R.layout.adapter_picktasklist, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.TaskNo = (TextView) view
                    .findViewById(R.id.tvTask);
            viewHolder.Date = (TextView) view.findViewById(R.id.tvDate);
            viewHolder.Route = (TextView) view.findViewById(R.id.tvRoute);
            //viewHolder.Stop  = (TextView) view.findViewById(R.id.tvStop);
            viewHolder.Status = (TextView) view.findViewById(R.id.tvStatus);
            viewHolder.DocNo = (TextView) view.findViewById(R.id.tvDocNo);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.TaskNo.setText(list.get(position).getTaskNo());
        holder.Route.setText(list.get(position).getRoute());
        holder.Date.setText(list.get(position).getDate());
        holder.Status.setText(list.get(position).getStatus());
        String soNos=list.get(position).getSonos();
        if (soNos.endsWith(",")) {
         soNos1 = soNos.substring(0, soNos.length() - 1);
        }
        holder.DocNo.setText(soNos1);

        return view;
    }
}
