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
import com.silvercreek.wmspickingclient.model.MoveTaskSlotList;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.List;

public class MoveTaskSlotListAdapter extends ArrayAdapter<MoveTaskSlotList> {

    private final List<MoveTaskSlotList> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;



    static class ViewHolder {

        protected TextView tvSlotList;

    }

    public MoveTaskSlotListAdapter(Activity context, List<MoveTaskSlotList> list) {
        super(context, R.layout.adapter_receive_slotlist, list);
        this.context = context;
        this.list = list;
        mDbHelper = new WMSDbHelper(context);
        mSupporter = new Supporter(context, mDbHelper);
        this.inflator = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            /*LayoutInflater inflator = context.getLayoutInflater();*/
            view = inflator.inflate(R.layout.adapter_receive_slotlist, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvSlotList = (TextView) view
                    .findViewById(R.id.tvSlotList);
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

        holder.tvSlotList.setText(list.get(position).getSlot());

        return view;
    }
}
