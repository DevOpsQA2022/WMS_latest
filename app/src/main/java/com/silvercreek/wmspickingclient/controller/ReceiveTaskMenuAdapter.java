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
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class ReceiveTaskMenuAdapter extends ArrayAdapter<receivetasklist> {

    private final List<receivetasklist> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;

    static class ViewHolder {
        protected TextView Type;
        protected TextView Order;
        protected TextView Status;
    }

    public ReceiveTaskMenuAdapter(Activity context, List<receivetasklist> list) {
        super(context, R.layout.adapter_receivetaskmenu, list);
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
            view = inflator.inflate(R.layout.adapter_receivetaskmenu, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.Type = (TextView) view
                    .findViewById(R.id.tvType);
            viewHolder.Order = (TextView) view
                    .findViewById(R.id.tvOrder);
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

        ViewHolder holder = (ViewHolder) view.getTag();
        holder.Type.setText(list.get(position).getDoctype());
        holder.Order.setText(list.get(position).getDocno());
        holder.Status.setText(list.get(position).getStatus());

        return view;
    }
}
