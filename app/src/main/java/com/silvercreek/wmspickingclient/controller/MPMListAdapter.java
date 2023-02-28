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
import com.silvercreek.wmspickingclient.model.MoveManuallyTransaction;
import com.silvercreek.wmspickingclient.model.receivetasklist;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.List;

public class MPMListAdapter extends ArrayAdapter<MoveManuallyTransaction> {

    private final List<MoveManuallyTransaction> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private LayoutInflater inflator;

    static class ViewHolder {
        protected TextView qty;
        protected TextView lot;
        protected TextView UOM;
        protected TextView fromSlot;
        protected TextView desc;
    }

    public MPMListAdapter(Activity context, List<MoveManuallyTransaction> list) {
        super(context, R.layout.adapter_mpm_list, list);
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
            view = inflator.inflate(R.layout.adapter_mpm_list, null);

            if (position % 2 == 1) {
                view.setBackgroundColor(Color.parseColor("#d0d8e8"));
            } else {
                    view.setBackgroundColor(Color.parseColor("#e9edf4"));
            }

            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.qty = (TextView) view
                    .findViewById(R.id.tvMMQty);
            viewHolder.lot = (TextView) view
                    .findViewById(R.id.tvMMLot);
            viewHolder.UOM = (TextView) view
                    .findViewById(R.id.tvMMUOM);
            viewHolder.fromSlot = (TextView) view
                    .findViewById(R.id.tvMMFromSlot);
            viewHolder.desc = (TextView) view
                    .findViewById(R.id.tvMMDesc);
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

        holder.qty.setText(String.valueOf(Math.round(Double.valueOf(list.get(position).getMmTranEqty()))));
        //holder.qty.setText(list.get(position).getMmTranEqty());
        holder.lot.setText(list.get(position).getMmTranLotrefid());
        holder.UOM.setText(list.get(position).getMmTranUOM());
        holder.fromSlot.setText(list.get(position).getMmTranSlot());
        holder.desc.setText(list.get(position).getMmTranItmDesc());
        return view;
    }
}
