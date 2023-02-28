package com.silvercreek.wmspickingclient.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.silvercreek.wmspickingclient.R;
import com.silvercreek.wmspickingclient.database.WMSDbHelper;
import com.silvercreek.wmspickingclient.model.RepackList;
import com.silvercreek.wmspickingclient.model.SalesOrderPickList;
import com.silvercreek.wmspickingclient.util.Supporter;

import java.util.ArrayList;
import java.util.List;

public class SalesOrderListAdapter extends ArrayAdapter<SalesOrderPickList> {

    private final List<SalesOrderPickList> list;
    private final Activity context;
    private Supporter mSupporter;
    private WMSDbHelper mDbHelper;
    private ArrayList<RepackList> trepackList=null;
    public Double grtrepackG_Qty = 0.0;


    static class ViewHolder {

        protected TextView tvOrderNo;
        protected TextView tvDate;
        protected TextView tvCustNo;
        protected TextView tvTotal;
    }

    public SalesOrderListAdapter(Activity context, List<SalesOrderPickList> list) {
        super(context, R.layout.adapter_salesorder_picklist, list);
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
            view = inflator.inflate(R.layout.adapter_salesorder_picklist, null);


            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.tvOrderNo = (TextView) view
                    .findViewById(R.id.tvOrderNo);
            viewHolder.tvDate  = (TextView) view
                    .findViewById(R.id.tvDate);
            viewHolder.tvCustNo = (TextView) view
                    .findViewById(R.id.tvCustNo);
            viewHolder.tvTotal = (TextView) view
                    .findViewById(R.id.tvTotal);
            view.setTag(viewHolder);
        } else {
            view = convertView;

        }

        ViewHolder holder = (ViewHolder) view.getTag();

        holder.tvOrderNo.setText(list.get(position).getSono());
        holder.tvDate.setText(list.get(position).getSodate());
        holder.tvCustNo.setText(list.get(position).getCustno());
        holder.tvTotal.setText(list.get(position).getOrdamt());

        return view;
    }
}
