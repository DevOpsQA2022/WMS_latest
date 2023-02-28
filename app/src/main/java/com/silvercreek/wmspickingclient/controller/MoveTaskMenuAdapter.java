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
import com.silvercreek.wmspickingclient.model.MoveTaskHeader;
import com.silvercreek.wmspickingclient.model.Movetasklist;
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.List;

public class MoveTaskMenuAdapter extends ArrayAdapter<Movetasklist> {


    private final List<Movetasklist> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;

    static class ViewHolder {
        protected TextView Type;
        protected TextView Task;
        protected TextView Status;
    }

    public MoveTaskMenuAdapter(Activity context, List<Movetasklist> list) {
        super(context, R.layout.adapter_movetask_layout, list);
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
            view = inflator.inflate(R.layout.adapter_movetask_layout, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            final MoveTaskMenuAdapter.ViewHolder viewHolder = new MoveTaskMenuAdapter.ViewHolder();

            viewHolder.Type = (TextView) view
                    .findViewById(R.id.move_type);
            viewHolder.Task = (TextView) view
                    .findViewById(R.id.move_task);
            viewHolder.Status = (TextView) view
                    .findViewById(R.id.move_status);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }
        }

        MoveTaskMenuAdapter.ViewHolder holder = (MoveTaskMenuAdapter.ViewHolder) view.getTag();

        holder.Type.setText(list.get(position).getTaskType());
        holder.Task.setText(list.get(position).getTaskNo());
        holder.Status.setText(list.get(position).getStatus());
        return view;
    }

}
